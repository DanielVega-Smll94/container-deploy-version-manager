class ContainerInfo {
  final String id, image, names, command, status, ports, createdAt;

  ContainerInfo({
    required this.id,
    required this.image,
    required this.names,
    required this.command,
    required this.status,
    required this.ports,
    required this.createdAt,
  });

  factory ContainerInfo.fromJson(Map<String, dynamic> json) => ContainerInfo(
    id: json['ID'] ?? '',
    image: json['Image'] ?? '',
    names: json['Names'] ?? '',
    command: json['Command'] ?? '',
    status: json['Status'] ?? '',
    ports: json['Ports'] ?? '',
    createdAt: json['CreatedAt'] ?? '',
  );
}
