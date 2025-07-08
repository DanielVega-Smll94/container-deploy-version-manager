// lib/services/api_service.dart
import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../models/DockerSummary.dart';
import '../models/servidor.dart';

class ServidoresService {
  static String get _host {
    if (kIsWeb) return 'localhost';
    if (Platform.isAndroid) return '192.168.14.149';
    // iOS Simulator también mapea a 'localhost'
    return '192.168.14.149';
  }
  static final _baseUrl = 'http://$_host:8081/api';

  static Future<List<Servidor>> fetchServidores() async {
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
