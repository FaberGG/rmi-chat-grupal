package co.edu.unicauca.servidor.controladores;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import co.edu.unicauca.cliente.controladores.UsuarioCllbckInt;

public class ControladorServidorChatImpl extends UnicastRemoteObject implements ControladorServidorChatInt {

    private  Map<String, UsuarioCllbckInt> usuarios;//lista que almacena la referencia remota de los clientes

    public ControladorServidorChatImpl() throws RemoteException
    {
        super();//asignamos el puerto 
        usuarios = new java.util.HashMap<>();
    }
    
    @Override
    public synchronized boolean  registrarReferenciaUsuario(UsuarioCllbckInt usuario, String nickname) throws RemoteException 
    {
       //método que unicamente puede ser accedido por un hilo
	System.out.println("Invocando al método registrar usuario desde el servidor");
        boolean bandera=false;
        if (!usuarios.containsKey(nickname))
        {
            bandera=usuarios.put(nickname, usuario) == null;
            System.out.println("Usuario registrado: " + nickname);
        }else{
            System.out.println("El nickname " + nickname + " ya está en uso. Elija otro.");
        }  
        return bandera;       
    }
   
    @Override
    public void enviarMensaje(String mensaje, String remitente) throws RemoteException 
    {        
        notificarUsuarios(mensaje, remitente);
    }
    
    private void notificarUsuarios(String mensaje, String remitente) throws RemoteException 
    {
        System.out.println("Invocando al método notificar usuarios desde el servidor");
        for(UsuarioCllbckInt objUsuario: usuarios.values())
        {
            objUsuario.notificar(mensaje, usuarios.size(), remitente);//el servidor hace el callback
            
        }
    }

    @Override
    public List<String> mostrarUsuarios() throws RemoteException {
        List<String> listaUsuarios = new ArrayList<>();
        for (String nickname : usuarios.keySet()) {
            listaUsuarios.add(nickname); // Agrega el nombre de usuario a la lista
        }
        return listaUsuarios;
    }

    @Override
    public void cerrarConexion(String nickname) throws RemoteException {
        if (usuarios.containsKey(nickname)) {
            usuarios.remove(nickname);
            System.out.println("Usuario desconectado: " + nickname);
            notificarUsuarios("El usuario " + nickname + " se ha desconectado.", "Servidor");
        } else {
            System.out.println("No se encontró el usuario: " + nickname);
        }
    }

    @Override
    public void enviarMensajeDirecto(String mensaje, String remitente, String destinatario) throws RemoteException {
        if (usuarios.containsKey(destinatario)) {
            UsuarioCllbckInt usuarioDestinatario = usuarios.get(destinatario);
            usuarioDestinatario.notificar(mensaje, usuarios.size(), remitente);
            System.out.println("Mensaje directo enviado de " + remitente + " a " + destinatario);
        } else {
            System.out.println("No se encontró el destinatario: " + destinatario);
            UsuarioCllbckInt usuarioRemitente = usuarios.get(remitente);
            if (usuarioRemitente != null) {
                usuarioRemitente.notificar("El destinatario " + destinatario + " no está conectado.", usuarios.size(), "Servidor");
            }
        }
    }
}
