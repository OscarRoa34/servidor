package co.edu.uptc;

import java.io.*;
import java.net.*;

public class Servidor {
    public static void main(String[] args) {
        int puerto = 1234;

        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            System.out.println("Servidor escuchando en el puerto " + puerto);

            // Espera la conexión del cliente
            Socket socketCliente = serverSocket.accept();
            System.out.println("Cliente conectado desde " + socketCliente.getInetAddress());

            // Hilo para recibir mensajes del cliente
            Thread recibirMensajes = new Thread(() -> {
                try (BufferedReader entrada = new BufferedReader(
                        new InputStreamReader(socketCliente.getInputStream()))) {
                    String mensaje;
                    while ((mensaje = entrada.readLine()) != null) {
                        System.out.println("Cliente: " + mensaje);
                    }
                } catch (IOException e) {
                    System.out.println("Conexión cerrada por el cliente.");
                }
            });

            // Hilo para enviar mensajes al cliente
            Thread enviarMensajes = new Thread(() -> {
                try (PrintWriter salida = new PrintWriter(socketCliente.getOutputStream(), true);
                        BufferedReader consola = new BufferedReader(new InputStreamReader(System.in))) {
                    String mensaje;
                    while (true) {
                        mensaje = consola.readLine(); // Lee mensaje desde la consola del servidor
                        salida.println(mensaje); // Envía mensaje al cliente
                    }
                } catch (IOException e) {
                    System.out.println("Error al enviar mensaje.");
                }
            });

            // Iniciar los hilos
            recibirMensajes.start();
            enviarMensajes.start();

            // Espera a que terminen los hilos
            recibirMensajes.join();
            enviarMensajes.join();
            socketCliente.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
