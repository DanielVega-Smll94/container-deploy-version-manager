// lib/services/sse_web.dart
// Sólo se importa en Web (flutter web).

import 'dart:async';
import 'dart:html';

/// Usa el API nativo de EventSource en Web.
Stream<String> streamWithEventSource(String url) {
  final source = EventSource(url);
  final controller = StreamController<String>();

  source.onMessage.listen((msg) {
    controller.add(msg.data);
  });

  source.onError.listen((err) {
    controller.addError('SSE error: $err');
    source.close();
    controller.close();
  });

  controller.onCancel = () {
    source.close();
    controller.close();
  };

  return controller.stream;
}
