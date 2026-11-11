package mailapp;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/** Reads and writes the application's data file. */
public class FileManager {
    private final Path dataFile;
    public FileManager() { this("data/mail-data.ser"); }
    public FileManager(String fileName) { dataFile = Path.of(fileName); }

    public DataStore load() {
        if (!Files.exists(dataFile)) return new DataStore();
        try (ObjectInputStream input = new ObjectInputStream(Files.newInputStream(dataFile))) {
            Object object = input.readObject();
            if (object instanceof DataStore) return (DataStore) object;
        } catch (IOException | ClassNotFoundException ex) {
            System.err.println("Could not load saved data: " + ex.getMessage());
        }
        return new DataStore();
    }

    public void save(DataStore store) throws IOException {
        Path parent = dataFile.getParent();
        if (parent != null) Files.createDirectories(parent);
        try (ObjectOutputStream output = new ObjectOutputStream(Files.newOutputStream(dataFile))) {
            output.writeObject(store);
        }
    }
}
