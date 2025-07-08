class DockerSummary {
  final int stacks;
  final int containers;
  final int images;
  final int volumes;
  final int networks;

  DockerSummary({
    required this.stacks,
    required this.containers,
    required this.images,
    required this.volumes,
    required this.networks,
  });

  factory DockerSummary.fromJson(Map<String, dynamic> json) => DockerSummary(
    stacks: json['stacks'] as int,
    containers: json['containers'] as int,
    images: json['images'] as int,
    volumes: json['volumes'] as int,
    networks: json['networks'] as int,
  );
}
