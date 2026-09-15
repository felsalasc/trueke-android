# Trueke

Aplicación móvil desarrollada para la asignatura **Desarrollo de Aplicaciones Móviles – DSY2204** de Duoc UC.

## Descripción

**Trueke** es una aplicación móvil orientada al intercambio de productos entre usuarios cercanos, sin necesidad de utilizar dinero como medio principal de transacción.

La aplicación permite visualizar productos disponibles, consultar su categoría, estado, valor referencial y distancia, además de aplicar filtros para facilitar la búsqueda.

El proyecto considera principios de accesibilidad, priorizando la comunicación visual y escrita mediante textos claros, componentes de interfaz reconocibles y navegación simple.

---

## Tecnologías utilizadas

- Kotlin
- Android Studio
- Jetpack Compose
- Material Design 3
- Navigation Compose
- Git
- GitHub

---

## Funcionalidades desarrolladas

La aplicación actualmente incluye:

- Inicio de sesión.
- Registro de usuarios.
- Recuperación de contraseña.
- Navegación entre vistas.
- Catálogo de productos.
- Productos almacenados localmente.
- Filtro por categoría.
- Filtro por distancia.
- Filtro por estado del producto.
- Tabla resumen del catálogo.
- Grilla adaptable de productos.
- Validación de formularios.
- Preferencia de comunicación mediante RadioButton.
- Aceptación de términos mediante Checkbox.
- Selector de región mediante ComboBox.
- Mensajes de error y confirmación.
- Diseño adaptable mediante Jetpack Compose.

---

# Semana 5 – Actividad Sumativa 2

## Integrando Kotlin a la aplicación móvil con Android Studio

Durante la Semana 5 se incorporaron y reforzaron funcionalidades básicas y avanzadas del lenguaje de programación **Kotlin**.

El objetivo de esta etapa es aplicar funciones, colecciones, lambdas, funciones de orden superior, funciones de extensión y manejo de excepciones dentro de la aplicación Trueke.

---

## Colecciones Kotlin

### Array de usuarios

Se implementó un `Array<User>` utilizando `arrayOf()` que contiene cinco usuarios previamente registrados junto con sus contraseñas.

```kotlin
val initialUsers: Array<User> = arrayOf(
    User(...),
    User(...),
    User(...),
    User(...),
    User(...)
)
