package co.edu.unicauca.servidor.controladores;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import co.edu.unicauca.cliente.controladores.UsuarioCllbckInt;

/**
 * Implementación del servidor de chat usando Java RMI con patrón Callback.
 * Esta clase implementa todos los puntos del taller:
 *   a) Registrar referencia remota del cliente junto con nickName
 *   b) Validar que el nickName sea único
 *   c) Mostrar nickNames de usuarios registrados y activos
 *   d) Salir del chat y eliminar referencia en el servidor
 *   e) Enviar mensaje a un usuario determinado (chat privado)
 *   f) Verificar conexión antes de reenviar chat privado
 *   g) Verificar conexión antes de reenviar chat público
 *   h) Consultar cantidad de usuarios activos
 *   i) La IP y puerto del NS se leen desde archivo properties (ver ServidorDeObjetos)
 */
public class ControladorServidorChatImpl extends UnicastRemoteObject implements ControladorServidorChatInt
{
    /**
     * Mapa que almacena las referencias remotas de los clientes.
     * La clave es el nickName del usuario (String).
     * El valor es la referencia remota del callback del cliente (UsuarioCllbckInt).
     */
    private Map<String, UsuarioCllbckInt> usuarios;

    /**
     * Constructor del servidor.
     * Inicializa el mapa de usuarios vacío.
     * @throws RemoteException si ocurre un error al exportar el objeto remoto
     */
    public ControladorServidorChatImpl() throws RemoteException
    {
        super(); // Exporta el objeto remoto con un puerto anónimo
        usuarios = new HashMap<>();
    }

    /**
     * PUNTO a y b: Registra la referencia remota del cliente junto con su nickName.
     * El método es synchronized para garantizar que solo un hilo lo ejecute a la vez,
     * evitando condiciones de carrera cuando varios clientes intentan registrarse al mismo tiempo.
     *
     * @param usuario  Referencia remota (callback) del cliente
     * @param nickname Nombre único del usuario
     * @return true si fue registrado exitosamente, false si el nickName ya existe
     */
    @Override
    public synchronized boolean registrarReferenciaUsuario(UsuarioCllbckInt usuario, String nickname) throws RemoteException
    {
        System.out.println("Solicitud de registro recibida para el usuario: " + nickname);

        // PUNTO b: Se verifica que el nickName no esté ya registrado en el mapa
        if (usuarios.containsKey(nickname))
        {
            // se verifica que el usuario registrado con ese nickName siga conectado, si terminó abruptamente se elimina su referencia del mapa y se permite el registro del nuevo usuario con ese nickName
            UsuarioCllbckInt usuarioRegistrado = usuarios.get(nickname);
            try
            {
                // Se intenta hacer el callback al usuario registrado para verificar su conexión
                usuarioRegistrado.notificar("Verificando conexión...", usuarios.size(), "Servidor");
            }
            catch (RemoteException e)
            {
                // El usuario registrado con ese nickName terminó abruptamente, se elimina su referencia del mapa y se permite el registro del nuevo usuario con ese nickName
                System.out.println("El usuario '" + nickname + "' registrado previamente terminó abruptamente. Eliminando referencia remota.");
                usuarios.remove(nickname); // Elimina la referencia del usuario que terminó abruptamente
                // Se continúa con el registro del nuevo usuario con ese nickName

                usuarios.put(nickname, usuario);
                System.out.println("Usuario registrado exitosamente: " + nickname);
                System.out.println("Total de usuarios activos: " + usuarios.size());

                // Se notifica a todos los usuarios que alguien nuevo se unió
                notificarUsuarios("El usuario '" + nickname + "' se ha unido al chat.", "Servidor");

                return true;
            }
            // El nickName ya existe, se informa y se retorna false
            System.out.println("El nickName '" + nickname + "' ya está en uso. Registro rechazado.");
            return false;
        }

        // PUNTO a: Se agrega la referencia remota del cliente al mapa con su nickName
        usuarios.put(nickname, usuario);
        System.out.println("Usuario registrado exitosamente: " + nickname);
        System.out.println("Total de usuarios activos: " + usuarios.size());

        // Se notifica a todos los usuarios que alguien nuevo se unió
        notificarUsuarios("El usuario '" + nickname + "' se ha unido al chat.", "Servidor");

        return true;
    }

    /**
     * Envía un mensaje público a todos los usuarios conectados.
     * PUNTO g: Antes de enviar a cada usuario, se verifica si sigue conectado.
     * Si un usuario terminó abruptamente, se elimina su referencia del mapa.
     *
     * @param mensaje   Contenido del mensaje
     * @param remitente NickName del usuario que envía el mensaje
     */
    @Override
    public void enviarMensaje(String mensaje, String remitente) throws RemoteException
    {
        System.out.println("Mensaje público de '" + remitente + "': " + mensaje);
        // Llama al método privado que recorre todos los usuarios y aplica el punto g
        notificarUsuarios(mensaje, remitente);
    }

