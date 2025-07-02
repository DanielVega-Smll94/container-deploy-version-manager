import 'package:flutter/material.dart';
import '../../models/servidor.dart';

class ServerTile extends StatelessWidget {
  final Servidor srv;
  final VoidCallback onTap;
  const ServerTile({required this.srv, required this.onTap, super.key});

  @override
  Widget build(BuildContext c) {
    return ListTile(
      title: Text(srv.nombre),
      subtitle: Text('${srv.host}:${srv.port}'),
      trailing: srv.estado
          ? const Icon(Icons.check_circle, color: Colors.green)
          : const Icon(Icons.cancel, color: Colors.red),
      onTap: onTap,
    );
  }
}
