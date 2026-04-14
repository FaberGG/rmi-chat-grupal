package co.edu.unicauca.cliente.utilidades;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase utilitaria para leer propiedades desde el archivo config.properties.
 * PUNTO i: Permite obtener la dirección IP y el puerto del NS (rmiRegistry)
 * desde un archivo de configuración externo, en lugar de tenerlos hardcoded.
 *
 * El archivo config.properties debe estar ubicado en:
 *   Cliente/src/main/resources/config.properties
 *
 * Contenido esperado del archivo:
 *   ns.ip=localhost
 *   ns.puerto=8080
 */
public class UtilidadesProperties
{
    // Nombre del archivo de propiedades que se busca en el classpath
    private static final String ARCHIVO_PROPERTIES = "config.properties";

    // Objeto Properties que almacena las claves y valores cargados del archivo
    private static Properties propiedades = null;

    /**
     * Carga el archivo config.properties desde el classpath (resources).
     * Se llama automáticamente la primera vez que se necesita una propiedad.
     */
    private static void cargarPropiedades()
    {
        propiedades = new Properties();

        try (InputStream stream = UtilidadesProperties.class
                .getClassLoader()
                .getResourceAsStream(ARCHIVO_PROPERTIES))
        {
            if (stream == null)
            {
                System.err.println("ERROR: No se encontró el archivo '" + ARCHIVO_PROPERTIES + "' en resources.");
                System.err.println("Cree el archivo en: Cliente/src/main/resources/config.properties");
                System.exit(1);
            }

            propiedades.load(stream);
            System.out.println("Archivo de configuración '" + ARCHIVO_PROPERTIES + "' cargado correctamente.");
        }
        catch (IOException e)
        {
            System.err.println("ERROR al leer el archivo de configuración: " + e.getMessage());
            System.exit(1);
        }
    }

    /**
     * Retorna el valor de una propiedad del archivo config.properties.
     *
     * @param clave Nombre de la propiedad a buscar (ej: "ns.ip", "ns.puerto")
     * @return Valor de la propiedad como String
     */
    public static String obtenerPropiedad(String clave)
    {
        if (propiedades == null)
        {
            cargarPropiedades();
        }

        String valor = propiedades.getProperty(clave);

        if (valor == null)
        {
            System.err.println("ADVERTENCIA: La propiedad '" + clave + "' no fue encontrada en " + ARCHIVO_PROPERTIES);
        }

        return valor;
    }
}
