// lib/services/sse_web.dart
// Sólo se importa en Web (flutter web).
/*
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
}*/

/*
import 'dart:async';
import 'dart:html';

/// Usa EventSource en web
Stream<String> streamWithEventSource(String url) {
  final controller = StreamController<String>();
  final source = EventSource(url);

  source.onMessage.listen((msg) {
    controller.add(msg.data);
  }, onError: (err) {
    controller.addError(err);
  });

  controller.onCancel = () {
    source.close();
    controller.close();
  };

  return controller.stream;
}*/
// lib/services/sse_web.dart
import 'dart:async';
import 'dart:html';

/// Stream SSE de logs en Web, usando EventSource nativo.
Stream<String> streamSse(String url) {
  final controller = StreamController<String>();
  final source     = EventSource(url);

  source.onMessage.listen((msg) {
    controller.add(msg.data);
  }, onError: (err) {
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
