// lib/services/sse_mobile.dart
/*
import 'dart:async';
import 'dart:convert';
import 'dart:io';

/// Stream SSE de logs usando HttpClient (móvil / escritorio).
Stream<String> streamWithHttpClient(String url) async* {
  final uri = Uri.parse(url);
  final client = HttpClient();
  final request = await client.getUrl(uri);
  final response = await request.close();

  await for (var chunk in response.transform(utf8.decoder)) {
    for (var line in chunk.split('\n')) {
      line = line.trim();
      if (line.startsWith('data:')) {
        yield line.substring(5).trim();
      }
    }
  }
}
*/

// lib/services/sse_mobile.dart
import 'dart:async';
import 'dart:convert';
import 'dart:io';

/// Stream SSE de logs en IO (móvil / desktop), usando HttpClient.
Stream<String> streamSse(String url) async* {
  final uri    = Uri.parse(url);
  final client = HttpClient();
  final req    = await client.getUrl(uri);
  final resp   = await req.close();

  await for (var chunk in resp.transform(utf8.decoder)) {
    for (var line in chunk.split('\n')) {
      final trimmed = line.trim();
      if (trimmed.startsWith('data:')) {
        yield trimmed.substring(5).trim();
      }
    }
  }
}
