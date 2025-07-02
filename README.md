# container-deploy-version-manager

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
**✨ Dashboard multiplataforma para la gestión de pases y gestión de contenedores Docker:  
despliegues, logs en tiempo real, versionamiento y control integral de la infraestructura.**


## 🚀 Características

- Gestión de servidores remotos vía SSH.  
- Listado, inicio, parada, reinicio y eliminación de contenedores.  
- Pull y eliminación de imágenes Docker.  
- Logs en vivo (SSE) desde la app web o Flutter (móvil/escritorio).  
- Versionamiento de despliegues (“pases”) y registro de auditoría.  
- Cifrado de credenciales en BD.


---

## 🛠️ Tecnologías

- **Backend**:  
  - Spring Boot, JSch (SSH), Jackson, MapStruct, JPA/Hibernate, Spring Security Crypto.  
- **Frontend Web**:  
  - Flutter Web (Dart), SSE, HTTP.  
- **Móvil / Escritorio**:  
  - Flutter multiplataforma (Android, iOS, Windows, macOS, Linux, Web).  
- **Base de Datos**:  
  - PostgreSQL.  

## 📂 Estructura del repositorio

    container-deploy-version-manager/
    ├── container-core-api-manager/ ← Spring Boot (API REST / SSH)
    ├── portal_manager_hub_ui/ ← Flutter multiplataforma (Android, iOS, Web, Desktop)
    ├── LICENSE
    └── README.md

---

## 🚀 Cómo arrancar
1. Clona:
```bash
    git clone https://github.com/DanielVega-Smll94/container-deploy-version-manager.git
```
2. 🛠️ Backend (Spring Boot) 
- Dirigirse a la carpeta y ejecutar:
```bash
cd container-core-api-manager
mvn clean install
mvn spring-boot:run
```

La API quedará escuchando en http://localhost:8081.

Configura application.yml con tus credenciales y cadena de conexión a BD.
Asegúrate de definir en application.yml la propiedad encryption.key.

--
3. 🖥️ Frontend / 📱 APP
```bash
cd portal_manager_hub_ui
flutter pub get
# Web
flutter run -d chrome    # o -d <tu dispositivo>
# Android
flutter run -d <emulador>
# iOS
flutter run -d <dispositivo>
```
- Para móviles o escritorio selecciona el emulador/dispositivo y flutter run.
- Verificar el puerto que designe flutter cuanndo sea en web

## 📄 Licencia
Este proyecto está bajo la Licencia MIT. Consulta el archivo [LICENSE](LICENSE) para más detalles.

### 🤝 Contribuir
¡Todas las aportaciones son bienvenidas!

Haz fork y crea una rama feature/tu-cambio.

Realiza tus cambios y commitea con mensajes claros.

Abre un Pull Request describiendo los cambios.