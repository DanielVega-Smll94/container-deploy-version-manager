// lib/services/api_service.dart
import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:http/http.dart' as http;
import '../models/DockerSummary.dart';
import '../models/servidor.dart';

class ServidoresService {
  static final String? _url_api = dotenv.env['API_URL'];
  static final String? _port = dotenv.env['API_PORT'];
  static final String? _web = dotenv.env['API_URL_WEB_LOCAL'];
  static final String? _path = dotenv.env['API_PATH'];
  static final String? _protocol = dotenv.env['API_PROTOCOL'];

  static String get _host {
    if (kIsWeb) return _web!;
    if (Platform.isAndroid) return _url_api!;
    // iOS Simulator también mapea a 'localhost'
    return _url_api!;
  }
  static final _baseUrl = '$_protocol://$_host:$_port$_path';

  static Future<List<Servidor>> fetchServidores() async {
    print(_baseUrl);
    final res = await http.get(Uri.parse('$_baseUrl/servidores/findAllServer/'));
    if (res.statusCode == 200) {
      final List datos = json.decode(res.body);
      return datos.map((e) => Servidor.fromJson(e)).toList();
    } else {
      throw Exception('Error al cargar servidores: ${res.statusCode}');
    }
  }

  static Future<DockerSummary> fetchSummary(int serverId) async {
    final res = await http.get(Uri.parse('$_baseUrl/servers/$serverId/summary'));
    if (res.statusCode == 200) {
      return DockerSummary.fromJson(json.decode(res.body) as Map<String, dynamic>);
    }
    throw Exception('Error fetching summary (${res.statusCode})');
  }
}
