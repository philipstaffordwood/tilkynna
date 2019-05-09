/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.storage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.tilkynna.report.templates.TemplateStorageProperties;

/**
 * @Repository a mechanism for encapsulating storage, retrieval, and search behavior which emulates a collection of objects
 * 
 * @author melissap
 *
 */
@ConditionalOnProperty(value = "report.template.storage", havingValue = "FileSystemContentRepository", matchIfMissing = true)
@Repository
@Primary
public class FileSystemContentRepository implements ContentRepository {

    private final Path rootLocation;

    @Autowired
    public FileSystemContentRepository(TemplateStorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    public FileSystemContentRepository(String location) {
        this.rootLocation = Paths.get(location);
    }

    @Override
    public void store(MultipartFile file, UUID documentUUID) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        Path pathToStoredFile = this.rootLocation.resolve(documentUUID.toString());
        try {
            if (file.isEmpty()) {
                throw new ContentStorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new ContentStorageException("Cannot store file with relative path outside current directory " + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.createDirectories(pathToStoredFile.getParent());
                Files.copy(inputStream, pathToStoredFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException e) {
            throw new ContentStorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public void store(byte[] reportFile, UUID correlationId) {
        Path pathToStoredFile = this.rootLocation.resolve(correlationId.toString());
        try {
            if (reportFile.length == 0) {
                throw new ContentStorageException("Failed to store empty file " + correlationId);
            }

            ByteArrayInputStream bis = new ByteArrayInputStream(reportFile);
            Files.createDirectories(pathToStoredFile.getParent());
            Files.copy(bis, pathToStoredFile, StandardCopyOption.REPLACE_EXISTING);

        } catch (IOException e) {
            throw new ContentStorageException("Failed to store file " + correlationId, e);
        }
    }

    @Override
    public Path load(String filename) {
        return rootLocation.resolve(filename);
    }

    @Override
    public Resource loadAsResource(UUID documentUUID) {
        try {

            Path pathToStoredFile = this.rootLocation.resolve(documentUUID.toString());
            Resource resource = new UrlResource(pathToStoredFile.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new ContentStorageNotFoundException("Could not read file: " + documentUUID.toString());

            }
        } catch (MalformedURLException e) {
            throw new ContentStorageNotFoundException("Could not read file: " + documentUUID.toString(), e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(rootLocation.toFile());
    }

    @Override
    public void init() {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new ContentStorageException("Could not initialize storage", e);
        }
    }
}
