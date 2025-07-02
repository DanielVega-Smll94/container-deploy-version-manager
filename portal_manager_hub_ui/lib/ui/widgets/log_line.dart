import 'package:flutter/material.dart';

class LogLine extends StatelessWidget {
  final String text;
  const LogLine(this.text, {super.key});

  @override
  Widget build(BuildContext c) {
    return Text(
      text,
      style: const TextStyle(fontFamily: 'monospace', fontSize: 12),
    );
  }
}
