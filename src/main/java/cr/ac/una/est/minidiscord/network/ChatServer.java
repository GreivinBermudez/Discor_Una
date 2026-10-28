package cr.ac.una.est.minidiscord.network;

import cr.ac.una.est.minidiscord.business.model.Message;
import cr.ac.una.est.minidiscord.business.service.ChatService;
import cr.ac.una.est.minidiscord.data.dao.JdbcMessageRepository;
import cr.ac.una.est.minidiscord.data.dao.MessageRepository;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class ChatServer {

    private final List<ClientHandler> clientes = new CopyOnWriteArrayList<>();
    private final ChatService servicio = new ChatService();
    private final MessageRepository repositorio = new JdbcMessageRepository();

    public void iniciar() throws IOException {
        try (ServerSocket servidor = new ServerSocket(ChatProtocol.PUERTO)) {
            System.out.println("Mini-Discord servidor iniciado en el puerto " + ChatProtocol.PUERTO);
            System.out.println("Cierre el servidor con Ctrl+C cuando termine la práctica.");

            while (true) {

                Socket socket = servidor.accept();
                System.out.println("Nueva conexión desde " + socket.getRemoteSocketAddress());

                ClientHandler manejador = new ClientHandler(socket, this, servicio);
                Thread hilo = new Thread(manejador, "cliente-minidiscord-" + socket.getPort());
                hilo.start();
            }
        }
    }

    synchronized void registrar(ClientHandler cliente) {
        List<Message> historial = repositorio.obtenerTodos();
        cliente.enviar("HISTORY_BEGIN");
        for (Message mensaje : historial) {
            cliente.enviar(ChatProtocol.codificar(mensaje));
        }
        clientes.add(cliente);
        cliente.enviar("READY");
        System.out.println(cliente.getUsuario().getUsername() + " está conectado. Clientes: " + clientes.size());
    }

    synchronized void publicar(ClientHandler remitente, Message mensaje) {

        repositorio.guardar(mensaje);
        String linea = ChatProtocol.codificar(mensaje);

        for (ClientHandler cliente : clientes) {
            if (!cliente.enviar(linea)) {

                clientes.remove(cliente);
            }
        }
        System.out.println("Mensaje guardado de " + remitente.getUsuario().getUsername());
    }

    void quitar(ClientHandler cliente) {
        clientes.remove(cliente);
        if (cliente.getUsuario() != null) {
            System.out.println(cliente.getUsuario().getUsername() + " salió. Clientes: " + clientes.size());
        }
    }

    public static void main(String[] args) {
        try {
            new ChatServer().iniciar();
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor. ¿El puerto 5050 está ocupado? " + e.getMessage());
        }
    }
}