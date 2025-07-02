import 'package:flutter/material.dart';
import 'ui/screens/servers_list.dart';

void main() => runApp(const MyApp());

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext c) => MaterialApp(
    title: 'Portal Manager Hub',
    theme: ThemeData(primarySwatch: Colors.blue),
    home: const ContainersList(5),
  );
}
