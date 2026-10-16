package ad.ud1;

import java.io.*;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;

/**
 * ACTIVIDAD 3: Sistema de persistencia seguro con copia de respaldo (.bak)
 * y control estricto de identificadores duplicados.
 */
public class Persistencia {

    private static final Path ARCHIVO_DATOS = Path.of("datos.json").toAbsolutePath().normalize();
    private static final Path ARCHIVO_RESPALDO = Path.of("datos.json.bak").toAbsolutePath().normalize();

    // Colección en memoria para simular el control de claves únicas (Paso 2 del .md)
    private static final Set<String> idsExistentes = new HashSet<>();

    public static void main(String[] args) {
        System.out.println("Iniciando módulo de persistencia...");

        // Simulamos que ya existen dos IDs cargados en el sistema
        idsExistentes.add("ID001");
        idsExistentes.add("ID002");

        // 1. Intentar registrar un elemento correcto (ID nuevo)
        guardarRegistro("ID003", "{\"nombre\": \"Producto C\", \"precio\": 25.5}");

        // 2. Intentar registrar un elemento con un ID duplicado a propósito para probar el control
        guardarRegistro("ID001", "{\"nombre\": \"Producto Duplicado\", \"precio\": 10.0}");
    }

    public static void guardarRegistro(String id, String jsonContenido) {
        System.out.println("\nIntentando guardar registro con ID: " + id);

        // REQUISITO 2: Control de IDs duplicados para proteger la integridad
        if (idsExistentes.contains(id)) {
            System.err.println("❌ ERROR DE PERSISTENCIA: El identificador '" + id + "' ya existe en el sistema. Operación abortada.");
            return;
        }

        // REQUISITO 3: Creación de la copia de respaldo (.bak) antes de escribir
        try {
            if (Files.exists(ARCHIVO_DATOS)) {
                Files.copy(ARCHIVO_DATOS, ARCHIVO_RESPALDO, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("💾 Copia de seguridad creada con éxito en: " + ARCHIVO_RESPALDO.getFileName());
            } else {
                // Si es la primera ejecución, creamos un archivo base vacío para simularlo
                Files.writeString(ARCHIVO_DATOS, "[]");
            }
        } catch (IOException e) {
            System.err.println("No se pudo generar el archivo de respaldo de seguridad: " + e.getMessage());
            return;
        }

        // REQUISITO 1: Uso estricto de try-with-resources para evitar bloqueos
        try (BufferedWriter bw = Files.newBufferedWriter(ARCHIVO_DATOS, StandardOpenOption.APPEND)) {
            
            bw.write(jsonContenido + "\n");
            idsExistentes.add(id); // Registrar el ID en memoria tras la escritura correcta
            System.out.println("✅ Registro guardado correctamente en 'datos.json'.");

        } catch (IOException e) {
            System.err.println("Fallo crítico al escribir en el archivo principal: " + e.getMessage());
        }
    }
}
