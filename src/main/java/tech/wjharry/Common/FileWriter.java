package tech.wjharry.Common;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileWriter {
    public static void appendBinary(String context, String filePath) throws IOException {
        byte[] data = (context + "\n").getBytes(StandardCharsets.UTF_8);
        Path path = Paths.get(filePath);
        Files.write(path, data, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
    }
}
