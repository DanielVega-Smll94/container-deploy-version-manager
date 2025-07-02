/*
import 'dart:async';

import 'package:flutter/material.dart';
import 'package:portal_manager_hub_ui/services/stream_service.dart';

import '../../services/api_service.dart';


class ContainerLogs extends StatefulWidget {
  final int servidorId;
  final String containerId;
  final String containerName;

  const ContainerLogs({
    super.key,
    required this.servidorId,
    required this.containerId,
    required this.containerName
  });

  @override
  State<ContainerLogs> createState() => _ContainerLogsState();
}

class _ContainerLogsState extends State<ContainerLogs> {
  final _lines = <String>[];
  late final StreamSubscription<String> _sub;

  @override
  void initState() {
    super.initState();
    _sub = StreamService()
        .streamLogsSseSSH(
      servidorId: widget.servidorId,
      containerName: widget.containerName,
      tailLines: 200,
    )
    .listen((line) {
      setState(() => _lines.insert(0, line));
    }, onError: (e) {
      setState(() => _lines.insert(0, '⚠️ $e'));
    });
  }

  @override
  void dispose() {
    _sub.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) => Scaffold(
    appBar: AppBar(title: Text('Logs: ${widget.containerId}')),
    body: Container(
      color: Colors.black87,
      child: ListView.builder(
        reverse: true,
        itemCount: _lines.length,
        itemBuilder: (_, i) => Padding(
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
          child: Text(
            _lines[i],
            style: const TextStyle(color: Colors.greenAccent, fontFamily: 'monospace'),
          ),
        ),
      ),
    ),
  );
}
*/


// lib/ui/screens/container_logs.dart

import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/foundation.dart';
import 'package:portal_manager_hub_ui/services/stream_service.dart';

class ContainerLogs extends StatefulWidget {
  final int servidorId;
  final String containerId;
  final String containerName;

  const ContainerLogs({
    super.key,
    required this.servidorId,
    required this.containerId,
    required this.containerName,
  });

  @override
  State<ContainerLogs> createState() => _ContainerLogsState();
}

class _ContainerLogsState extends State<ContainerLogs> {
  final _lines = <String>[];
  StreamSubscription<String>? _sub;

  @override
  void initState() {
    super.initState();
    _startLogs();
  }

  Future<void> _startLogs() async {
    try {
      // Esperamos el Future<Stream<String>>
      final stream = await StreamService().logs(
        servidorId: widget.servidorId,
        containerId: widget.containerName,
        tail: 200,
      );
      // Y aquí ya nos suscribimos
      _sub = stream.listen(
            (line) => setState(() => _lines.insert(0, line)),
        onError: (e) => setState(() => _lines.insert(0, '⚠️ $e')),
      );
    } catch (e) {
      setState(() => _lines.insert(0, '⚠️ $e'));
    }
  }

  @override
  void dispose() {
    _sub?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) => Scaffold(
    appBar: AppBar(title: Text('Logs: ${widget.containerName}',
    style: TextStyle(
      fontSize: 18
    ),
    )),
    body: Container(
      color: Colors.black87,
      child: ListView.builder(
        reverse: true,
        itemCount: _lines.length,
        itemBuilder: (_, i) => Padding(
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
          child: Text(
            _lines[i],
            style: const TextStyle(
              color: Colors.greenAccent,
              fontFamily: 'monospace',
            ),
          ),
        ),
      ),
    ),
  );
}
