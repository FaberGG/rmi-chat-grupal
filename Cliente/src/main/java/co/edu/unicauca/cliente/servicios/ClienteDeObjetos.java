package co.edu.unicauca.cliente.servicios;

import java.util.List;

import co.edu.unicauca.cliente.controladores.UsuarioCllbckImpl;
import co.edu.unicauca.cliente.utilidades.UtilidadesConsola;
import co.edu.unicauca.cliente.utilidades.UtilidadesProperties;
import co.edu.unicauca.cliente.utilidades.UtilidadesRegistroC;
import co.edu.unicauca.servidor.controladores.ControladorServidorChatInt;

/**
 * Clase principal del cliente de chat.
 * Implementa el menú de opciones para interactuar con el servidor de chat RMI.
 *
 * Puntos del taller implementados aquí:
 *   a) Registrar referencia remota con nickName
 *   b) Validar nickName único (respuesta del servidor)
 *   c) Ver nickNames de usuarios activos
 *   d) Salir del chat eliminando la referencia en el servidor
 *   e) Enviar mensaje directo a un usuario determinado
 *   h) Consultar cantidad de usuarios activos
 *   i) IP y puerto del NS leídos desde config.properties
 */
public class ClienteDeObjetos
{
    // Constantes para las opciones del menú del cliente
    private static final int OPCION_ENVIAR_MENSAJE          = 1;
    private static final int OPCION_MOSTRAR_USUARIOS        = 2;
    private static final int OPCION_ENVIAR_MENSAJE_DIRECTO  = 3;
    private static final int OPCION_CANTIDAD_USUARIOS       = 4; 
    private static final int OPCION_SALIR                   = 0;

    public static void main(String[] args)
    {
        try
        {
            // PUNTO i: Se obtienen la IP y el puerto del NS desde el archivo config.properties
            // en lugar de estar escritos directamente en el código
            String direccionIpRMIRegistry = UtilidadesProperties.obtenerPropiedad("ns.ip");
            int numPuertoRMIRegistry = Integer.parseInt(UtilidadesProperties.obtenerPropiedad("ns.puerto"));

            // Se muestran los datos de conexión al usuario
            System.out.println("=== Cliente de Chat RMI ===");
            System.out.println("Conectando con el servidor en " + direccionIpRMIRegistry + ":" + numPuertoRMIRegistry + "...");

            // Se obtiene la referencia remota del objeto servidor desde el rmiRegistry
            ControladorServidorChatInt servidor = (ControladorServidorChatInt)
                UtilidadesRegistroC.obtenerObjRemoto(numPuertoRMIRegistry, direccionIpRMIRegistry, "ServidorChat");

            // Se verifica que se haya podido obtener la referencia remota del servidor
            if (servidor == null)
            {
                System.out.println("No se pudo obtener la referencia remota del servidor.");
                System.out.println("Verifique que el servidor esté en ejecución en " + direccionIpRMIRegistry + ":" + numPuertoRMIRegistry);
                return;
            }

            System.out.println("Conexión exitosa al servidor.");

            // Se crea el objeto callback del cliente (referencia remota que el servidor usará)
            UsuarioCllbckImpl objNuevoUsuario = new UsuarioCllbckImpl();

            // PUNTO a y b: Se solicita el nickName y se registra en el servidor
            // El servidor valida que el nickName sea único (retorna false si ya existe)
            String nickname = UtilidadesConsola.leerCadena("Digite su nickName para registrarse en el chat: ");
            boolean registrado = servidor.registrarReferenciaUsuario(objNuevoUsuario, nickname);

            if (!registrado)
            {
                // PUNTO b: El servidor rechazó el registro porque el nickName ya está en uso
                System.out.println("No se pudo registrar: el nickName '" + nickname + "' ya está en uso.");
                System.out.println("Por favor, reinicie el cliente e intente con otro nickName.");
                return;
            }

            System.out.println("Sesión iniciada correctamente. ¡Bienvenido, " + nickname + "!");

            // Ciclo principal del menú del cliente
            while (true)
            {
                mostrarMenu();
                int opcion = UtilidadesConsola.leerEntero("Seleccione una opción: ");

                switch (opcion)
                {
                    case OPCION_ENVIAR_MENSAJE:
                        // Envía un mensaje público a todos los usuarios conectados
                        String mensaje = UtilidadesConsola.leerCadena("Digite el mensaje a enviar: ");
                        servidor.enviarMensaje(mensaje, nickname);
                        break;

                    case OPCION_MOSTRAR_USUARIOS:
                        // PUNTO c: Muestra la lista de nickNames de usuarios activos
                        List<String> usuariosActivos = servidor.mostrarUsuarios();
                        if (usuariosActivos == null || usuariosActivos.isEmpty())
                        {
                            System.out.println("No hay usuarios activos en este momento.");
                        }
                        else
                        {
                            System.out.println("Usuarios activos (" + usuariosActivos.size() + "):");
                            for (String usuario : usuariosActivos)
                            {
                                System.out.println("  - " + usuario);
                            }
                        }
                        break;

                    case OPCION_ENVIAR_MENSAJE_DIRECTO:
                        // PUNTO e: Envía un mensaje privado a un usuario determinado
                        String destinatario = UtilidadesConsola.leerCadena("Digite el nickName del destinatario: ");
                        String mensajeDireto = UtilidadesConsola.leerCadena("Digite el mensaje privado: ");
                        servidor.enviarMensajeDirecto(mensajeDireto, nickname, destinatario);
                        break;

                    case OPCION_CANTIDAD_USUARIOS:
                        // PUNTO h: Consulta la cantidad de usuarios activos
                        int cantidad = servidor.obtenerCantidadUsuariosActivos();
                        System.out.println("Cantidad de usuarios activos en el chat: " + cantidad);
                        break;

                    case OPCION_SALIR:
                        // PUNTO d: El cliente sale del chat y elimina su referencia en el servidor
                        servidor.cerrarConexion(nickname);
                        System.out.println("Sesión finalizada. ¡Hasta luego, " + nickname + "!");
                        return; // Termina el programa

                    default:
                        System.out.println("Opción inválida. Intente nuevamente.");
                        break;
                }
            }
        }
        catch (Exception e)
        {
            System.out.println("Error de conexión con el servidor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Muestra el menú de opciones disponibles para el cliente.
     */
    private static void mostrarMenu()
    {
        System.out.println("\n----- MENÚ CLIENTE CHAT -----");
        System.out.println("1. Enviar mensaje público");
        System.out.println("2. Ver usuarios activos");           // PUNTO c
        System.out.println("3. Enviar mensaje privado");         // PUNTO e
        System.out.println("4. Ver cantidad de usuarios activos"); // PUNTO h
        System.out.println("0. Salir del chat");                 // PUNTO d
        System.out.println("-----------------------------");
    }
}

