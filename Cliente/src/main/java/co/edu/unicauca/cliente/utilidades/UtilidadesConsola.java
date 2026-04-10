package co.edu.unicauca.cliente.utilidades;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class UtilidadesConsola
{
	//refactorizar el metodo leerEntero para que reciba un mensaje personalizado a mostrar al usuario
	public static int leerEntero(String mensaje)
    {
    	String linea = "";
    	int opcion = 0;
    	boolean valido = false;
    	do
    	{
    		try
    		{
                System.out.println(mensaje);
                BufferedReader br = new BufferedReader(new
                InputStreamReader(System.in));
                linea = br.readLine();
                opcion = Integer.parseInt(linea);
                valido = true;
    		}
    		catch(Exception e)
    		{
    			System.out.println("Error intente nuevamente...");
    			valido = false;
    		}
    	}while(!valido);
    	
    	return opcion;
    
    }
	//refactorizar el metodo leerCadena para que reciba un mensaje personalizado a mostrar al usuario
	public static String leerCadena(String mensaje)
    {
    	String linea = "";
    	boolean valido = false;
    	do
    	{
    		try
    		{
				if (mensaje != null && !mensaje.isEmpty()) {
					System.out.println(mensaje);
				}
                BufferedReader br = new BufferedReader(new
                InputStreamReader(System.in));
                linea = br.readLine();
                valido = true;
    		}
    		catch(Exception e)
    		{
    			System.out.println("Error intente nuevamente...");
    			valido = false;
    		}
    	}while(!valido);
    	
    	return linea;
    
    }

}
