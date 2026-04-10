package co.edu.unicauca.cliente.controladores;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class UsuarioCllbckImpl extends UnicastRemoteObject implements UsuarioCllbckInt
{
    
    public UsuarioCllbckImpl() throws RemoteException
    {
        super();		
    }

    @Override
    public void notificar(String mensaje, int cantidadUsuarios, String remitente) throws RemoteException
    {
        // System.out.println("Mensaje enviado del servidor: " + mensaje);       
        // System.out.println("Cantidad de usuarios conectados: " + cantidadUsuarios );
        // System.out.println("Remitente: " + remitente);
        // Formatear el mensaje de notificación [nicname]: [mensaje] (Usuarios conectados: [cantidadUsuarios])
        String mensajeFormateado = String.format("[%s]: %s (Usuarios conectados: %d)", remitente, mensaje, cantidadUsuarios);
        System.out.println(mensajeFormateado);
    }

}
