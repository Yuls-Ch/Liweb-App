# 📖 LIWEB – Aplicación Móvil de Venta de Libros para Android

**LIWEB** es una aplicación móvil nativa para Android, desarrollada en **Kotlin**, diseñada para ofrecer una experiencia de compra de libros moderna y segura. Integra tecnologías de vanguardia como **Firebase** para el backend en tiempo real y **Stripe** para un procesamiento de pagos confiable.

---

## 🎨 Vista Previa y Descarga

[🎨 **Ver Prototipo (Figma)**](https://bit.ly/Liweb-App)

[![Download APK](https://img.shields.io/badge/⬇️_Descargar-LiWeb%20v1.0-brightgreen?style=for-the-badge)](https://drive.google.com/uc?export=download&id=18_Oj23RSBEXvvO7jpKHIBZOyzJBSmXwO)

---

## ✨ Características Principales

| Funcionalidad | Descripción |
| :--- | :--- |
| **Autenticación Segura** | Registro e inicio de sesión con Correo/Contraseña y **Google Sign-In**. |
| **Carrito Sincronizado** | Gestión de carrito en tiempo real con **Firebase Realtime Database**. |
| **Pagos Integrados** | Procesamiento de pagos seguro mediante **Stripe SDK**. |
| **Lista de Deseos** | Función de "Favoritos" personalizada para cada usuario. |
| **Temas Dinámicos** | Soporte completo para **Modo Claro y Oscuro**. |
| **Control de Stock** | Actualización y visualización del inventario en tiempo real. |
| **Diseño Material** | Interfaz moderna, animada y responsiva con **Material Design**. |

---

## 🛠 Stack Tecnológico

El proyecto está construido con un stack moderno y escalable para el ecosistema Android:

### 💻 Desarrollo Nativo

* **Lenguaje:** **Kotlin**
* **Arquitectura:** **MVP** 
* **Diseño de UI:** XML, **ViewBinding**, y **Material Design**

### ☁️ Backend y Servicios

* **Base de Datos:** **Firebase Realtime Database**
* **Autenticación:** **Firebase Authentication**
* **Pasarela de Pago:** **Stripe SDK**

### 🧩 Dependencias Clave

| Dependencia | Propósito |
| :--- | :--- |
| **Retrofit** | Cliente HTTP para comunicación de red (incluyendo Stripe). |
| **Glide** | Carga y caché eficiente de imágenes. |
| **Gson** | Serialización y deserialización de objetos JSON. |
| **SweetAlert** | Diálogos y notificaciones de usuario mejoradas. |

---

## 🚀 Instalación y Ejecución Local

Sigue estos pasos para configurar y ejecutar el proyecto en tu entorno de desarrollo.

### 1. Requisitos del Entorno

* **Android Studio** (Versión reciente recomendada).
* **Java Development Kit (JDK) 17** o superior.
* Credenciales de **Firebase** y **Stripe** para configuración.

### 2. Clonar y Abrir el Proyecto

1. Clone the repository:
   ```bash
   git clone https://github.com/Yuls-Ch/Liweb-App.git
   cd Liweb-App

2.  Abre la carpeta `LiWeb-App` en **Android Studio**.
3.  Espera la sincronización de Gradle.

### 3. Configuración de Credenciales

1.  **Firebase:**
    * Crea un proyecto y una aplicación Android en la consola de Firebase.
    * Descarga el archivo **`google-services.json`** y colócalo en la carpeta **`app/`** del proyecto.
2.  **Stripe:**
    * Obtén tus **Keys de Prueba** (`pk_test_...` y `sk_test_...`).
    * Inyecta estas claves en el archivo de constantes del proyecto (e.g., `Constants.kt`) para habilitar los pagos.

### 4. Ejecutar

* Ejecuta la aplicación en un emulador de Android (mínimo API 21) o en un dispositivo físico.
