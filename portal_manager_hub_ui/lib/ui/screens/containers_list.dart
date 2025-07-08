import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:portal_manager_hub_ui/models/DockerSummary.dart';
import 'package:portal_manager_hub_ui/services/servidores.dart';
import 'package:portal_manager_hub_ui/ui/screens/summary_cards.dart';
import '../../models/container_info.dart';
import '../../services/api_service.dart';
import '../widgets/remove_choice_dialog.dart';
import 'container_logs.dart';
import '../../models/filters.dart';

class ContainersList extends StatefulWidget {
  final int servidorId;
  final String host;
  const ContainersList(this.servidorId, this.host, {super.key});

  @override
  State<ContainersList> createState() => _ContainersListState();
}

class _ContainersListState extends State<ContainersList>
    with SingleTickerProviderStateMixin {
  late Future<List<ContainerInfo>> _future;
  String _filter = '';
  late AnimationController _iconController;
  StatusFilter _statusFilter = StatusFilter.all;

  @override
  void initState() {
    super.initState();
    _iconController = AnimationController(
      vsync: this,
      duration: const Duration(seconds: 4),
    )..repeat();
    //_future = ApiService().listContainers(widget.servidorId);
    _load();
  }

  @override
  void dispose() {
    _iconController.dispose();
    super.dispose();
  }

  void _load() => _future = ApiService().listContainers(widget.servidorId);

  /// Este es el callback que le pasas a RefreshIndicator.onRefresh
  Future<void> _refresh() {
    // 1) lanza la nueva llamada pero NO await aquí
    final nueva = ApiService().listContainers(widget.servidorId);
    // 2) actualiza el state de forma síncrona
    setState(() {
      _future = nueva;
    });
    // 3) devuelves un Future<void> que completa cuando `nueva` finalice
    return nueva.then((_) => null);
  }

  bool _matchesStatus(ContainerInfo c) {
    final isUp = c.status.toLowerCase().startsWith('up');
    switch (_statusFilter) {
      case StatusFilter.active:
        return isUp;
      case StatusFilter.inactive:
        return !isUp;
      case StatusFilter.all:
      default:
        return true;
    }
  }

  void _handleAction(String act, ContainerInfo c) async {
    try {
      switch (act) {
        case 'start':
          await ApiService().startContainer(widget.servidorId, c.id);
          break;
        case 'stop':
          await ApiService().stopContainer(widget.servidorId, c.id);
          break;
        case 'restart':
          await ApiService().restartContainer(widget.servidorId, c.id);
          break;
        case 'remove':
          final choice = await showRemoveChoiceDialog(context);
          if (choice == 'container') {
            await ApiService().removeContainer(widget.servidorId, c.id);
          } else if (choice == 'both') {
            await ApiService().removeContainer(widget.servidorId, c.id);
            await ApiService().deleteImage(serverId: widget.servidorId, imageName: c.image);
          }
          break;
        case 'logs':
          Navigator.push(
            context,
            MaterialPageRoute(
              builder: (_) => ContainerLogs(
                servidorId: widget.servidorId,
                containerId: c.id,
                containerName: c.names,
              ),
            ),
          );
          return;
      }
      await _refresh(); // recarga la lista
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('$e')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Contenedores del ${widget.host}')),
      body: Column(
        children: [
          FutureBuilder<DockerSummary>(
            future: ServidoresService.fetchSummary(widget.servidorId),
            builder: (ctx, snap) {
              if (snap.connectionState != ConnectionState.done) {
                return const Padding(
                  padding: EdgeInsets.all(16),
                  child: Center(
                    child: CircularProgressIndicator(strokeWidth: 2),
                  ),
                );
              }
              if (snap.hasError) {
                return const Padding(
                  padding: EdgeInsets.all(16),
                  child: Center(child: Text('Error al cargar resumen')),
                );
              }
              return Padding(
                padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                child: SummaryCards( sum: snap.data!, servidorId:  widget.servidorId),
              );
            },
          ),
          // → Search bar separada
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
            child: TextField(
              decoration: InputDecoration(
                hintText: 'Buscar contenedor…',
                prefixIcon: const Icon(Icons.search),
                filled: true,
                fillColor: Colors.white,
                contentPadding: const EdgeInsets.symmetric(vertical: 0),
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(24),
                  borderSide: BorderSide.none,
                ),
              ),
              onChanged: (v) => setState(() {
                _filter = v.trim().toLowerCase();
              }),
            ),
          ),

          // — ChoiceChips de estado —
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            child: Wrap(
              spacing: 8,
              children: StatusFilter.values.map((sf) {
                final label = {
                  StatusFilter.all: 'Todos',
                  StatusFilter.active: 'Activos',
                  StatusFilter.inactive: 'Inactivos',
                }[sf]!;
                return ChoiceChip(
                  label: Text(label),
                  selected: _statusFilter == sf,
                  onSelected: (_) => setState(() => _statusFilter = sf),
                );
              }).toList(),
            ),
          ),
          // → El resto en Expanded
          Expanded(
            child: RefreshIndicator(
              onRefresh: _refresh,
              child: FutureBuilder<List<ContainerInfo>>(
                future: _future,
                builder: (ctx, snap) {
                  if (snap.connectionState != ConnectionState.done) {
                    return const Center(child: CircularProgressIndicator());
                  }
                  if (snap.hasError) {
                    return Center(child: Text('Error: ${snap.error}'));
                  }

                  // Aplico búsqueda AND filtro de estado
                  final todos = snap.data ?? [];
                  final filtrados = todos.where((c) {
                    final matchName = c.names.toLowerCase().contains(_filter);
                    final matchStatus = _matchesStatus(c);
                    return matchName && matchStatus;
                  }).toList();

                  if (filtrados.isEmpty) {
                    return const Center(child: Text('No hay contenedores.'));
                  }

                  return ListView.builder(
                    physics: const AlwaysScrollableScrollPhysics(),
                    padding: const EdgeInsets.symmetric(vertical: 6),
                    itemCount: filtrados.length,
                    itemBuilder: (ctx, i) {
                      final c = filtrados[i];
                      final isUp = c.status.toLowerCase().startsWith('up');
                      return Padding(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 4,
                        ),
                        child: Card(
                          margin: EdgeInsets.zero,
                          shape: RoundedRectangleBorder(
                            borderRadius: BorderRadius.circular(12),
                          ),
                          elevation: 2,
                          child: ListTile(
                            contentPadding: const EdgeInsets.symmetric(
                              horizontal: 16,
                              vertical: 8,
                            ),
                            leading: RotationTransition(
                              turns: _iconController,
                              child: CircleAvatar(
                                backgroundColor: Colors.blue.shade50,
                                child: FaIcon(
                                  FontAwesomeIcons.cube,
                                  color: Colors.blue.shade700,
                                ),
                              ),
                            ),
                            title: Text(
                              c.names,
                              style: const TextStyle(
                                fontWeight: FontWeight.bold,
                              ),
                            ),
                            subtitle: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                const SizedBox(height: 4),
                                Text(c.status),
                                //const SizedBox(height: 2),
                                Divider(),
                                if (c.ports.isNotEmpty) ...[
                                  Text(
                                    'Ports: ${c.ports}',
                                    style: const TextStyle(
                                      fontSize: 11,
                                      color: Colors.black54,
                                    ),
                                  ),
                                  const SizedBox(height: 2),
                                ],
                                Text(
                                  'Image: ${c.image}',
                                  style: const TextStyle(
                                    fontSize: 11,
                                    color: Colors.black54,
                                  ),
                                ),
                                const SizedBox(height: 2),
                                Text(
                                  'Created: ${c.createdAt.split(' -')[0]}',
                                  style: const TextStyle(
                                    fontSize: 11,
                                    color: Colors.black54,
                                  ),
                                ),
                              ],
                            ),
                            trailing: Row(
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Icon(
                                  isUp ? Icons.check_circle : Icons.cancel,
                                  color: isUp ? Colors.green : Colors.red,
                                  size: 16,
                                ),
                                const SizedBox(width: 8),
                                PopupMenuButton<String>(
                                  icon: const Icon(Icons.more_vert),
                                  onSelected: (act) => _handleAction(act, c),
                                  itemBuilder: (_) {
                                    final isUp = c.status
                                        .toLowerCase()
                                        .startsWith('up');
                                    return [
                                      if (!isUp)
                                        const PopupMenuItem(
                                          value: 'start',
                                          child: Text('Start'),
                                        ),
                                      if (isUp) ...[
                                        const PopupMenuItem(
                                          value: 'stop',
                                          child: Text('Stop'),
                                        ),
                                        const PopupMenuItem(
                                          value: 'restart',
                                          child: Text('Restart'),
                                        ),
                                      ],
                                      const PopupMenuItem(
                                        value: 'remove',
                                        child: Text('Remove'),
                                      ),
                                      const PopupMenuDivider(),
                                      const PopupMenuItem(
                                        value: 'logs',
                                        child: Text('Logs'),
                                      ),
                                    ];
                                  },
                                ),
                              ],
                            ),
                            onTap: () {
                              Navigator.push(
                                context,
                                MaterialPageRoute(
                                  builder: (_) => ContainerLogs(
                                    servidorId: widget.servidorId,
                                    containerId: c.id,
                                    containerName: c.names,
                                  ),
                                ),
                              );
                            },
                          ),
                        ),
                      );
                    },
                  );
                },
              ),
            ),
          ),
        ],
      ),
    );
  }
}
