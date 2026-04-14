package co.edu.unicauca.servidor.controladores;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import co.edu.unicauca.cliente.controladores.UsuarioCllbckInt;

/**
 * Interfaz remota del servidor de chat.
 * Define todos los métodos que el cliente puede invocar remotamente sobre el servidor.
 */
public interface ControladorServidorChatInt extends Remote
{
    /**
     * PUNTO a y b: Registra la referencia remota del cliente junto con su nickName.
     * Valida que el nickName sea único antes de registrarlo.
     *
     * @param usuario  Referencia remota (callback) del cliente
     * @param nickname Nombre único que identifica al usuario en el chat
     * @return true si el registro fue exitoso, false si el nickName ya está en uso
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    public boolean registrarReferenciaUsuario(UsuarioCllbckInt usuario, String nickname) throws RemoteException;

    /**
     * Envía un mensaje público a todos los usuarios conectados.
     * PUNTO g: Antes de reenviar, comprueba si cada usuario sigue conectado.
     * Si un usuario terminó abruptamente, su referencia remota es eliminada.
     *
     * @param mensaje   Contenido del mensaje a enviar
     * @param remitente NickName del usuario que envía el mensaje
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    public void enviarMensaje(String mensaje, String remitente) throws RemoteException;

    /**
     * PUNTO c: Retorna la lista de nickNames de los usuarios registrados y activos.
     *
     * @return Lista de strings con los nickNames de los usuarios activos
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    public List<String> mostrarUsuarios() throws RemoteException;

    /**
     * PUNTO d: Permite al cliente salir del chat y eliminar su referencia en el servidor.
     *
     * @param nickname NickName del usuario que desea salir del chat
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    public void cerrarConexion(String nickname) throws RemoteException;

    /**
     * PUNTO e y f: Envía un mensaje privado (directo) a un usuario determinado.
     * Antes de reenviar, comprueba si el destinatario sigue conectado.
     * Si el destinatario terminó abruptamente, su referencia es eliminada y
     * se notifica al emisor con el mensaje correspondiente.
     *
     * @param mensaje      Contenido del mensaje privado
     * @param remitente    NickName del usuario que envía el mensaje
     * @param destinatario NickName del usuario que debe recibir el mensaje
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    public void enviarMensajeDirecto(String mensaje, String remitente, String destinatario) throws RemoteException;

    /**
     * PUNTO h: Consulta la cantidad de usuarios activos en el servidor de chat.
     *
     * @return Número entero con la cantidad de usuarios activos
     * @throws RemoteException si ocurre un error en la comunicación RMI
     */
    public int obtenerCantidadUsuariosActivos() throws RemoteException;
}


