package com.example.eshop.catalog.infrastructure;

import com.example.eshop.catalog.domain.file.FileNotFoundException;
import com.example.eshop.catalog.domain.file.FileStorage;
import com.example.eshop.catalog.domain.file.OpenInputStreamException;
import com.example.eshop.catalog.domain.file.SaveFileException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Component
@RequiredArgsConstructor
public class FileSystemFileStorage implements FileStorage {
    private final Properties properties;

    /**
     * Base Directory where Files will be stored
     */
    private String baseDirAbsolutePath;

    @PostConstruct
    private void init() {
        initBaseDir();
    }

    @SneakyThrows
    private void initBaseDir() {
        Path path;

        if (StringUtils.hasLength(properties.getBaseDir())) {
            path = Path.of(properties.getBaseDir());
            Files.createDirectories(path);
        } else {
            path = createDefaultBaseDir();
        }

        baseDirAbsolutePath = path.toAbsolutePath().normalize().toString();
    }

    private Path createDefaultBaseDir() throws IOException {
        return Files.createTempDirectory("eshop-files");
    }

    @Override
    public InputStream getInputStream(String location) {
        var path = getAbsolutePath(location);

        // check if path is existed and not a directory
        if (!Files.isRegularFile(path)) {
            throw new FileNotFoundException();
        }

        // TODO is there any way to do check and open file descriptor atomically ???

        try {
            return Files.newInputStream(path);
        } catch (Exception e) {
            throw new OpenInputStreamException(e);
        }
    }

    @Override
    public void save(InputStream is, String location) {
        var path = getAbsolutePath(location);

        if (!Files.isDirectory(path)) {
            throw new SaveFileException("Given location '" + location + "' is directory. Can't replace directory. " +
                    "Use filename instead");
        }

        try {
            Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            throw new SaveFileException(e);
        }
    }

    private Path getAbsolutePath(String location) {
        var path = Path.of(baseDirAbsolutePath + location).toAbsolutePath().normalize();

        // prevent Path Traversal attack
        if (!path.startsWith(baseDirAbsolutePath)) {
            throw new IllegalArgumentException("Given location must have no special names such as '..', './'");
        }

        return path;
    }

    @ConfigurationProperties(prefix = "file-storage.filesystem")
    @Getter
    @Setter
    public static class Properties {
        /**
         * Base directory where files will be stored by {@link FileSystemFileStorage}.
         * Can be in any form that can be normalized by {@link Path#normalize()}.
         */
        private String baseDir;
    }
}
