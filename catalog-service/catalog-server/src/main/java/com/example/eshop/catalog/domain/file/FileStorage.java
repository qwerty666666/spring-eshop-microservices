package com.example.eshop.catalog.domain.file;

import java.io.InputStream;

public interface FileStorage {
    /**
     * Get File by given {@code location}.
     *
     * @throws FileNotFoundException if File with given {@code location} does not exist
     * @throws OpenInputStreamException if open InputStream failed for some reason
     */
    InputStream getInputStream(String location);

    /**
     * Saves InputStream to Storage. If File already exists, it will be overwritten.
     *
     * @throws SaveFileException if File was not saved for some reason
     */
    void save(InputStream is, String location);
}
