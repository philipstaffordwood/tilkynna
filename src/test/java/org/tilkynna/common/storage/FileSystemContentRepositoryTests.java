/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.common.storage;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.tilkynna.report.templates.TemplateStorageProperties;

//test naming convention used is: givenUnitOfWork_whenInitialCondition_thenExpectedResult
//TODO loadAsResource needs to be tested too
@RunWith(SpringRunner.class)
public class FileSystemContentRepositoryTests {

    private TemplateStorageProperties properties = new TemplateStorageProperties();
    private FileSystemContentRepository contentRepository;

    @Before
    public void init() {
        properties.setLocation("target/files/" + Math.abs(new Random().nextLong()));
        contentRepository = new FileSystemContentRepository(properties);
        contentRepository.init();
    }

    @Test
    public void loadNonExistent() {
        UUID documentId = UUID.randomUUID();

        assertThat(contentRepository.load("example_designfile.rptdesign")).doesNotExist();
        assertThat(contentRepository.load(documentId + "/example_designfile.rptdesign")).doesNotExist();
    }

    @Test
    public void saveAndLoad() {
        UUID documentId = UUID.randomUUID();

        contentRepository.store(new MockMultipartFile("foo", "foo.txt", MediaType.TEXT_XML_VALUE, "Hello World".getBytes()), documentId);
        assertThat(contentRepository.load(documentId.toString())).exists();
    }

    @Test(expected = ContentStorageException.class)
    public void saveNotPermitted() {
        UUID documentId = UUID.randomUUID();

        contentRepository.store(new MockMultipartFile("foo", "../foo.txt", MediaType.TEXT_XML_VALUE, "Hello World".getBytes()), documentId);
    }

    @Test
    public void savePermitted() {
        UUID documentId = UUID.randomUUID();
        contentRepository.store(new MockMultipartFile("foo", "bar/../foo.txt", MediaType.TEXT_XML_VALUE, "Hello World".getBytes()), documentId);
    }
}
