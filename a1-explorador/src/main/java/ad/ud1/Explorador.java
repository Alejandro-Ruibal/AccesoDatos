package ad.ud1;
 
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
 
/**
 * ACTIVIDAD 1: Explorador de directorios con java.nio.file
 * 
 * COMO SE EJECUTA:
 *   mvn -q compile exec:java
 *   mvn -q compile exec:java -Dexec.args="prueba -r"
 */
public class Explorador {
 
    private static final DateTimeFormatter FECHA = DateTimeFormatter
            .ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneId.systemDefault());
    
    // Contadores estadísticos requeridos en el Paso 8
    private static int ficheros, directorios, sinAcceso;     
    private static long bytes;                               
 
    public static void main(String[] args) {
        // PASO 2: Leer argumentos (ruta por defecto "." y opción -r)
        String textoRuta = ".";
        boolean recursivo = false;
        
        for (String a : args) {
            if (a.equals("-r")) {
                recursivo = true;
            } else {
                textoRuta = a;
            }
        }
        
        // Construir la ruta absoluta y normalizada para evitar fallos de relativize
        Path raiz = Path.of(textoRuta).toAbsolutePath().normalize();
        System.out.println("Explorando " + raiz);

        // PASO 3: Validar la existencia de la ruta
        if (!Files.exists(raiz)) {
            System.err.println("ERROR: no existe " + raiz);
            System.exit(1);
        }

        // Si es un archivo individual, mostrarlo directamente y terminar
        if (!Files.isDirectory(raiz)) {
            System.out.println(linea(raiz, raiz.getParent()));
            return;
        }

        // PASO 4 y 6: Abrir el Stream con try-with-resources para el cierre automático de recursos
        try (Stream<Path> rutas = recursivo ? Files.walk(raiz).skip(1) 
                                            : Files.list(raiz)) {
            
            // Si no es recursivo se ordena por nombre; si es walk, mantenemos el orden natural
            (recursivo ? rutas : rutas.sorted())
                .forEach(p -> {
                    String l = linea(p, raiz);
                    if (!l.isEmpty()) {
                        System.out.println(l);
                    }
                });

        } catch (NoSuchFileException e) {
            System.err.println("ERROR: El archivo o carpeta desapareció: " + e.getFile());
            System.exit(1);
        } catch (AccessDeniedException e) {
            System.err.println("ERROR: Sin permisos de acceso a la raíz: " + e.getFile());
            System.exit(1);
        } catch (UncheckedIOException e) {
            System.err.println("ERROR: Recorrido interrumpido por fallo de lectura a mitad: " + e.getCause());
            System.exit(1);
        } catch (IOException e) {
            System.err.println("ERROR de Entrada/Salida: " + e.getMessage());
            System.exit(1);
        }

        // PASO 8: Imprimir el resumen final solicitado
        System.out.printf("%n%d ficheros · %d directorios · %d bytes · %d sin acceso%n", 
                ficheros, directorios, bytes, sinAcceso);
    }
 
    static String linea(Path p, Path base) {
        try {
            // PASO 5: Calcular permisos en formato rwx
            String permisos = (Files.isReadable(p) ? "r" : "-")
                            + (Files.isWritable(p) ? "w" : "-")
                            + (Files.isExecutable(p) ? "x" : "-");

            // Obtener fecha de última modificación y formatearla
            String fechaStr = FECHA.format(Files.getLastModifiedTime(p).toInstant());

            // PASO 6: Calcular la sangría dinámica según el nivel de profundidad
            int nivel = base.relativize(p).getNameCount();
            String sangria = "  ".repeat(nivel - 1);

            String nombre = p.getFileName().toString();

            // PASO 8: Incrementar contadores globales y gestionar tamaños
            if (Files.isDirectory(p)) {
                directorios++;
                return String.format("%s %-12s %11s  %s%s/", permisos, "", fechaStr, sangria, nombre);
            } else {
                ficheros++;
                long tamano = Files.size(p);
                bytes += tamano; // Sumar al total de bytes de la auditoría
                return String.format("%s %-12d %11s  %s%s", permisos, tamano, fechaStr, sangria, nombre);
            }

        } catch (IOException e) {
            // PASO 7: Tratamiento de errores en elementos sin acceso
            sinAcceso++;
            String nombreError = p.getFileName().toString();
            return String.format("--- %-12s %11s  [%s: %s]", "", "", nombreError, e.getClass().getSimpleName());
        }
    }
}
