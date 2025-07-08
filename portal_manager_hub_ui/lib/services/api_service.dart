import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../models/ImagesInfo.dart';
import '../models/container_info.dart';
import 'package:flutter/foundation.dart' show kIsWeb;
import 'sse_web.dart' if (dart.library.io) 'sse_mobile.dart';

class ApiService {
  // final _base = 'http://192.168.14.149:8081/api';
  // Elegimos el host según plataforma:
  static String get _host {
    if (kIsWeb) return 'localhost';
    if (Platform.isAndroid) return '192.168.14.149';
    // iOS Simulator también mapea a 'localhost'
    return '192.168.14.149';
  }

  // Y en lugar de hardcodear localhost, usamos nuestra getter:
  final _base = 'http://$_host:8081/api';

  Future<List<ContainerInfo>> listContainers(int servidorId) async {
    final uri = Uri.parse('$_base/ssh/execute/json');
    final payload = {"servidorId":servidorId,"command":"docker ps -a"};
    //final resp = await http.get(uri);
    final resp = await http.post(uri,
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(payload));
    if (resp.statusCode == 200) {
      final List data = json.decode(resp.body);
      return data.map((e) => ContainerInfo.fromJson(e)).toList();
    }
    throw Exception('Error listContainers: ${resp.statusCode}');
  }

  Future<void> startContainer(int servidorId, String cid) async {
    final uri = Uri.parse('$_base/ssh/containers/$cid/start?servidorId=$servidorId');
    final resp = await http.post(uri);
    if (resp.statusCode >= 400) {
      throw Exception('startContainer: ${resp.body}');
    }
  }

  Future<void> stopContainer(int servidorId, String cid) async {
    final uri = Uri.parse('$_base/ssh/containers/$cid/stop?servidorId=$servidorId');
    final resp = await http.post(uri);
    if (resp.statusCode >= 400) {
      throw Exception('stopContainer: ${resp.body}');
    }
  }

  Future<void> restartContainer(int servidorId, String cid) async {
    final uri = Uri.parse('$_base/ssh/containers/$cid/restart?servidorId=$servidorId');
    final resp = await http.post(uri);
    if (resp.statusCode >= 400) {
      throw Exception('restartContainer: ${resp.body}');
    }
  }

  Future<void> removeContainer(int servidorId, String cid, {bool force = false}) async {
    final uri = Uri.parse(
        '$_base/ssh/containers/$cid?servidorId=$servidorId&force=$force'
    );
    final resp = await http.delete(uri);
    if (resp.statusCode >= 400) {
      throw Exception('removeContainer: ${resp.body}');
    }
  }

  Future<Map<String,String>> pullImage(int servidorId, String imageName) async {
    final uri = Uri.parse('$_base/ssh/images/pull?servidorId=$servidorId&imageName=$imageName');
    final resp = await http.post(uri);
    if (resp.statusCode == 200) {
      return Map<String,String>.from(json.decode(resp.body));
    }
    throw Exception('pullImage: ${resp.body}');
  }

  Future<Map<String,String>> removeImage(int servidorId, String imageName) async {
    final uri = Uri.parse('$_base/ssh/images/$imageName?servidorId=$servidorId');
    final resp = await http.delete(uri);
    if (resp.statusCode == 200) {
      return Map<String,String>.from(json.decode(resp.body));
    }
    throw Exception('removeImage: ${resp.body}');
  }

  /// ---- SSE de logs usando HttpClient ----
  Stream<String> streamLogs({
    required int servidorId,
    required String containerId,
    int tail = 200,
  }) async* {
    final uri = Uri.parse(
        '$_base/ssh/containers/$containerId/logs/stream'
            '?servidorId=$servidorId&tailLines=$tail'
    );

    final client = HttpClient();
    final req = await client.getUrl(uri);
    final resp = await req.close();

    // cada línea de SSE empieza con "data:"
    await for (var chunk in resp.transform(utf8.decoder)) {
      for (var line in chunk.split('\n')) {
        if (line.startsWith('data:')) {
          yield line.substring(5).trim();
        }
      }
    }
  }

