
package com.app.beleza;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class UploadConfig {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public Path getUploadPath() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }
}