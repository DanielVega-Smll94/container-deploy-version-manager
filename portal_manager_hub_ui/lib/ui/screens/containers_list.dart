import 'package:flutter/material.dart';
import '../../models/container_info.dart';
import '../../services/api_service.dart';
import 'container_logs.dart';

class ContainersList extends StatefulWidget {
  final int servidorId;
  const ContainersList(this.servidorId, {super.key});

  @override
  State<ContainersList> createState() => _ContainersListState();
}

class _ContainersListState extends State<ContainersList> {
  late Future<List<ContainerInfo>> _future;

  @override
  void initState() {
    super.initState();
    _future = ApiService().listContainers(widget.servidorId);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Contenedores')),
      body: FutureBuilder<List<ContainerInfo>>(
        future: _future,
        builder: (ctx, snap) {
          if (snap.connectionState != ConnectionState.done) {
            return Center(child: CircularProgressIndicator());
          }
          if (snap.hasError) {
            return Center(child: Text('Error: ${snap.error}'));
          }
          final list = snap.data!;
          return ListView.builder(
            itemCount: list.length,
            itemBuilder: (_, i) {
              final c = list[i];
              return ListTile(
                title: Text(c.names),
                subtitle: Text(c.status),
                onTap: () => Navigator.push(
                  context,
                  MaterialPageRoute(
                    builder: (_) => ContainerLogs(
                      servidorId: widget.servidorId,
                      containerId: c.id,
                      containerName: c.names,
                    ),
                  ),
                ),
                trailing: PopupMenuButton<String>(
                  onSelected: (act) async {
                    try {
                      // 1) Ejecuta la acción
                      if (act == 'start') {
                        await ApiService().startContainer(
                          widget.servidorId,
                          c.id,
                        );
                      } else if (act == 'stop') {
                        await ApiService().stopContainer(
                          widget.servidorId,
                          c.id,
                        );
                      } else if (act == 'restart') {
                        await ApiService().restartContainer(
                          widget.servidorId,
                          c.id,
                        );
                      }
                      else if (act == 'remove') {
                        await ApiService().removeContainer(
                          widget.servidorId,
                          c.id,
                        );
                      }

                      // 2) Recupera la nueva lista
                      final listaActualizada = await ApiService()
                          .listContainers(widget.servidorId);

                      // 3) Sincronamente, dispara el rebuild
                      setState(() {
                        _future = Future.value(listaActualizada);
                      });
                    } catch (e) {
                      ScaffoldMessenger.of(
                        context,
                      ).showSnackBar(SnackBar(content: Text('$e')));
                    }
                  },
                  itemBuilder: (_) => [
                    PopupMenuItem(value: 'start', child: Text('Start')),
                    PopupMenuItem(value: 'stop', child: Text('Stop')),
                    PopupMenuItem(value: 'restart', child: Text('Restart')),
                    PopupMenuItem(value: 'remove', child: Text('Remove')),
                    PopupMenuItem(
                      value: 'logs',
                      child: Text('Logs'),
                      onTap: () => Future.microtask(() {
                        Navigator.push(
                          context,
                          MaterialPageRoute(
                            builder: (_) => ContainerLogs(
                              servidorId: widget.servidorId,
                              containerId: c.id,
                              containerName: c.names
                            ),
                          ),
                        );
                      }),
                    ),
                  ],
                ),
              );
            },
          );
        },
      ),
    );
  }
}
