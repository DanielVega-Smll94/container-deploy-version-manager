class Servidor {
  final int id;
  final String nombre;
  final String host;
  final int port;
  final String username;
  final String descripcion;
  final bool estado;

  Servidor({
    required this.id,
    required this.nombre,
    required this.host,
    required this.port,
    required this.username,
    required this.descripcion,
    required this.estado,
  });

  factory Servidor.fromJson(Map<String, dynamic> json) => Servidor(
    id: json['id'],
    nombre: json['nombre'],
    host: json['host'],
    port: json['port'],
    username: json['username'],
    descripcion: json['descripcion'],
    estado: json['estado'],
  );
}
