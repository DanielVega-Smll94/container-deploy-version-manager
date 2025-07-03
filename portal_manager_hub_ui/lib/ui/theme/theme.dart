import 'package:flutter/material.dart';

/// Palette de colores de la aplicación
typedef AppColor = Color;
class AppColors {
  AppColors._();

  static const primary = Color(0xFF006CFF);
  static const secondary = Color(0xFF00D1FF);
  static const background = Color(0xFFF5F6FA);
  static const surface = Colors.white;
  static const error = Color(0xFFFF4D4F);
  static const success = Color(0xFF52C41A);

  /// Textos
  static const textPrimary = Color(0xFF1F1F41);
  static const textSecondary = Color(0xFF5A5A7A);
}

/// Tipografía y estilos de texto
class AppTextStyles {
  AppTextStyles._();

  static const fontFamily = 'Roboto';

  static const headline1 = TextStyle(
    fontFamily: fontFamily,
    fontSize: 32,
    fontWeight: FontWeight.bold,
    color: AppColors.textPrimary,
  );

  static const headline2 = TextStyle(
    fontFamily: fontFamily,
    fontSize: 24,
    fontWeight: FontWeight.w600,
    color: AppColors.textPrimary,
  );

  static const body1 = TextStyle(
    fontFamily: fontFamily,
    fontSize: 16,
    fontWeight: FontWeight.normal,
    color: AppColors.textSecondary,
  );

  static const monospace = TextStyle(
    fontFamily: 'SourceCodePro',
    fontSize: 14,
    color: AppColors.success,
  );
}

/// Espaciados y dimensiones comunes
class AppSpacing {
  AppSpacing._();

  static const xs = 4.0;
  static const sm = 8.0;
  static const md = 16.0;
  static const lg = 24.0;
  static const xl = 32.0;
}

/// Construye el ThemeData global para la app
ThemeData buildAppTheme() {
  return ThemeData(
    useMaterial3: false,
    primaryColor: AppColors.primary,
    scaffoldBackgroundColor: AppColors.background,
    colorScheme: ColorScheme.light(
      primary: AppColors.primary,
      secondary: AppColors.secondary,
      background: AppColors.background,
      surface: AppColors.surface,
      error: AppColors.error,
    ),
    textTheme: const TextTheme(
      displayLarge: AppTextStyles.headline1,
      displayMedium: AppTextStyles.headline2,
      bodyLarge: AppTextStyles.body1,
    ),
    fontFamily: AppTextStyles.fontFamily,
    elevatedButtonTheme: ElevatedButtonThemeData(
      style: ElevatedButton.styleFrom(
        textStyle: const TextStyle(fontWeight: FontWeight.w600),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
        ),
      ),
    ),
    cardTheme: const CardThemeData(
      color: AppColors.surface,
      elevation: 4,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.all(Radius.circular(16)),
      ),
      margin: EdgeInsets.all(AppSpacing.md),
    ),
    listTileTheme: const ListTileThemeData(
      contentPadding: EdgeInsets.symmetric(
        horizontal: AppSpacing.sm,
        vertical: AppSpacing.xs,
      ),
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.all(Radius.circular(8)),
      ),
    ),
  );
}
