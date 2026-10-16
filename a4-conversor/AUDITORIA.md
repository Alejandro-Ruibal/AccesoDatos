# Informe de Auditoría del Conversor - Actividad 4

## 🔍 Fallos Críticos Detectados en el Código de la IA

| Línea | Componente Afectado | Tipo de Error | Impacto y Descripción | Solución Aplicada |
| :--- | :--- | :--- | :--- | :--- |
| **Ln 32** | `ConversorXML` | Excepción no capturada | El programa rompe por completo si el archivo XML de origen está mal formateado o corrupto. | Se encapsula la lectura en un bloque `try-catch` capturando `SAXException` de forma explícita. |
| **Ln 48** | `FlujoSalida` | Pérdida de datos | Se utiliza un flujo de escritura que sobrescribe el archivo en cada ejecución en lugar de añadir contenido de forma incremental. | Se modifica el constructor del `FileWriter` añadiendo el parámetro booleano `true` para activar el modo *append*. |
| **Ln 65** | `CierreRecursos` | Fuga de memoria (*Memory Leak*) | No se cierran los flujos de datos abiertos en los bloques de captura secundarios, dejando descriptores de archivos abiertos en el sistema operativo. | Se reestructura todo el flujo migrando el código hacia un bloque nativo `try-with-resources`. |
