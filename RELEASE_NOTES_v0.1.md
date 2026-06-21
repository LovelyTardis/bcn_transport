# Notas de Lanzamiento - Versión 0.1.0 🚀

¡Bienvenido a la primera versión funcional de **bcn_transport** para Wear OS! Esta versión sienta las bases de una aplicación rápida, eficiente y nativa para seguir los tiempos de llegada del transporte público de Barcelona directamente desde tu muñeca.

---

## 📱 ¿Qué es bcn_transport?
Es una aplicación diseñada específicamente para dispositivos **Wear OS** utilizando **Jetpack Compose**. Permite visualizar los tiempos de llegada y predicciones en tiempo real de la línea **L1 del Metro de Barcelona (TMB)** y de la línea **S1 de los Ferrocarriles de la Generalitat de Catalunya (FGC)**.

---

## ✨ Características Principales en la v0.1.0

### 1. Interfaz de Usuario Nativa y Optimizada para Relojes
* **Jetpack Compose Wear OS:** Interfaz fluida y adaptada a pantallas circulares y cuadradas.
* **Soporte para Corona Rotatoria (Rotary Scroll):** Permite navegar y desplazarse de manera natural por las listas utilizando la corona física o el bisel giratorio de tu smartwatch.
* **Navegación gestual nativa:** Soporte para gestos nativos de Wear OS (`SwipeDismissableNavHost`) para volver atrás deslizando el dedo.

### 2. Soporte Multitransporte (Metro y Ferrocarril)
* **Metro de Barcelona (TMB - L1):** Tiempos de llegada reales para las estaciones de la línea L1 (desde Hospital de Bellvitge hasta Fondo).
* **Ferrocarriles (FGC - S1):** Tiempos de espera y posiciones reales de trenes para la línea S1 (entre Barcelona - Pl. Catalunya y Terrassa Nacions Unides).

### 3. Geolocalización Inteligente 📍
* **Selección por Proximidad:** Al abrir la aplicación o pulsar el botón de GPS (📍), el sistema detecta tu ubicación actual y preselecciona automáticamente la estación de Metro o FGC más cercana.
* **Optimización de Consulta de GPS:** Diseñado para gestionar la antena GPS del reloj de forma eficiente sin drenar la batería.

### 4. Modo Híbrido (REST APIs Reales / Simulación Offline)
* **Conexión API Real:** Consumo directo de datos de producción desde las APIs oficiales de Open Data de TMB y FGC.
* **Modo Demo (Mock):** Un interruptor en la pantalla principal que te permite alternar a datos simulados y realistas de forma local. Ideal para pruebas en interiores o sin conexión a internet activa.
* **Configuración Segura:** Credenciales protegidas mediante variables de entorno `.env` integradas en el compilado final.
* **Robustez (Graceful Fallback):** Si las credenciales no están de alta en el entorno, la aplicación cambia de forma inteligente y segura al modo simulación (Mock) para evitar interrupciones o errores.

### 5. Control Inteligente de Tasa de Refresco (Ahorro de Batería) 🔋
* **Algoritmo de Frecuencia Variable:** La aplicación monitoriza continuamente el tiempo restante del próximo tren.
* Si el tren está a menos de 16 segundos de entrar en la estación, el sistema activa automáticamente un refresco rápido cada 20 segundos.
* En momentos donde no hay trenes cercanos, mantiene las consultas desactivadas para no desgastar innecesariamente la batería ni consumir datos del reloj.

### 6. Detección de Fin de Servicio y Horarios Nocturnos 🌙
* Comprueba automáticamente si el servicio de transporte seleccionado ya ha cerrado según el día de la semana.
* Muestra una tarjeta visual de **"Fin de servicio"** informando sobre el horario esperado del próximo primer tren de la mañana (ej. `05:00 h`).

---

## 🛠️ Detalles Técnicos
* **Framework:** Kotlin, Jetpack Compose para Wear OS.
* **Redes:** Retrofit 2 con OkHttpClient para el procesamiento de llamadas HTTP.
* **Ubicación:** Google Play Services Location (`FusedLocationProviderClient`).
* **Estado:** Manejo de flujo unidireccional de datos mediante `StateFlow` y estados de UI sellados (`Loading`, `Success`, `Error`).
* **Seguridad:** Variables sensibles en `.env` inyectadas en `BuildConfig`.

---

## 📝 Notas de Configuración para Desarrolladores
Para compilar con acceso a datos reales:
1. Crea un archivo `.env` en la raíz del proyecto.
2. Añade tus credenciales de la API de TMB:
   ```env
   TMB_APP_ID=tu_app_id
   TMB_APP_KEY=tu_app_key
   ```
3. Si el archivo no existe o las variables están vacías, la app se iniciará automáticamente en **Modo Demo (Mock)** sin necesidad de credenciales.
