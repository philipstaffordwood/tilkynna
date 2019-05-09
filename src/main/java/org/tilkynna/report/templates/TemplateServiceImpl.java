/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.openapitools.model.LookupTag;
import org.openapitools.model.Template;
import org.openapitools.model.TemplateDetail;
import org.openapitools.model.TemplateTagList;
import org.openapitools.model.Templates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.tilkynna.ReportingConstants;
import org.tilkynna.common.error.AlreadyExistsExceptions;
import org.tilkynna.common.error.CustomValidationExceptions;
import org.tilkynna.common.error.ResourceNotFoundExceptions;
import org.tilkynna.common.error.TemplateEngineExceptions;
import org.tilkynna.common.storage.ContentRepository;
import org.tilkynna.engine.TemplateEngine;
import org.tilkynna.engine.TemplateEngineFactory;
import org.tilkynna.engine.model.TemplateEngineParameter;
import org.tilkynna.lookup.tag.LookupTagRepository;
import org.tilkynna.report.datasource.model.dao.DatasourceEntityRepository;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;
import org.tilkynna.report.templates.assembler.TemplateAssembler;
import org.tilkynna.report.templates.assembler.TemplateParametersAssembler;

import lombok.extern.slf4j.Slf4j;

//TODO rollback .. if 1 the below things fail?

