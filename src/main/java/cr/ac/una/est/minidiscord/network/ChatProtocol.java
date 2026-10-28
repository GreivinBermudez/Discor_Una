package cr.ac.una.est.minidiscord.network;

import cr.ac.una.est.minidiscord.business.model.Message;
import cr.ac.una.est.minidiscord.business.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

public final class ChatProtocol {

    public static final int PUERTO = 5050;

    private ChatProtocol() {
    }

    public static String saludo(String username) {
        return "HELLO|" + username;
    }

    public static String solicitudMensaje(String contenido) {
        return "SEND|" + contenido;
    }

    public static String codificar(Message mensaje) {
        return "MSG|" + mensaje.getAuthor().getUsername()
                + "|" + mensaje.getContent()
                + "|" + mensaje.getSentAt();
    }

    public static Message decodificar(String linea) {
        String[] partes = linea.split("\\|", 4);
        if (partes.length != 4 || !"MSG".equals(partes[0])) {
            throw new IllegalArgumentException("El servidor envió un mensaje inválido");
        }
        try {
            User autor = new User(partes[1]);
            LocalDateTime enviado = LocalDateTime.parse(partes[3]);
            return new Message(autor, partes[2], enviado);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Fecha de envío inválida en: " + linea, e);
        }
    }
}