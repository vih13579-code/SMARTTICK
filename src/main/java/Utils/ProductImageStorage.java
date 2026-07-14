package Utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import javax.servlet.http.Part;

/** Safe image storage outside the deployed WAR. */
public final class ProductImageStorage {
    private static final long MAX_SIZE = 5L * 1024L * 1024L;
    private static final Set<String> ALLOWED_EXTENSIONS = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("jpg", "jpeg", "png", "webp")));
    private static final Set<String> ALLOWED_MIME = Collections.unmodifiableSet(
            new HashSet<>(Arrays.asList("image/jpeg", "image/png", "image/webp")));

    private ProductImageStorage() { }

    public static Path uploadDirectory() throws IOException {
        String configured = System.getenv("SMARTTICK_UPLOAD_PATH");
        if (configured == null || configured.trim().isEmpty()) {
            configured = System.getenv("FWATCH_UPLOAD_PATH");
        }
        if (configured == null || configured.trim().isEmpty()) {
            Properties properties = new Properties();
            try (InputStream input = ProductImageStorage.class.getClassLoader().getResourceAsStream("db.properties")) {
                if (input != null) {
                    properties.load(input);
                }
            }
            configured = properties.getProperty("upload.product.path", "uploads/products");
        }
        Path path = Paths.get(configured);
        if (!path.isAbsolute()) {
            path = Paths.get(System.getProperty("user.home"), "SMARTTICK", configured);
        }
        path = path.normalize().toAbsolutePath();
        Files.createDirectories(path);
        return path;
    }

    public static String save(Part part, boolean required) throws IOException {
        if (part == null || part.getSize() == 0) {
            if (required) {
                throw new IOException("The main product image is required.");
            }
            return null;
        }
        if (part.getSize() > MAX_SIZE) {
            throw new IOException("Each image must not exceed 5 MB.");
        }

        String extension = extensionOf(part.getSubmittedFileName());
        String declaredMime = part.getContentType() == null
                ? "" : part.getContentType().toLowerCase(Locale.ROOT);
        if (!ALLOWED_EXTENSIONS.contains(extension) || !ALLOWED_MIME.contains(declaredMime)) {
            throw new IOException("Only JPG, JPEG, PNG, or WEBP images are accepted.");
        }

        Path temporary = Files.createTempFile("smarttick-image-", ".upload");
        try {
            try (InputStream input = part.getInputStream()) {
                Files.copy(input, temporary, StandardCopyOption.REPLACE_EXISTING);
            }
            if (!hasValidSignature(temporary, extension)) {
                throw new IOException("The file content does not match the declared image format.");
            }

            String fileName = UUID.randomUUID().toString().replace("-", "") + "." + extension;
            Path root = uploadDirectory();
            Path target = root.resolve(fileName).normalize();
            if (!target.startsWith(root) || !target.getParent().equals(root)) {
                throw new IOException("The upload path is invalid.");
            }
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } finally {
            Files.deleteIfExists(temporary);
        }
    }

    private static boolean hasValidSignature(Path file, String extension) throws IOException {
        byte[] header = new byte[12];
        int read;
        try (InputStream input = Files.newInputStream(file)) {
            read = input.read(header);
        }
        if (read < 4) {
            return false;
        }
        if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            return (header[0] & 0xFF) == 0xFF
                    && (header[1] & 0xFF) == 0xD8
                    && (header[2] & 0xFF) == 0xFF;
        }
        if ("png".equals(extension)) {
            return read >= 8
                    && (header[0] & 0xFF) == 0x89
                    && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47
                    && header[4] == 0x0D && header[5] == 0x0A
                    && header[6] == 0x1A && header[7] == 0x0A;
        }
        if ("webp".equals(extension)) {
            return read >= 12
                    && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
                    && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P';
        }
        return false;
    }

    public static Path resolve(String fileName) throws IOException {
        if (fileName == null || !fileName.matches("[A-Za-z0-9._-]+")) {
            return null;
        }
        Path root = uploadDirectory();
        Path resolved = root.resolve(fileName).normalize();
        return resolved.startsWith(root) ? resolved : null;
    }

    public static void deleteQuietly(String fileName) {
        try {
            Path file = resolve(fileName);
            if (file != null) {
                Files.deleteIfExists(file);
            }
        } catch (IOException ignored) {
            // Failure to remove an old image must not break the product update transaction.
        }
    }

    private static String extensionOf(String name) {
        if (name == null) {
            return "";
        }
        String safeName = Paths.get(name).getFileName().toString();
        int dot = safeName.lastIndexOf('.');
        return dot < 0 ? "" : safeName.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
