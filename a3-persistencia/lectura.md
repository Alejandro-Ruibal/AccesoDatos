# Análisis de Persistencia y Control de Excepciones - Actividad 3

## 1. Justificación del bloque try-with-resources
El uso de `try-with-resources` es indispensable en la lectura/escritura de archivos JSON y XML porque implementa la interfaz `AutoCloseable`. Esto asegura que los flujos de datos (`Readers` y `Writers`) se cierren automáticamente al terminar el bloque, evitando bloqueos del sistema operativo (ficheros abiertos en segundo plano) incluso si ocurre una excepción inesperada a mitad del proceso.

## 2. Gestión de IDs duplicados
Para evitar que se introduzcan registros con identificadores duplicados, se ha modificado el método de inserción utilizando una estructura de datos indexada basada en colecciones (un `HashMap` o un filtrado previo con Streams). Antes de añadir el nuevo objeto al archivo, se verifica si el ID ya existe con `.containsKey(id)`. Si se detecta el duplicado, se lanza una excepción personalizada controlada, abortando la escritura para garantizar la integridad de los datos.

## 3. Implementación del archivo de respaldo (.bak)
Para prevenir la pérdida de datos por corrupción de archivos durante una escritura fallida, se realiza una copia de seguridad previa de la base de datos actual. Antes de abrir el flujo de escritura sobre el archivo principal, se utiliza el método `Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING)` para renombrar y salvaguardar el JSON anterior en un archivo con extensión `.bak`.
