package co.edu.unicauca.cliente.controladores;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Implementación del callback del cliente.
 * Esta clase es el objeto remoto que el cliente registra en el servidor.
 * El servidor usa esta referencia para enviarle mensajes al cliente (callback).
 *
 * Extiende UnicastRemoteObject para ser exportado como objeto remoto RMI.
 */
public class UsuarioCllbckImpl extends UnicastRemoteObject implements UsuarioCllbckInt
{
    /**
     * Constructor que exporta el objeto remoto para que el servidor pueda invocarlo.
     * @throws RemoteException si ocurre un error al exportar el objeto
     */
    public UsuarioCllbckImpl() throws RemoteException
    {
        super(); // Exporta el objeto con un puerto anónimo para recibir callbacks
    }

    /**
     * Método invocado remotamente por el servidor para notificar un mensaje al cliente.
     * Recibe el mensaje y lo muestra en la consola del cliente con formato legible.
     *
     * @param mensaje          Contenido del mensaje recibido desde el servidor
     * @param cantidadUsuarios Número de usuarios activos al momento del envío
     * @param remitente        NickName del usuario que envió el mensaje (o "Servidor")
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    @Override
    public void notificar(String mensaje, int cantidadUsuarios, String remitente) throws RemoteException
    {
        // Se formatea el mensaje para mostrarlo de forma clara en la consola del cliente
        // Formato: [remitente]: mensaje (Usuarios conectados: N)
        String mensajeFormateado = String.format(
            "[%s]: %s (Usuarios conectados: %d)",
            remitente,
            mensaje,
            cantidadUsuarios
        );

        // Se imprime el mensaje recibido en la consola del cliente
        System.out.println(mensajeFormateado);
    }
}