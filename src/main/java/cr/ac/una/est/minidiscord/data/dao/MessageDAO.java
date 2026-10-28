package cr.ac.una.est.minidiscord.data.dao;

import cr.ac.una.est.minidiscord.business.model.Message;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    private static final String ARCHIVO = "mensajes.dat";

    public void guardarTodos(List<Message> mensajes) {
        try (ObjectOutputStream salida = new ObjectOutputStream(new FileOutputStream(ARCHIVO))) {
            salida.writeObject(new ArrayList<>(mensajes));
        } catch (IOException e) {
            System.out.println("No se pudo guardar el historial local: " + e.getMessage());
        }
    }

    public List<Message> cargarTodos() {
        File archivo = new File(ARCHIVO);
        if (!archivo.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(archivo))) {
            return (List<Message>) entrada.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No se pudo leer el historial local: " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
