# Marketplace Android Application 🛒🤖

Aplicación Android nativa desarrollada en **Kotlin** con arquitectura moderna (MVVM, Navigation Component, Room, Firebase Firestore, Firebase Auth) e integración de un **Asistente Virtual IA** para recomendación personalizada de productos.

---

## 🚀 Características Principales

- 🛍️ **Catálogo de Productos**: Búsqueda en tiempo real y filtrado dinámico de artículos con persistencia local en Room y sincronización en la nube con Firebase.
- 🤖 **Asistente Virtual IA**: Chat interactivo capaz de recomendar productos (ej. pantallas, laptops, celulares) mediante un flujo de preguntas inteligentes sobre presupuesto y uso.
- 🔐 **Autenticación de Usuarios**: Inicio de sesión y registro seguro con roles de **Comprador** y **Administrador**.
- 🛠️ **Panel de Administración**: Gestión completa de inventario (crear, editar, eliminar productos y revisar mensajes de clientes).
- 💬 **Mensajería en Tiempo Real**: Chat P2P entre compradores y vendedores.
- ⚙️ **Integración CI/CD**: Pipeline automatizado con Azure DevOps (`azure-pipelines.yml`) para compilación de APK en cada push o Pull Request.

---

## 🛠️ Tecnologías Utilizadas

- **Lenguaje**: Kotlin (JDK 17)
- **UI & Layouts**: Android Material Components, ConstraintLayout, ViewBinding, Glide
- **Navegación**: AndroidX Navigation Component (`safeargs`)
- **Persistencia Local**: Room Database (SQLite)
- **Backend & Cloud**: Firebase Authentication, Cloud Firestore
- **Integración Continua**: Azure DevOps Pipelines

---

## 🏗️ Compilación y Ejecución

Para compilar el proyecto localmente mediante Gradle:

```bash
# Compilar APK Debug
./gradlew assembleDebug

# Ejecutar pruebas unitarias
./gradlew test
```
