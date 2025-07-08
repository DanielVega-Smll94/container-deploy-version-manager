class ImagesInfo {
  final String id;
  final String repository;
  final String tag;
  final String createdAt;
  final String size;

  ImagesInfo({
    required this.id,
    required this.repository,
    required this.tag,
    required this.createdAt,
    required this.size,
  });

  factory ImagesInfo.fromJson(Map<String, dynamic> json) => ImagesInfo(
    id: json['ID'] as String,
    repository: json['Repository'] as String,
    tag: json['Tag'] as String,
    // cortamos la parte " -0500 -05" tal y como hacías antes
    createdAt: (json['CreatedAt'] as String).split(' -').first,
    size: json['Size'] as String,
  );
}
