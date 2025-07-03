import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:flutter/material.dart';

import '../../models/servidor.dart';

class ServerCard extends StatelessWidget {
  final Servidor servidor;
  final VoidCallback onTap;

  const ServerCard({
    Key? key,
    required this.servidor,
    required this.onTap,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(vertical: 6, horizontal: 12),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      elevation: 2,
      child: InkWell(
        borderRadius: BorderRadius.circular(12),
        onTap: onTap,
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Row(
            children: [
              // ícono Docker de FontAwesome
              CircleAvatar(
                radius: 24,
                backgroundColor: Colors.blue.shade50,
                child: FaIcon(
                  FontAwesomeIcons.docker,
                  color: Colors.blue.shade700,
                  size: 28,
                ),
              ),

              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      '${servidor.nombre} ${servidor.host}',
                      style: const TextStyle(
                        fontSize: 16,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      servidor.descripcion,
                      style: const TextStyle(fontSize: 12, color: Colors.black54),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      '${servidor.host}',
                      style: const TextStyle(fontSize: 12),
                    ),
                    const SizedBox(height: 4),
                    Row(
                      children: [
                        Icon(
                          servidor.estado ? Icons.check_circle : Icons.cancel,
                          size: 14,
                          color: servidor.estado ? Colors.green : Colors.red,
                        ),
                        const SizedBox(width: 4),
                        Text(
                          servidor.estado ? 'Up' : 'Down',
                          style: const TextStyle(fontSize: 12),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              const Icon(Icons.chevron_right),
            ],
          ),
        ),
      ),
    );
  }
}
