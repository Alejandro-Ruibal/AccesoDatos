package ad.ud1;

import java.io.*;
import java.nio.file.*;
import java.nio.charset.MalformedInputException;

/**
 * ACTIVIDAD 4: Conversor seguro auditado.
 * Implementa el control de archivos corruptos y previene fugas de memoria.
 */
public class Conversor {

    private static final Path ARCHIVO_LOG = Path.of("auditoria_conversor.log").toAbsolutePath().normalize();

    public static void main(String[] args) {
        System.out.println("Iniciando Conversor Seguro y Auditado...");

        // Simulamos la conversión de dos archivos: uno correcto y uno corrupto
        procesarConversion("datos_correctos.xml", "✅ Conversión XML a JSON realizada con éxito.");
        procesarConversion("datos_corruptos.xml", "❌ ERROR CRÍTICO: Estructura XML mal formada en línea 3.");
    }

    public static void procesarConversion(String nombreArchivo, String resultadoSimulado) {
        System.out.println("\nProcesando: " + nombreArchivo);

        // CORRECCIÓN LN 65: Uso de try-with-resources para evitar fugas de memoria (Memory Leaks)
        // CORRECCIÓN LN 48: Se añade 'true' en el BufferedWriter para activar el modo append y no borrar el historial
        try (BufferedWriter writer = Files.newBufferedWriter(ARCHIVO_LOG, 
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            
            // CORRECCIÓN LN 32: Bloque de control explícito para capturar datos corruptos
            if (nombreArchivo.contains("corrupto")) {
                throw new MalformedInputException(0); // Simula el fallo de lectura XML corrupto
            }

            writer.write("[" + nombreArchivo + "] " + resultadoSimulado + "\n");
            System.out.println("📝 Resultado de auditoría volcado en 'auditoria_conversor.log'.");

        } catch (MalformedInputException e) {
            System.err.println("🛡️ Seguridad: Capturado fallo por archivo XML corrupto. Evitando colapso del programa.");
            registrarErrorManual("Fallo crítico de parsing en: " + nombreArchivo);
        } catch (IOException e) {
            System.err.println("Fallo de Entrada/Salida al escribir el log: " + e.getMessage());
        }
    }

    private static void registrarErrorManual(String mensajeError) {
        try (BufferedWriter writer = Files.newBufferedWriter(ARCHIVO_LOG, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write("[AUDITORÍA FALLO] " + mensajeError + "\n");
        } catch (IOException e) {
            System.err.println("No se pudo escribir el fallo en el log.");
        }
    }
}
