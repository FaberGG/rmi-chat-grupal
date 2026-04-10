
package co.edu.unicauca.servidor.servicios;

import java.rmi.RemoteException;
import java.util.concurrent.CountDownLatch;

import co.edu.unicauca.servidor.controladores.ControladorServidorChatImpl;
import co.edu.unicauca.servidor.utilidades.UtilidadesRegistroS;

public class ServidorDeObjetos
{
    public static void main(String args[]) throws RemoteException
    {        
         
        int numPuertoRMIRegistry = 8080;
        String direccionIpRMIRegistry = "localhost";
                       
        // System.out.println("Cual es el la dirección ip donde se encuentra  el rmiRegistry ");
        // direccionIpRMIRegistry = UtilidadesConsola.leerCadena();
        // System.out.println("Cual es el número de puerto por el cual escucha el rmiRegistry ");
        // numPuertoRMIRegistry = UtilidadesConsola.leerEntero(); 
        
        //imprimir detalles de conexion
        System.out.println("Iniciando el servidor en la dirección IP: " + direccionIpRMIRegistry + " y puerto: " + numPuertoRMIRegistry);

        ControladorServidorChatImpl objRemoto = new ControladorServidorChatImpl();//se leasigna el puerto de escucha del objeto remoto
        
        try
        {
           UtilidadesRegistroS.arrancarNS(numPuertoRMIRegistry);
              UtilidadesRegistroS.RegistrarObjetoRemoto(objRemoto, direccionIpRMIRegistry, numPuertoRMIRegistry, "ServidorChat");
              System.out.println("Servidor listo. Presione Ctrl + C para finalizar.");
              new CountDownLatch(1).await();
              //confirmar conexion
                System.out.println("Servidor conectado exitosamente al RMI Registry en " + direccionIpRMIRegistry + ":" + numPuertoRMIRegistry);
           
        } catch (Exception e)
        {
            System.err.println("No fue posible Arrancar el NS o Registrar el objeto remoto" +  e.getMessage());
        }
        
        
    }
}
