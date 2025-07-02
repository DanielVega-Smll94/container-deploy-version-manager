// lib/services/sse_client.dart

/// Este archivo exporta _condicionalmente_ la implementación
/// correcta de `streamSse()` según plataforma.
/// Si estás en Dart VM (móvil/desktop) usará `sse_mobile.dart`.
/// Si estás en Web usará `sse_web.dart`.

export 'sse_web.dart' if (dart.library.io) 'sse_mobile.dart';