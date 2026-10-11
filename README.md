# TRUEKE

Aplicación Android desarrollada para **Desarrollo de Aplicaciones Móviles (DSY2204)** de **DUOC UC**.

TRUEKE permite publicar y consultar productos para coordinar intercambios entre usuarios. El catálogo muestra categoría, estado, valor referencial y distancia, con filtros para facilitar la búsqueda.

Repositorio: [felsalasc/trueke-android](https://github.com/felsalasc/trueke-android).

## Funciones implementadas

- Registro e inicio de sesión con Firebase Authentication.
- Recuperación de contraseña mediante un enlace enviado por Firebase al correo registrado.
- Persistencia de perfiles en Cloud Firestore y saludo con el nombre del usuario.
- Restauración de sesión al abrir la aplicación y cierre de sesión con retorno al acceso.
- Catálogo de productos con lectura en tiempo real desde Firestore.
- Creación, edición y eliminación de productos. Solo el propietario puede editar o eliminar sus publicaciones.
- Filtros por categoría, distancia máxima y buen estado.
- Formulario con listas desplegables, validaciones y bloqueo mientras se guarda.
- Contacto mediante una aplicación de correo instalada: se prepara destinatario, asunto y mensaje; el usuario decide si lo envía.
- Resumen del catálogo, grilla adaptable y formulario de productos desplazable.
- Registro con selección de región, preferencia de comunicación y aceptación de términos.
- Comunicación accesible desde Inicio: escribir un mensaje con texto grande, mostrarlo a otra persona, reproducirlo en español mediante TextToSpeech, detener la reproducción y convertir voz a texto mediante el reconocedor disponible en Android. Incluye instrucciones breves y retorno al catálogo.
- Historial privado de comunicación: guardado automático de solicitudes y respuestas exitosas y consulta de los últimos 20 registros, con tipo de función y fecha.

### Opciones del catálogo

| Campo | Opciones |
|---|---|
| Categoría | Deportes, Videojuegos, Instrumentos, Tecnología, Fotografía y Otros |
| Estado | Excelente, Buen estado y Usado |
| Distancia del formulario | 2, 5 y 10 km |

El filtro de categoría agrega **Todas**. El filtro de buen estado incluye **Excelente** y **Buen estado**. La distancia máxima incluye productos cuya distancia es menor o igual al límite seleccionado.

La distancia es declarada por el usuario; actualmente no se calcula con GPS. Al editar, se conservan los valores antiguos que no pertenecen a las nuevas listas.

El contacto se muestra en productos ajenos. Si falta un correo válido o no hay una aplicación de correo compatible, TRUEKE muestra un aviso. Las conversaciones se realizan fuera de TRUEKE y no se almacenan en Firestore.

## Tecnologías

- Kotlin y Android Studio.
- Jetpack Compose, Material Design 3 y Navigation Compose.
- Firebase Authentication y Cloud Firestore.
- Gradle Wrapper y catálogo de versiones en `gradle/libs.versions.toml`.
- JUnit 4, Git y GitHub.

La configuración actual utiliza Android SDK 37 para compilación y destino, con Android 7.0 (API 24) como versión mínima. Las versiones de las dependencias están declaradas en el proyecto.

## Organización del código

El código principal se encuentra en `app/src/main/java/com/example/trueke/`:

| Ubicación | Responsabilidad |
|---|---|
| `MainActivity.kt` | Punto de entrada |
| `navigation/AppNavigation.kt` | Rutas y navegación de sesión |
| `ui/screens/` | Acceso, registro, recuperación y catálogo |
| `ui/screens/CommunicationDialog.kt` | Escritura a voz y voz a texto mediante servicios de Android |
| `ui/theme/` | Tema, colores y tipografía |
| `model/User.kt` | Datos del perfil, sin contraseña |
| `model/Product.kt` | Datos del producto y propiedad por UID |
| `data/ProductDataSource.kt` | Interfaz de las operaciones del catálogo |
| `data/ProductRepository.kt` | Implementación de lectura y CRUD con Firebase |
| `model/CommunicationRecord.kt` | Solicitud, respuesta, tipo de función y fecha del historial |
| `data/CommunicationRepository.kt` | Guardado y consulta del historial privado en Firestore |
| `data/UserRepository.kt` | Colecciones de perfiles de ejemplo, sin credenciales; no realizan la autenticación |
| `utils/` | Filtrado, validación numérica y extensión de correo |

`HomeScreen` recibe un `ProductDataSource` y utiliza `ProductRepository` como implementación predeterminada. Los resultados se entregan mediante callbacks porque Firebase trabaja de forma asíncrona. La pantalla elimina la suscripción al catálogo cuando deja de utilizarla.

### Evidencias de Kotlin

- **Clases y propiedades:** modelos `User` y `Product`.
- **Objetos, visibilidad e interfaz:** `ProductRepository` implementa `ProductDataSource` mediante `override` y mantiene detalles internos privados.
- **Herencia:** `MainActivity` hereda de `ComponentActivity`.
- **Colecciones y ciclos:** listas, `mapNotNull`, `filter`, `forEach` y colecciones de ejemplo en `UserRepository`.
- **Lambdas, función de orden superior e inline:** `filterProducts` recibe una condición `(Product) -> Boolean` y está declarada como `inline`.
- **Extensión:** `String.isValidEmail()`.
- **Condicionales y operadores:** validaciones, filtros y permisos de edición.
- **Try/catch:** lectura de documentos y apertura de la aplicación de correo.
- **Try/finally:** `completeProductSave` en `HomeScreen` procesa la respuesta de creación o edición y libera el estado de guardado en `finally`, incluso si el procesamiento del resultado lanza una excepción. Se invoca desde los callbacks, después de recibir el resultado de Firebase.

## Firebase y datos

Se necesita una configuración válida de Firebase para `com.example.trueke`, con Authentication mediante correo y contraseña habilitado y Cloud Firestore disponible. El archivo de configuración Android utilizado es `app/google-services.json`.

| Colección | Datos |
|---|---|
| `users/{uid}` | `name`, `email`, `region`, `communicationPreference` |
| `products/{productId}` | `id`, `name`, `description`, `category`, `condition`, `referenceValue`, `distanceKm`, `owner`, `ownerUid` |
| `users/{uid}/communicationHistory/{recordId}` | `mode`, `request`, `response`, `createdAt` |

Firebase Authentication administra las contraseñas. El perfil no contiene un campo de contraseña. En publicaciones nuevas, `owner` contiene el correo del publicador y `ownerUid` identifica su cuenta.

El archivo `firestore.rules` contiene las reglas locales de acceso y validación:

- Cada usuario accede a su propio perfil.
- Los usuarios autenticados pueden leer el catálogo.
- La creación exige que `ownerUid` corresponda al usuario autenticado.
- La edición y eliminación requieren propiedad del documento. La edición conserva los datos del propietario.
- Se validan campos, tipos y valores numéricos; el resto de las rutas se deniega.
- Cada cuenta puede crear y leer únicamente su historial. Los registros requieren textos de 1 a 4000 caracteres, tipo `write` o `speak` y fecha del servidor. Esta versión no permite editar ni eliminar registros.

Editar este archivo no publica automáticamente las reglas: deben aplicarse al proyecto correspondiente desde Firebase. Los productos antiguos sin `ownerUid` no habilitan edición ni eliminación desde la aplicación.

Las claves privadas, contraseñas de firma y archivos de cuentas de servicio deben mantenerse fuera del repositorio. `.gitignore` excluye archivos JKS, keystore y la carpeta `app/release/`.

## Ejecutar y compilar

1. Clonar el repositorio y abrir la carpeta del proyecto en Android Studio.
2. Instalar el SDK requerido y utilizar un JDK compatible con el Gradle Wrapper; en el entorno de desarrollo se utiliza el JBR de Android Studio.
3. Comprobar la configuración de Firebase y sincronizar Gradle. La primera sincronización puede requerir conexión para descargar dependencias.
4. Seleccionar un emulador o dispositivo con API 24 o superior, conexión a internet y ejecutar `app`.

Desde PowerShell, en la raíz del proyecto:

```powershell
.\gradlew.bat :app:assembleDebug
```

El APK de desarrollo se genera en `app/build/outputs/apk/debug/app-debug.apk` y utiliza firma de depuración. La versión de distribución 1.0 se generó mediante **Build → Generate Signed App Bundle / APK**, seleccionando `release` y una clave privada excluida del repositorio. El APK resultante, `app/release/app-release.apk`, pasó la verificación con `apksigner` mediante el esquema de firma v2. El desarrollador confirmó su instalación y funcionamiento en un teléfono.

La clave de firma y sus contraseñas deben conservarse como respaldo privado. Para actualizar una instalación existente se necesita una firma compatible con la versión instalada.

## Pruebas

Ejecutar las pruebas unitarias:

```powershell
.\gradlew.bat :app:testDebugUnitTest
```

El conjunto actual contiene **23 pruebas unitarias**:

| Archivo | Pruebas | Cobertura |
|---|---:|---|
| `ExampleUnitTest.kt` | 5 | Filtros por categoría, distancia, buen estado, combinación y ausencia de resultados |
| `ProductOwnershipTest.kt` | 6 | Propiedad por UID, sesión ausente y productos antiguos |
| `ProductValidationTest.kt` | 12 | Enteros, límites, distancia decimal y rechazo de negativos o valores no finitos |

Los reportes se generan en `app/build/reports/tests/testDebugUnitTest/`. Estas pruebas verifican lógica local; no validan las reglas desplegadas ni las operaciones reales de Firebase.

Existe además `ExampleInstrumentedTest.kt`, una comprobación básica del paquete que requiere un dispositivo o emulador. No constituye una prueba completa de los flujos.

### Comprobación manual con dos cuentas

Verificar registro, acceso incorrecto y correcto, recuperación, saludo, restauración y cierre de sesión. Publicar, editar y eliminar productos de prueba; comprobar persistencia y filtros. Con la segunda cuenta, verificar que los productos ajenos no permitan editar ni eliminar. Comprobar que el contacto abre un borrador con los datos completos y que el formulario permite alcanzar sus acciones en una pantalla pequeña.

## Pendientes para la evaluación final

- Completar la comprobación del aislamiento del historial con dos cuentas. Las reglas del historial ya fueron publicadas y se verificó el guardado y lectura reales con una cuenta.
- Disponer un enlace público de descarga del APK firmado.
- Preparar la presentación técnica, el video y el ZIP de entrega.

La reproducción requiere un servicio de texto a voz con español disponible y volumen multimedia audible. Si falta el servicio o el idioma, se muestra un aviso y el texto sigue disponible. La voz se detiene y sus recursos se liberan al cerrar el diálogo.

El reconocimiento abre una aplicación compatible mediante `RecognizerIntent` y recibe el texto mediante Activity Result. Esa aplicación administra el micrófono y puede requerir permisos y conexión a internet; TRUEKE no solicita directamente acceso al micrófono en esta implementación. Un resultado válido reemplaza el mensaje visible y puede corregirse antes de reproducirlo. Al cancelar, fallar o no obtener texto, se conserva el mensaje anterior. Si falta una aplicación compatible, se muestra un aviso.

El historial guarda el mensaje escrito y la confirmación cuando termina su reproducción. Para voz a texto, guarda la solicitud «Convertir voz a texto» y la transcripción recibida. La fecha se asigna con `FieldValue.serverTimestamp()`. No se almacenan grabaciones de audio, errores ni cancelaciones. Los textos reconocidos de más de 4000 caracteres permanecen visibles, pero no se guardan y se muestra un aviso. La consulta muestra los últimos 20 registros; los anteriores permanecen almacenados. Un error de guardado se informa por separado y no convierte una operación de voz exitosa en un éxito de persistencia.
