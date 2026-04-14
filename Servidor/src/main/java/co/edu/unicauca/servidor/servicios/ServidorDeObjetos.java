package co.edu.unicauca.servidor.servicios;

import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

import co.edu.unicauca.servidor.controladores.ControladorServidorChatImpl;
import co.edu.unicauca.servidor.utilidades.UtilidadesRegistroS;
import co.edu.unicauca.servidor.utilidades.UtilidadesProperties;

/**
 * Clase principal del servidor de chat.
 * PUNTO i: La dirección IP y el puerto del Name Server (NS / rmiRegistry)
 * se obtienen desde el archivo de configuración "config.properties",
 * en lugar de estar escritos directamente en el código (hardcoded).
 */
public class ServidorDeObjetos
{
    public static void main(String args[]) throws RemoteException
    {
        // PUNTO i: Se cargan la IP y el puerto desde el archivo properties
        // El archivo config.properties debe estar en src/main/resources/
        String direccionIpRMIRegistry = UtilidadesProperties.obtenerPropiedad("ns.ip");
        int numPuertoRMIRegistry = Integer.parseInt(UtilidadesProperties.obtenerPropiedad("ns.puerto"));

        // Se muestran los datos de conexión cargados desde el archivo
        System.out.println("=== Servidor de Chat RMI ===");
        System.out.println("Configuración cargada desde config.properties:");
        System.out.println("  IP del NS     : " + direccionIpRMIRegistry);
        System.out.println("  Puerto del NS : " + numPuertoRMIRegistry);
        System.out.println("Iniciando servidor...");

        // Se crea la implementación del objeto remoto del servidor
        ControladorServidorChatImpl objRemoto = new ControladorServidorChatImpl();

        try
        {
            // Se arranca el rmiRegistry en el puerto configurado
            UtilidadesRegistroS.arrancarNS(numPuertoRMIRegistry);

            // Se registra el objeto remoto en el rmiRegistry con el nombre "ServidorChat"
            UtilidadesRegistroS.RegistrarObjetoRemoto(objRemoto, direccionIpRMIRegistry, numPuertoRMIRegistry, "ServidorChat");

            System.out.println("Servidor listo. Esperando conexiones de clientes...");
            System.out.println("Presione Ctrl + C para finalizar el servidor.");

            // Mantiene el servidor activo indefinidamente usando CountDownLatch
            // (el hilo principal queda bloqueado esperando, el servidor sigue corriendo)
            new CountDownLatch(1).await();
        }
        catch (Exception e)
        {
            System.err.println("Error al arrancar el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