    /**
     * Método privado que recorre el mapa de usuarios y les envía el mensaje.
     * PUNTO g: Por cada usuario, intenta hacer el callback. Si falla (RemoteException),
     * significa que el cliente terminó abruptamente y se elimina su referencia.
     *
     * @param mensaje   Contenido del mensaje a notificar
     * @param remitente NickName del remitente del mensaje
     */
    private void notificarUsuarios(String mensaje, String remitente)
    {
        System.out.println("Notificando a todos los usuarios conectados...");

        // Se usa Iterator para poder eliminar elementos del mapa mientras se recorre
        // (no se puede usar for-each y modificar el mapa al mismo tiempo)
        Iterator<Map.Entry<String, UsuarioCllbckInt>> iterador = usuarios.entrySet().iterator();

        while (iterador.hasNext())
        {
            Map.Entry<String, UsuarioCllbckInt> entrada = iterador.next();
            String nickUsuario = entrada.getKey();
            UsuarioCllbckInt objUsuario = entrada.getValue();

            try
            {
                // PUNTO g: Se intenta hacer el callback al usuario
                // Si el cliente terminó abruptamente, lanzará RemoteException
                objUsuario.notificar(mensaje, usuarios.size(), remitente);
            }
            catch (RemoteException e)
            {
                // PUNTO g: El cliente terminó abruptamente (cerró sin avisar)
                // Se elimina su referencia remota del mapa para limpiar el servidor
                System.out.println("El usuario '" + nickUsuario + "' terminó abruptamente. Eliminando referencia remota.");
                iterador.remove(); // Elimina el usuario del mapa de forma segura
                // se notifica a los demás usuarios que alguien se desconectó (opcional, pero útil para mantener la lista actualizada)
                notificarUsuarios("El usuario '" + nickUsuario + "' ha salido del chat.", "Servidor");
            }
        }
    }

    /**
     * PUNTO c: Retorna la lista de nickNames de los usuarios registrados y activos.
     *
     * @return Lista con los nickNames de todos los usuarios activos
     */
    @Override
    public List<String> mostrarUsuarios() throws RemoteException
    {
        // se verifica que cada usuario siga conectado antes de retornar la lista, si alguno terminó abruptamente se elimina su referencia del mapa
        Iterator<Map.Entry<String, UsuarioCllbckInt>> iterador = usuarios.entrySet().iterator();
        while (iterador.hasNext())
        {
            Map.Entry<String, UsuarioCllbckInt> entrada = iterador.next();
            String nickUsuario = entrada.getKey();
            UsuarioCllbckInt objUsuario = entrada.getValue();

            try
            {
                // Se intenta hacer el callback al usuario para verificar su conexión
                objUsuario.notificar("Verificando conexión...", usuarios.size(), "Servidor");
            }
            catch (RemoteException e)
            {
                // El usuario terminó abruptamente, se elimina su referencia del mapa
                System.out.println("El usuario '" + nickUsuario + "' terminó abruptamente durante la verificación. Eliminando referencia remota.");
                iterador.remove(); // Elimina el usuario del mapa de forma segura
                // se notifica a los demás usuarios que alguien se desconectó (opcional, pero útil para mantener la lista actualizada)
                notificarUsuarios("El usuario '" + nickUsuario + "' ha salido del chat.", "Servidor");
            }
        }
        // Se crea una nueva lista con los nickNames del mapa
        List<String> listaUsuarios = new ArrayList<>(usuarios.keySet());
        System.out.println("Listando " + listaUsuarios.size() + " usuario(s) activo(s).");
        return listaUsuarios;
    }

    /**
     * PUNTO d: Permite al cliente salir del chat y eliminar su referencia en el servidor.
     * También notifica a los demás usuarios que alguien se desconectó.
     *
     * @param nickname NickName del usuario que desea salir
     */
    @Override
    public void cerrarConexion(String nickname) throws RemoteException
    {
        // Se verifica que el usuario esté registrado antes de eliminar
        if (usuarios.containsKey(nickname))
        {
            // PUNTO d: Se elimina la referencia remota del usuario del mapa
            usuarios.remove(nickname);
            System.out.println("Usuario '" + nickname + "' se ha desconectado correctamente.");

            // Se notifica a los demás usuarios que alguien salió del chat
            notificarUsuarios("El usuario '" + nickname + "' ha salido del chat.", "Servidor");
        }
        else
        {
            // El usuario no estaba registrado (caso improbable pero se maneja)
            System.out.println("Se intentó desconectar un usuario no registrado: " + nickname);
        }
    }

