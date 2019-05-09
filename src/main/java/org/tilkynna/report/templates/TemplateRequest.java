/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class TemplateRequest {

    private MultipartFile file;

    private String templateName;

    private List<UUID> datasourceIds;

    private List<String> tags;

    public TemplateRequest() {
        super();
    }

    public TemplateRequest(MultipartFile file, String templateName, List<UUID> datasourceIds, List<String> tags) {
        super();
        this.file = file;
        this.templateName = templateName;
        this.datasourceIds = datasourceIds;
        this.tags = tags;
    }
}
