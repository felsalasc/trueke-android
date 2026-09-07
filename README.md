# Trueke

Aplicación móvil desarrollada para la asignatura
Desarrollo de Aplicaciones Móviles – DSY2204.

## Descripción

Trueke es una aplicación móvil orientada al intercambio de productos
entre usuarios cercanos sin necesidad de utilizar dinero como medio
principal de transacción.

Los usuarios pueden visualizar productos disponibles, consultar su
valor referencial, distancia y estado, y manifestar interés en realizar
un intercambio.

## Tecnologías

- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- Navigation Compose

## Funcionalidades desarrolladas

- Inicio de sesión
- Registro de usuario
- Recuperación de contraseña
- Navegación entre vistas
- Catálogo de productos
- Cinco productos almacenados localmente
- Visualización de distancia, categoría, estado y valor referencial

## Semana 4 - Integración de funcionalidades Kotlin

Durante la Semana 4 se incorporaron y reforzaron funcionalidades utilizando Kotlin:

- Adaptación y validación de las vistas de Login, Registro y Recuperar contraseña.
- Implementación de un `Array<Product>` mediante `arrayOf()` para almacenar 5 productos.
- Uso de colecciones Kotlin mediante `List`, `listOf()` y `filter()`.
- Implementación de la función `filterProducts()` para filtrar productos por:
  - categoría;
  - distancia máxima;
  - estado del producto.
- Interfaz adaptable mediante `GridCells.Adaptive`.
- Formulario de registro desplazable para mejorar la experiencia en dispositivos pequeños.
- Validación del correo electrónico en recuperación de contraseña.
- Incorporación del permiso de Internet en `AndroidManifest.xml`.


## Autor

Felipe Salas
