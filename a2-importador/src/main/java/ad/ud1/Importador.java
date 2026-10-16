package ad.ud1;

import java.io.*;
import java.nio.file.*;
import java.util.regex.Pattern;

/**
 * ACTIVIDAD 2: Importador y validador de archivos CSV de clientes.
 */
public class Importador {

    // Expresiones regulares para validar DNI (8 números y 1 letra) y Código Postal (5 números)
    private static final Pattern PATRON_DNI = Pattern.compile("^\\d{8}[A-Z]$");
    private static final Pattern PATRON_CP = Pattern.compile("^\\d{5}$");

    public static void main(String[] args) {
        // Si no se pasa un archivo por argumento, usamos uno por defecto
        String archivoOrigen = args.length > 0 ? args[0] : "clientes.csv";
        
        Path rutaOrigen = Path.of(archivoOrigen).toAbsolutePath().normalize();
        Path rutaCorrectos = Path.of("clientes_correctos.csv").toAbsolutePath().normalize();
        Path rutaErrores = Path.of("clientes_errores.csv").toAbsolutePath().normalize();

        System.out.println("Leyendo archivo desde: " + rutaOrigen);

        // Crear un archivo de prueba automático si el de origen no existe para que el programa no falle
        if (!Files.exists(rutaOrigen)) {
            crearArchivoPrueba(rutaOrigen);
        }

        int validos = 0, invalidos = 0;

        // Estructura try-with-resources para asegurar el cierre automático de todos los flujos de archivos
        try (BufferedReader br = Files.newBufferedReader(rutaOrigen);
             BufferedWriter bwCorrectos = Files.newBufferedWriter(rutaCorrectos);
             BufferedWriter bwErrores = Files.newBufferedWriter(rutaErrores)) {

            String linea;
            // Leer línea a línea el archivo CSV
            while ((linea = br.readLine()) != null) {
                if (linea.isBlank()) continue;

                // Separar los campos por el carácter de la coma
                String[] campos = linea.split(",");

                // Verificar si faltan campos esenciales (mínimo DNI en posición 0 y CP en posición 3)
                if (campos.length < 4) {
                    bwErrores.write(linea + " -> ERROR: Faltan campos esenciales\n");
                    invalidos++;
                    continue;
                }

                String dni = campos[0].trim().toUpperCase();
                String codigoPostal = campos[3].trim();

                // Validar formatos usando las expresiones regulares
                boolean dniValido = PATRON_DNI.matcher(dni).matches();
                boolean cpValido = PATRON_CP.matcher(codigoPostal).matches();

                if (dniValido && cpValido) {
                    bwCorrectos.write(linea + "\n");
                    validos++;
                } else {
                    String motivo = (!dniValido ? "DNI Inválido " : "") + (!cpValido ? "CP Inválido" : "");
                    bwErrores.write(linea + " -> ERROR: " + motivo.trim() + "\n");
                    invalidos++;
                }
            }

            System.out.println("\n🎉 ¡Proceso finalizado con éxito!");
            System.out.println("✔️ Clientes válidos exportados: " + validos);
            System.out.println("❌ Clientes con errores detectados: " + invalidos);

        } catch (IOException e) {
            System.err.println("ERROR crítico de lectura/escritura: " + e.getMessage());
        }
    }

    private static void crearArchivoPrueba(Path ruta) {
        try {
            Files.writeString(ruta, 
                "12345678Z,Carlos,Gomez,28001\n" +   // Válido
                "XYZ12345,Ana,Martinez,08002\n" +   // DNI Malo
                "87654321X,Luis,Perez,280A3\n" +     // CP Malo
                "55555555M,Maria,Lopez,41012\n"      // Válido
            );
            System.out.println("✨ Creado archivo 'clientes.csv' de prueba con datos simulados.");
        } catch (IOException e) {
            System.err.println("No se pudo crear el archivo de prueba simulado.");
        }
    }
}
