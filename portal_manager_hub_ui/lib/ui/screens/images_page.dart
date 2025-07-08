// lib/ui/screens/images_list.dart
import 'package:flutter/material.dart';
import 'package:font_awesome_flutter/font_awesome_flutter.dart';
import 'package:portal_manager_hub_ui/models/ImagesInfo.dart';
import '../../services/api_service.dart';

class ImagesList extends StatefulWidget {
  final int servidorId;
  const ImagesList(this.servidorId, {Key? key}) : super(key: key);

  @override
  State<ImagesList> createState() => _ImagesListState();
}

class _ImagesListState extends State<ImagesList> {
  late Future<List<ImagesInfo>> _future;
  String _filter = '';

  @override
  void initState() {
    super.initState();
    _refresh(); // carga inicial
  }

  Future<void> _refresh() {
    setState(() {
      _future = ApiService().listImages(widget.servidorId);
    });
    return _future.then((_) => null);
  }

  void _onDelete(ImagesInfo img) async {
    final ok = await showDialog<bool>(
      context: context,
      builder: (_) => AlertDialog(
        title: const Text('¿Borrar imagen?'),
        content: Text('${img.repository}:${img.tag}'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context,false), child: const Text('Cancelar')),
          TextButton(onPressed: () => Navigator.pop(context,true),  child: const Text('Borrar')),
        ],
      ),
    );
    if (ok == true) {
      try {
        await ApiService().deleteImage(
            serverId: widget.servidorId,
            imageId: img.id
        );
        ScaffoldMessenger.of(context)
            .showSnackBar(const SnackBar(content: Text('Imagen borrada')));
        _refresh();
      } catch (e) {
        ScaffoldMessenger.of(context)
            .showSnackBar(SnackBar(content: Text('Error: $e')));
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Imágenes')),
      body: Column(
        children: [
          // Buscador
          Padding(
            padding: const EdgeInsets.all(12),
            child: TextField(
              decoration: InputDecoration(
                hintText: 'Buscar imagen…',
                prefixIcon: const Icon(Icons.search),
                filled: true,
                fillColor: Colors.white,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(24),
                  borderSide: BorderSide.none,
                ),
                isDense: true,
              ),
              onChanged: (v) => setState(() => _filter = v.trim().toLowerCase()),
            ),
          ),

          // Grid de cards con pull-to-refresh
          Expanded(
            child: RefreshIndicator(
              onRefresh: _refresh,
              child: FutureBuilder<List<ImagesInfo>>(
                future: _future,
                builder: (ctx, snap) {
                  if (snap.connectionState != ConnectionState.done) {
                    return const Center(child: CircularProgressIndicator());
                  }
                  if (snap.hasError) {
                    return Center(child: Text('Error: ${snap.error}'));
                  }
                  final all = snap.data!;
                  final filtered = _filter.isEmpty
                      ? all
                      : all.where((img) {
                    final f = _filter;
                    return img.repository.toLowerCase().contains(f)
                        || img.tag.toLowerCase().contains(f)
                        || img.id.startsWith(f);
                  }).toList();
                  if (filtered.isEmpty) {
                    return const Center(child: Text('No hay imágenes.'));
                  }
                  return GridView.builder(
                    physics: const AlwaysScrollableScrollPhysics(),
                    padding: const EdgeInsets.all(12),
                    gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
                      crossAxisCount: 2,
                      mainAxisSpacing: 12,
                      crossAxisSpacing: 12,
                      childAspectRatio: 0.75,
                    ),
                    itemCount: filtered.length,
                    itemBuilder: (ctx, i) {
                      final img = filtered[i];
                      return Card(
                        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                        elevation: 3,
                        child: SizedBox(
                          // le puedes dar altura fija o dejarla crecer según contenido
                          child: Padding(
                            padding: const EdgeInsets.all(8),
                            child: Stack(
                              children: [
                                // 1) El contenido principal
                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.stretch,
                                  children: [
                                    const Icon(FontAwesomeIcons.fileImage, size: 30, color: Colors.blueGrey),
                                    const SizedBox(height: 8),
                                    Text(img.repository, style: const TextStyle(fontWeight: FontWeight.bold)),
                                    Text(img.tag, style: const TextStyle(fontSize: 12, color: Colors.black54)),
                                    const SizedBox(height: 6),
                                    Text(img.id.substring(0,12),
                                        style: const TextStyle(fontSize: 10, color: Colors.black45)),
                                    const SizedBox(height: 8),
                                    Text('Size: ${img.size}', style: const TextStyle(fontSize: 11)),
                                    Text('Created: ${img.createdAt}', style: const TextStyle(fontSize: 11)),
                                  ],
                                ),

                                // 2) El botón delete posicionado abajo-derecha
                                Positioned(
                                  bottom: -12,
                                  right: 0,
                                  child: IconButton(
                                    icon: const Icon(Icons.delete, size: 20, color: Colors.redAccent),
                                    onPressed: () => _onDelete(img),
                                  ),
                                ),
                              ],
                            ),
                          ),
                        ),
                      );

                      /*return Card(
                        shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(12),
                        ),
                        elevation: 3,
                        child: Padding(
                          padding: const EdgeInsets.all(8),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.stretch,
                            children: [
                              const Icon(Icons.image, size: 40, color: Colors.blueGrey),
                              const SizedBox(height: 8),
                              Text(
                                img.repository,
                                maxLines: 2,
                                overflow: TextOverflow.ellipsis,
                                style: const TextStyle(fontWeight: FontWeight.bold),
                              ),
                              Text(
                                img.tag,
                                style: const TextStyle(fontSize: 12, color: Colors.black54),
                              ),
                              const SizedBox(height: 6),
                              Text(
                                img.id.substring(0, 12),
                                style: const TextStyle(fontSize: 10, color: Colors.black45),
                              ),
                              const Spacer(),
                              Text('Size: ${img.size}', style: const TextStyle(fontSize: 11)),
                              Text('Created: ${img.createdAt}', style: const TextStyle(fontSize: 11)),
                              Align(
                                alignment: Alignment.bottomRight,
                                child: IconButton(
                                  icon: const Icon(Icons.delete, size: 20, color: Colors.redAccent),
                                  onPressed: () async {
                                    final ok = await showDialog<bool>(
                                      context: context,
                                      builder: (_) => AlertDialog(
                                        title: const Text('¿Borrar imagen?'),
                                        content: Text('${img.repository}:${img.tag}'),
                                        actions: [
                                          TextButton(
                                            onPressed: () => Navigator.pop(context, false),
                                            child: const Text('Cancelar'),
                                          ),
                                          TextButton(
                                            onPressed: () => Navigator.pop(context, true),
                                            child: const Text('Borrar'),
                                          ),
                                        ],
                                      ),
                                    );
                                    if (ok == true) {
                                      try {
                                        await ApiService().deleteImage(serverId: widget.servidorId, imageId: img.id);
                                        ScaffoldMessenger.of(context).showSnackBar(
                                          const SnackBar(content: Text('Imagen borrada')),
                                        );
                                        _refresh();
                                      } catch (e) {
                                        ScaffoldMessenger.of(context).showSnackBar(
                                          SnackBar(content: Text('Error: $e')),
                                        );
                                      }
                                    }
                                  },
                                ),
                              ),
                            ],
                          ),
                        ),
                      );*/
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
