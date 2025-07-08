// lib/services/stream_service.dart

import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

import 'package:flutter/foundation.dart';
import 'sse_client.dart';   // export condicional

class StreamService {
  // final _base = 'http://192.168.14.149:8081/api';
  //10.0.2.2
  static String get _host {
    if (kIsWeb) return 'localhost';
    if (Platform.isAndroid) return '192.168.14.149';
    // iOS Simulator también mapea a 'localhost'
    return '192.168.14.149';
  }

  // Y en lugar de hardcodear localhost, usamos nuestra getter:
  final _base = 'http://$_host:8081/api';

  /// SSE unificado para web y móvil:
  /*Stream<String> streamLogsSseSSH({
    required int servidorId,
    required String containerName,
    int tailLines = 200,
  }) {
    final url = '$_base/logs/stream/$servidorId/$containerName'
        '?tailLines=$tailLines';
    return streamSse(url);  // siempre existe, venga de mobile o web
  }*/
  /// Para web → SSE
  /// Para móvil/desktop → GET /ssh/containers/{id}/logs
  Future<Stream<String>> logs({
    required int servidorId,
    required String containerId,
    int tail = 200,
  }) async {
    if (kIsWeb) {
      final url = '$_base/logs/stream/$servidorId/$containerId?tailLines=$tail';
      return streamSse(url);
    } else {
      final url = '$_base/ssh/containers/$containerId/logs'
          '?servidorId=$servidorId&tail=$tail';
      final resp = await http.get(Uri.parse(url));
      if (resp.statusCode != 200) {
        throw Exception('Error cargando logs: ${resp.statusCode}');
      }
      final body = json.decode(resp.body) as Map<String, dynamic>;
      final raw = body['data'];
      Iterable<String> lines;
      if (raw is List) {
        // Si viene lista de líneas
        lines = raw.map((e) => e.toString());
      } else if (raw is String) {
        // Si viene un único String con saltos de línea
        lines = raw.split(RegExp(r'\r?\n'));
      } else {
        throw Exception('Tipo inesperado de data: ${raw.runtimeType}');
      }
      return Stream.fromIterable(lines);
    }
  }
}