// TODO -- validation needed: valid file extension
// TODO -- validation needed: can I validate that file is XML (or BIRT xml even)?
@Service
@Slf4j
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private ContentRepository contentRepository;

    @Autowired
    private TemplateEntityRepository templateRepository;

    @Autowired
    private LookupTagRepository lookupTagRepository;

    @Autowired
    private DatasourceEntityRepository datasourceEntityRepository;

    @Autowired
    private TemplateAssembler templateAssembler;

    @Autowired
    private TemplateEngineFactory templateEngineFactory;

    @Autowired
    private TemplateParametersAssembler templateParamsAssembler;

    private final Path rootLocation;

    @Autowired
    public TemplateServiceImpl(TemplateStorageProperties properties) {
        this.rootLocation = Paths.get(properties.getLocation());
    }

    @Override
    public Template save(TemplateRequest templateRequest) {
        validateTemplateRequest(templateRequest);

        TemplateEntity template = saveTemplateToDB(templateRequest);
        saveTemplateToStorage(templateRequest.getFile(), template.getId());

        return templateAssembler.mapTemplateEntityToTemplate(template);
    }

    private void validateTemplateRequest(TemplateRequest templateRequest) {
        if (templateRequest == null || templateRequest.getTemplateName() == null) {
            throw new TemplateNameNotEmptyException();
        }

        String fileExtension = ReportingConstants.extractFileExtension(templateRequest.getFile().getOriginalFilename());
        if (!ReportingConstants.VALID_FILE_EXTENSIONS.contains(fileExtension)) {
            throw new CustomValidationExceptions.TemplateFileExtensionNotAllowedException(fileExtension);
        }

        if (onlyAllowOneDatasourcePerTemplate(templateRequest)) {
            throw new CustomValidationExceptions.OneDatasourcePerTemplate();
        }

        if (templateRepository.existsByNameIgnoreCase(templateRequest.getTemplateName())) {
            throw new AlreadyExistsExceptions.Template(templateRequest.getTemplateName());
        }

        List<UUID> datasources = templateRequest.getDatasourceIds();
        if (datasources == null || datasources.isEmpty()) {
            throw new TemplateEngineExceptions.TempalteHasNoDatasourcesException(templateRequest.getTemplateName());
        }

        UUID datasourceId = datasources.get(0);
        if (!datasourceEntityRepository.existsById(datasourceId)) {
            throw new ResourceNotFoundExceptions.Datasource(datasourceId.toString());
        }

    }

    /**
     * Currently we only allowing for 1 datasrouce per template. As the functionality for: </br>
     * - validating all datasources are active </br>
     * - getting BIRT engine to use all the datasources </br>
     * is going to take more time. For first MVP only 1 datasource is needed for now.
     * 
     * @param templateRequest
     * @return true/false datasourceId's has 1 and only 1 element
     */
    private boolean onlyAllowOneDatasourcePerTemplate(TemplateRequest templateRequest) {
        // templateRequest.getDatasourceIds() will never be empty as its validated for before entering into service class
        return templateRequest.getDatasourceIds().size() > 1;
    }

    private void saveTemplateToStorage(MultipartFile templateFile, UUID uuID) {
        contentRepository.store(templateFile, uuID);
    }

    // https://www.postgresql.org/docs/current/ddl-inherit.html#DDL-INHERIT-CAVEATS
    private TemplateEntity saveTemplateToDB(TemplateRequest templateRequest) {
        Set<DatasourceEntity> datasourceEntities = gatherDatasourceEntities(templateRequest.getDatasourceIds());

        UUID dsId = templateRequest.getDatasourceIds().get(0); // templateRequest.getDatasourceIds() can never be null at this point
        DatasourceEntity existingDatasource = datasourceEntityRepository.findById(dsId).orElseThrow(() -> new ResourceNotFoundExceptions.Datasource(dsId.toString()));

        TemplateEntity template = templateAssembler.mapTemplateRequestToTemplateEntity(templateRequest, datasourceEntities);
        template.addDatasource(existingDatasource);

        template = templateRepository.save(template);
        template.addTags(templateRequest.getTags());
        template = templateRepository.save(template);

        return template;
    }

    @Override
    public Templates findAll() {
        List<TemplateEntity> templatesDB = (List<TemplateEntity>) templateRepository.findAll();

        return templateAssembler.mapListTemplateEntityToTemplates(templatesDB);
    }

    @Override
    public TemplateTagList addTemplateTags(UUID templateId, List<LookupTag> lookupTags) {
        TemplateEntity templateDB = templateRepository.findById(templateId) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.Template(templateId.toString()));

        lookupTags.forEach(lookupTagElement -> { //
            templateDB.addTag(lookupTagElement.getTag());
        });
        templateRepository.save(templateDB);

        return templateAssembler.mapListStringToTemplateTagList(templateId, lookupTags);
    }

    @Override
    public void removeTempalteTags(UUID templateId) {
        templateRepository.findById(templateId) //
                .orElseThrow(() -> new ResourceNotFoundExceptions.Template(templateId.toString()));

        lookupTagRepository.deleteAllTemplateTags(templateId);
    }

    private DatasourceEntity getDatasourceEntity(UUID uuid) {
        return datasourceEntityRepository.findById(uuid).orElseThrow(() -> new ResourceNotFoundExceptions.Datasource(uuid.toString()));
    }

    @Override
    public Set<DatasourceEntity> gatherDatasourceEntities(List<UUID> datasourceIds) {
        Set<DatasourceEntity> datasources = new HashSet<>();
        datasourceIds.forEach(datasourceId -> datasources.add(getDatasourceEntity(datasourceId)));

        return datasources;
    }

    @Override
    public TemplateDetail getTemplateDetail(UUID templateId) {
        TemplateEntity templateHeaderDb = templateRepository.findById(templateId).orElseThrow(() -> new ResourceNotFoundExceptions.Template(templateId.toString()));

        TemplateDetail templateDetail = new TemplateDetail();
        templateDetail.setHeader(templateAssembler.mapTemplateEntityToTemplate(templateHeaderDb));
        templateDetail.setParameters(templateParamsAssembler.templateEngineParameterToTemplateParameter(getTemplateParameters(templateId, templateHeaderDb)));

        return templateDetail;
    }

    private List<TemplateEngineParameter> getTemplateParameters(UUID templateId, TemplateEntity templateHeaderDb) {
        TemplateEngine templateEngine = templateEngineFactory.getReportService(templateHeaderDb.getOriginalFilename());
        Path pathToStoredFile = this.rootLocation.resolve(templateId.toString());

        return templateEngine.getTemplateParameters(templateId.toString(), pathToStoredFile.toString());

    }

}
