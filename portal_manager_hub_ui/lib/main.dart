import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:portal_manager_hub_ui/ui/screens/servers_page.dart';
import 'package:portal_manager_hub_ui/ui/theme/theme.dart';
import 'ui/screens/containers_list.dart';

//void main() => runApp(const MyApp());
Future<void> main() async {
  await dotenv.load(fileName: ".env");
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});
  @override
  Widget build(BuildContext c) => MaterialApp(
    title: 'Portal Manager Hub',
    theme: buildAppTheme(),
    debugShowCheckedModeBanner: false,
    //theme: ThemeData(primarySwatch: Colors.blue),
    home: const ServersPage()//ContainersList(5),
  );
}
