// lib/ui/widgets/summary_cards.dart
/*import 'package:flutter/material.dart';
import '../../models/DockerSummary.dart';

class SummaryCards extends StatelessWidget {
  final DockerSummary sum;
  final int servidorId;              // ← ahora recibe el ID

  const SummaryCards(this.sum, this.servidorId, {Key? key}) : super(key: key);

  Widget _item(IconData icon, String label, int value) {
    return Expanded(
      child: Card(
        margin: const EdgeInsets.symmetric(horizontal: 4),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
        elevation: 1,
        child: Padding(
          padding: const EdgeInsets.symmetric(vertical: 8),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Icon(icon, size: 20, color: Colors.blueGrey),
              const SizedBox(height: 4),
              Text('$value', style: const TextStyle(fontWeight: FontWeight.bold)),
              const SizedBox(height: 2),
              Text(label, style: const TextStyle(fontSize: 11, color: Colors.black54)),
            ],
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        _item(Icons.layers,       'Stack',      sum.stacks),
        _item(Icons.cloud_queue,  'Containers', sum.containers),
        _item(Icons.image,        'Images',     sum.images),
        _item(Icons.storage,      'Volumes',    sum.volumes),
        _item(Icons.network_wifi, 'Networks',   sum.networks),
      ],
    );
  }
}
*/

// lib/ui/widgets/summary_cards.dart
import 'package:flutter/material.dart';
import '../../models/DockerSummary.dart';
import 'images_page.dart';

class SummaryCards extends StatelessWidget {
  final DockerSummary sum;
  final int servidorId;
  const SummaryCards({
    Key? key,
    required this.sum,
    required this.servidorId,
  }) : super(key: key);

  Widget _item({
    required IconData icon,
    required String label,
    required int value,
    VoidCallback? onTap,
  }) {
    final card = Card(
      margin: const EdgeInsets.symmetric(horizontal: 4),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      elevation: 1,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 8),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(icon, size: 20, color: Colors.blueGrey),
            const SizedBox(height: 4),
            Text('$value', style: const TextStyle(fontWeight: FontWeight.bold)),
            const SizedBox(height: 2),
            Text(label, style: const TextStyle(fontSize: 11, color: Colors.black54)),
          ],
        ),
      ),
    );

    return Expanded(
      child: onTap == null
          ? card
          : InkWell(
        borderRadius: BorderRadius.circular(8),
        onTap: onTap,
        child: card,
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        _item(icon: Icons.layers,      label: 'Stack',      value: sum.stacks),
        _item(icon: Icons.cloud_queue, label: 'Containers', value: sum.containers),
        _item(
          icon: Icons.image,
          label: 'Images',
          value: sum.images,
          onTap: () {
            Navigator.push(
              context,
              MaterialPageRoute(
                builder: (_) => ImagesList(servidorId),
              ),
            );
          },
        ),
        // aquí corregimos el nombre del parámetro:
        _item(icon: Icons.storage,     label: 'Volumes',    value: sum.volumes),
        _item(icon: Icons.network_wifi,label: 'Networks',   value: sum.networks),
      ],
    );
  }
}
