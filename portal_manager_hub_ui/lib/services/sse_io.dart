
import 'dart:async';
import 'dart:convert';
import 'dart:io';

/// Stream de SSE usando HttpClient (Android, iOS, Desktop)
Stream<String> streamWithHttpClient(String url) async* {
  final uri = Uri.parse(url);
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
