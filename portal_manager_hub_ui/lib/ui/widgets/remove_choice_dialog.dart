// lib/ui/widgets/remove_choice_dialog.dart
import 'package:flutter/material.dart';

/// Muestra un diálogo para elegir eliminar sólo el contenedor o también la imagen.
/// Devuelve 'container', 'both' o null si se cancela.
Future<String?> showRemoveChoiceDialog(BuildContext context) {
  return showDialog<String>(
    context: context,
    builder: (_) => AlertDialog(
      title: const Text('Eliminar contenedor'),
      content: const Text('¿Qué deseas eliminar?'),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context, null),
          child: const Text('Cancelar'),
        ),
        TextButton(
          onPressed: () => Navigator.pop(context, 'container'),
          child: const Text('Sólo contenedor'),
        ),
        TextButton(
          onPressed: () => Navigator.pop(context, 'both'),
          child: const Text('Contenedor + Imagen'),
        ),
      ],
    ),
  );
}
