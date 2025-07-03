// lib/pages/servers_page.dart
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:portal_manager_hub_ui/ui/screens/container_logs.dart';
import 'package:portal_manager_hub_ui/ui/screens/containers_list.dart';
import 'package:portal_manager_hub_ui/ui/widgets/ServerCard.dart';
import '../../models/servidor.dart';
import '../../services/servidores.dart';

class ServersPage extends StatelessWidget {
  const ServersPage({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title:
      Row(
        children: const [
          FaIcon(
            FontAwesomeIcons.server,
            size: 20,
            color: Colors.white,
          ),
          SizedBox(width: 8),
          Text('Mis Servidores'),
        ],
      ),
      //const Text('Mis Servidores')
      ),
      body: FutureBuilder<List<Servidor>>(
        future: ServidoresService.fetchServidores(),
        builder: (context, snapshot) {
          if (snapshot.connectionState != ConnectionState.done) {
            return const Center(child: CircularProgressIndicator());
          }
          if (snapshot.hasError) {
            return Center(child: Text(snapshot.error.toString()));
          }
          final servidores = snapshot.data!;
          return ListView.builder(
            padding: const EdgeInsets.all(8),
            itemCount: servidores.length,
            itemBuilder: (context, i) {
              final s = servidores[i];
              return ServerCard(servidor: s,
              onTap: () {
                Navigator.push(
                  context,
                  MaterialPageRoute(
                      builder: (_) =>
                          ContainersList(s.id, s.host)
                  ),
                );
              });




              /*Card(
                margin: const EdgeInsets.symmetric(vertical: 6),
                child: ListTile(
                  contentPadding: const EdgeInsets.all(12),
                  title: Text(s.nombre, style: const TextStyle(fontWeight: FontWeight.bold)),
                  subtitle: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text('${s.host}:${s.port}  •  ${s.descripcion}'),
                      Text('User: ${s.username}  •  Pass: ${'*******'}'),
                      Text('Estado: ${s.estado ? "Up" : "Down"}'),
                    ],
                  ),
                  isThreeLine: true,
                  trailing: const Icon(Icons.chevron_right),
                  onTap: () {
                    Navigator.push(
                      context,
                      MaterialPageRoute(
                        builder: (_) =>
                        ContainersList(s.id)
                      ),
                    );
                  },
                ),
              );*/
            },
          );
        },
      ),
    );
  }
}
