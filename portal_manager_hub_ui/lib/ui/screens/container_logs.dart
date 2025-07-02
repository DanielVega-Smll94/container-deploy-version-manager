
/*
import 'package:flutter/material.dart';
import '../../services/api_service.dart';

class ContainerLogs extends StatefulWidget {
  final int servidorId;
  final String containerId;
  const ContainerLogs({
    super.key,
    required this.servidorId,
    required this.containerId,
  });

  @override
  State<ContainerLogs> createState() => _ContainerLogsState();
}

class _ContainerLogsState extends State<ContainerLogs> {
  final _lines = <String>[];

  @override
  void initState() {
    super.initState();
    ApiService()
        .streamLogs(
      servidorId: widget.servidorId,
      containerId: widget.containerId,
      tail: 200,
    )
        .listen((line) {
      setState(() => _lines.insert(0, line)); // va al inicio
    }, onError: (e) {
      setState(() => _lines.insert(0, '⚠️ $e'));
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Logs: ${widget.containerId}')),
      body: ListView.builder(
        reverse: true,
        itemCount: _lines.length,
        itemBuilder: (_, i) => Padding(
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
          child: Text(_lines[i], style: TextStyle(fontFamily: 'monospace')),
        ),
      ),
    );
  }
}
*/

import 'package:flutter/material.dart';

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

  @override
  void initState() {
    super.initState();
    ApiService()
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
  Widget build(BuildContext context) => Scaffold(
    appBar: AppBar(title: Text('Logs: ${widget.containerId}')),
    body: ListView.builder(
      reverse: true,
      itemCount: _lines.length,
      itemBuilder: (_, i) => Padding(
        padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
        child: Text(
          _lines[i],
          style: const TextStyle(fontFamily: 'monospace'),
        ),
      ),
    ),
  );
}