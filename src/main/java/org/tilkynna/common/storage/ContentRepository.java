/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.storage;

import java.nio.file.Path;
import java.util.UUID;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ContentRepository {

    void init();

    void store(MultipartFile file, UUID documentUUID);

    void store(byte[] reportFile, UUID correnationUUID);

    Path load(String filename);

    Resource loadAsResource(UUID documentUUID);

    void deleteAll();

}