    /**
     * PUNTO e y f: Envía un mensaje privado a un usuario determinado.
     * PUNTO f: Antes de reenviar, comprueba si el destinatario sigue conectado.
     * Si terminó abruptamente, se elimina su referencia y se notifica al emisor.
     *
     * @param mensaje      Contenido del mensaje privado
     * @param remitente    NickName del usuario que envía el mensaje
     * @param destinatario NickName del usuario que debe recibir el mensaje
     */
    @Override
    public void enviarMensajeDirecto(String mensaje, String remitente, String destinatario) throws RemoteException
    {
        System.out.println("Mensaje directo de '" + remitente + "' para '" + destinatario + "': " + mensaje);

        // Se verifica que el destinatario esté en la lista de usuarios activos
        if (usuarios.containsKey(destinatario))
        {
            // Obtiene la referencia remota del destinatario
            UsuarioCllbckInt usuarioDestinatario = usuarios.get(destinatario);

            try
            {
                // PUNTO f: Se intenta hacer el callback al destinatario
                // Si el cliente terminó abruptamente, lanzará RemoteException
                usuarioDestinatario.notificar("[Privado] " + mensaje, usuarios.size(), remitente);
                System.out.println("Mensaje directo enviado exitosamente de '" + remitente + "' a '" + destinatario + "'.");
            }
            catch (RemoteException e)
            {
                // PUNTO f: El destinatario terminó abruptamente (cerró sin avisar)
                // Se elimina su referencia remota del mapa
                System.out.println("El usuario '" + destinatario + "' terminó abruptamente. Eliminando referencia remota.");
                usuarios.remove(destinatario);

                // PUNTO f: Se notifica al emisor que el mensaje no pudo ser enviado
                UsuarioCllbckInt usuarioRemitente = usuarios.get(remitente);
                if (usuarioRemitente != null)
                {
                    try
                    {
                        // Se envía el mensaje de error exacto requerido por el taller
                        usuarioRemitente.notificar(
                            "El mensaje no se logró enviar porque el usuario receptor no está conectado",
                            usuarios.size(),
                            "Servidor"
                        );
                    }
                    catch (RemoteException ex)
                    {
                        // El remitente también terminó abruptamente
                        System.out.println("El remitente '" + remitente + "' también terminó abruptamente. Eliminando referencia.");
                        usuarios.remove(remitente);
                    }
                }
            }
        }
        else
        {
            // El destinatario no está en la lista de usuarios activos
            System.out.println("El destinatario '" + destinatario + "' no está conectado.");

            // Se notifica al emisor que el destinatario no existe o no está conectado
            UsuarioCllbckInt usuarioRemitente = usuarios.get(remitente);
            if (usuarioRemitente != null)
            {
                try
                {
                    usuarioRemitente.notificar(
                        "El mensaje no se logró enviar porque el usuario receptor no está conectado",
                        usuarios.size(),
                        "Servidor"
                    );
                }
                catch (RemoteException ex)
                {
                    System.out.println("El remitente '" + remitente + "' terminó abruptamente. Eliminando referencia.");
                    usuarios.remove(remitente);
                }
            }
        }
    }

    /**
     * PUNTO h: Retorna la cantidad de usuarios activos en el servidor.
     *
     * @return Número de usuarios activos (tamaño del mapa de usuarios)
     */
    @Override
    public int obtenerCantidadUsuariosActivos() throws RemoteException
    {
        //se verifica la conexion para cada uno de los usuarios activos, si alguno terminó abruptamente se elimina su referencia del mapa
        Iterator<Map.Entry<String, UsuarioCllbckInt>> iterador = usuarios.entrySet().iterator();
        while (iterador.hasNext())
        {
            Map.Entry<String, UsuarioCllbckInt> entrada = iterador.next();
            String nickUsuario = entrada.getKey();
            UsuarioCllbckInt objUsuario = entrada.getValue();

            try
            {
                // Se intenta hacer el callback al usuario para verificar su conexión
                objUsuario.notificar("Verificando conexión...", usuarios.size(), "Servidor");
            }
            catch (RemoteException e)
            {
                // El usuario terminó abruptamente, se elimina su referencia del mapa
                System.out.println("El usuario '" + nickUsuario + "' terminó abruptamente durante la verificación. Eliminando referencia remota.");
                iterador.remove(); // Elimina el usuario del mapa de forma segura
                // se notifica a los demás usuarios que alguien se desconectó (opcional, pero útil para mantener la lista actualizada)
                notificarUsuarios("El usuario '" + nickUsuario + "' ha salido del chat.", "Servidor");
            }
        }
        int cantidad = usuarios.size();
        System.out.println("Consulta de usuarios activos: " + cantidad);
        return cantidad;
    }
}
