

package co.edu.unicauca.servidor.controladores;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import co.edu.unicauca.cliente.controladores.UsuarioCllbckInt;

public interface ControladorServidorChatInt extends Remote
{
    public boolean registrarReferenciaUsuario(UsuarioCllbckInt  usuario, String nickname) throws RemoteException;
    public void enviarMensaje(String mensaje, String remitente)throws RemoteException;
    // mostrar usuarios desde el cliente
    public List<String> mostrarUsuarios() throws RemoteException;
    //cerrar conexion desde el cliente
    public void cerrarConexion(String nickname) throws RemoteException;
    //enviar dm desde el cliente
    public void enviarMensajeDirecto(String mensaje, String remitente, String destinatario) throws RemoteException;
 }


