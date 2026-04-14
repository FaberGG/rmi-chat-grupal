package co.edu.unicauca.cliente.controladores;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interfaz remota del callback del cliente.
 * Define el método que el servidor puede invocar sobre el cliente
 * para notificarle mensajes (patrón Callback en RMI).
 *
 * Esta interfaz es usada por:
 *   - El cliente: implementa el método notificar para recibir mensajes
 *   - El servidor: invoca el método notificar para enviar mensajes al cliente
 */
public interface UsuarioCllbckInt extends Remote
{
    /**
     * Método invocado por el servidor para notificar un mensaje al cliente.
     * Es el mecanismo de callback: el servidor llama a este método en el cliente.
     *
     * @param mensaje          Contenido del mensaje recibido
     * @param cantidadUsuarios Número de usuarios activos en el momento del envío
     * @param remitente        NickName del usuario que envió el mensaje (o "Servidor")
     * @throws RemoteException si ocurre un error en la comunicación RMI
     *                         (el servidor usa esta excepción para detectar clientes caídos)
     */
    public void notificar(String mensaje, int cantidadUsuarios, String remitente) throws RemoteException;
}



