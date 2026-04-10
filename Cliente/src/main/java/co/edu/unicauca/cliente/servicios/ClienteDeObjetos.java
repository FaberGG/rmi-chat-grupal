package co.edu.unicauca.cliente.servicios;

import java.util.List;

import co.edu.unicauca.cliente.controladores.UsuarioCllbckImpl;
import co.edu.unicauca.cliente.utilidades.UtilidadesConsola;
import co.edu.unicauca.cliente.utilidades.UtilidadesRegistroC;
import co.edu.unicauca.servidor.controladores.ControladorServidorChatInt;

public class ClienteDeObjetos
{
    private static final int OPCION_ENVIAR_MENSAJE = 1;
    private static final int OPCION_MOSTRAR_USUARIOS = 2;
    private static final int OPCION_ENVIAR_MENSAJE_DIRECTO = 3;
    private static final int OPCION_SALIR = 0;

    public static void main(String[] args)
    {

        try
        {
            ControladorServidorChatInt servidor;
            int numPuertoRMIRegistry = 8080;
            String direccionIpRMIRegistry = "localhost";
            /* 
            System.out.println("Cual es el la dirección ip donde se encuentra  el rmiregistry ");
            direccionIpRMIRegistry = UtilidadesConsola.leerCadena();
            System.out.println("Cual es el número de puerto por el cual escucha el rmiregistry ");
            numPuertoRMIRegistry = UtilidadesConsola.leerEntero(); 
*/
            //mostrar detalles de conexion
            System.out.println("Intentando conectar con el servidor en la dirección IP: " + direccionIpRMIRegistry + " y puerto: " + numPuertoRMIRegistry);

            servidor = (ControladorServidorChatInt) UtilidadesRegistroC.obtenerObjRemoto(numPuertoRMIRegistry,direccionIpRMIRegistry, "ServidorChat");
            if(servidor == null)
            {
                System.out.println("No se pudo obtener la referencia remota del servidor. Verifique IP, puerto y que el servidor siga en ejecucion.");
                return;
            }
            //confirmar conexion
            System.out.println("Conexión exitosa al servidor en " + direccionIpRMIRegistry + ":" + numPuertoRMIRegistry);

            UsuarioCllbckImpl objNuevoUsuario= new UsuarioCllbckImpl();
            // se pide el nickname del cliente para registrarlo en el servidor
            String nickname = UtilidadesConsola.leerCadena("Digite su nickname para registrarse en el chat: ");
            boolean registrado = servidor.registrarReferenciaUsuario(objNuevoUsuario, nickname);
            if (!registrado)
            {
                System.out.println("No se pudo registrar el usuario. El nickname " + nickname + " ya está en uso. Por favor, intente con otro nickname.");
                return;
            }
            System.out.println("Sesion iniciada correctamente para: " + nickname);

            while (true)
            {
                mostrarMenu();
                int opcion = UtilidadesConsola.leerEntero("Seleccione una opción: ");

                switch (opcion)
                {
                    case OPCION_ENVIAR_MENSAJE:
                        String mensaje = UtilidadesConsola.leerCadena("Digite el mensaje a enviar al servidor: ");
                        servidor.enviarMensaje(mensaje, nickname);
                        break;

                    case OPCION_MOSTRAR_USUARIOS:
                        List<String> usuariosActivos = servidor.mostrarUsuarios();
                        if (usuariosActivos == null || usuariosActivos.isEmpty())
                        {
                            System.out.println("No hay usuarios activos en este momento.");
                        }
                        else
                        {
                            System.out.println("Usuarios activos:");
                            for (String usuario : usuariosActivos)
                            {
                                System.out.println("- " + usuario);
                            }
                        }
                        break;

                    case OPCION_ENVIAR_MENSAJE_DIRECTO:
                        String destinatario = UtilidadesConsola.leerCadena("Digite el nickname del destinatario: ");
                        String mensajedm = UtilidadesConsola.leerCadena("Digite el mensaje a enviar: ");
                        servidor.enviarMensajeDirecto(mensajedm, nickname, destinatario);
                        break;

                    case OPCION_SALIR:
                        servidor.cerrarConexion(nickname);
                        System.out.println("Sesion finalizada por el cliente.");
                        return;

                    default:
                        System.out.println("Opcion invalida. Intente nuevamente.");
                        break;
                }
            }

        }
        catch(Exception e)
        {
                System.out.println("No se pudo realizar la conexion...");
                System.out.println(e.getMessage());
        }

    }

    private static void mostrarMenu()
    {
        System.out.println("\n----- MENU CLIENTE CHAT -----");
        System.out.println("1. Enviar mensaje");
        System.out.println("2. Mostrar usuarios activos");
        System.out.println("3. Enviar mensaje directo");
        System.out.println("0. Salir");
    }
	
}