  /// Consume SSE de logs desde /api/logs/stream/{servidorId}/{containerName}
  Stream<String> streamLogsSse({
    required int servidorId,
    required String containerName,
    int tailLines = 200,
  }) async* {
    final uri = Uri.parse(
        '$_base/logs/stream/$servidorId/$containerName'
            '?tailLines=$tailLines'
    );
    final client = HttpClient();
    final request = await client.getUrl(uri);
    final response = await request.close();

    await for (var chunk in response.transform(utf8.decoder)) {
      for (var line in chunk.split('\n')) {
        if (line.startsWith('data:')) {
          yield line.substring(5).trim();
        }
      }
    }
  }

  /// --------------------------------------------------
  /// El único método SSE que vas a usar en tu UI:
  ///
  ///   ApiService().streamLogsSse(
  ///     servidorId: ...,
  ///     containerName: ...,
  ///     tailLines: ...
  ///   )
  ///
  ///
  /*Stream<String> streamLogsSseSSH({
    required int servidorId,
    required String containerName,
    int tailLines = 200,
  }) {
    final url = '$_base/logs/stream/$servidorId/$containerName'
        '?tailLines=$tailLines';

    // Si es Web, usa EventSource; si no, HttpClient
    return kIsWeb
        ? streamWithEventSource(url)
        : streamWithHttpClient(url);
  }*/

  Future<List<ImagesInfo>> listImages(int serverId) async {
    final res = await http.get(Uri.parse('$_base/servers/$serverId/images'));
    if (res.statusCode == 200) {
      final List data = json.decode(res.body) as List;
      return data.map((e) => ImagesInfo.fromJson(e)).toList();
    }
    throw Exception('Error ${res.statusCode} al listar imágenes');
  }

  /// Borra una imagen por ID
  Future<void> deleteImage({
    required int serverId,
    String? imageId,
    String? imageName,
  }) async {

    // 1) Montar map de parámetros
    final params = <String, String>{};
    if (imageId != null && imageId.isNotEmpty) {
      params['imageId'] = imageId;
    } else if (imageName != null && imageName.isNotEmpty) {
      params['imageName'] = imageName;
    } else {
      throw Exception('Debe especificar imageId o imageName');
    }

    // 2) Construir Uri con queryParameters
    final uri = Uri.parse('$_base/servers/$serverId/images')
        .replace(queryParameters: params);
    // 3) Llamada DELETE
    final res = await http.delete(uri);
    if (res.statusCode != 204) {
      throw Exception('Error ${res.statusCode} borrando imagen');
    }
  }

}


/*import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import '../models/servidor.dart';

class ApiService {
  final String _baseUrl = 'http://localhost:8081/api';

  Future<List<Servidor>> getServers() async {
    final resp = await http.get(Uri.parse('$_baseUrl/servidores'));
    if (resp.statusCode == 200) {
      final List jsonList = json.decode(resp.body);
      return jsonList.map((e) => Servidor.fromJson(e)).toList();
    }
    throw Exception('Error cargando servidores');
  }

  Future<Servidor> getServerDetail(int id) async {
    final resp = await http.get(Uri.parse('$_baseUrl/servidores/$id'));
    if (resp.statusCode == 200) {
      return Servidor.fromJson(json.decode(resp.body));
    }
    throw Exception('Servidor no encontrado');
  }

  Stream<String> streamLogs({
    required int servidorId,
    required String containerName,
    int tailLines = 200,
  }) async* {
    final uri = Uri.parse(
      '$_baseUrl/logs/stream/$containerName'
      '?servidorId=$servidorId&tailLines=$tailLines',
    );

    final client = HttpClient();
    final req = await client.getUrl(uri);
    final resp = await req.close();

    // SSE viene formateado con líneas que empiezan por "data: "
    await for (var chunk in resp.transform(utf8.decoder)) {
      for (var line in chunk.split('\n')) {
        line = line.trim();
        if (line.startsWith('data:')) {
          yield line.substring(5).trim();
        }
      }
    }
  }
}
*/