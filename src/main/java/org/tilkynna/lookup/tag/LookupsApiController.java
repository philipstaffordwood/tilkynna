/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.lookup.tag;

import java.sql.Driver;
import java.util.List;

import org.openapitools.api.LookupsApi;
import org.openapitools.model.LookupConnectionType;
import org.openapitools.model.LookupDestinationType;
import org.openapitools.model.LookupExportFormat;
import org.openapitools.model.LookupTag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;
import org.tilkynna.common.utils.PaginatedResultsRetrievedEvent;
import org.tilkynna.common.utils.ParseOrderByQueryParam;
import org.tilkynna.lookup.driver.DriversLocationProperties;
import org.tilkynna.lookup.driver.FindDriverClasses;
import org.tilkynna.report.datasource.DatasourceService;
import org.tilkynna.report.destination.DestinationService;
import org.tilkynna.report.generate.download.DownloadService;

@RestController
@PreAuthorize("hasRole('TILKYNNA_USER') or hasRole('TILKYNNA_ADMIN')")
public class LookupsApiController implements LookupsApi {

    private static final String DEFAULT_SORT_FIELD = "tag";

    @Autowired
    private LookupTagService lookupTagService;

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private DestinationService destinationService;

    @Autowired
    private DownloadService downloadService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private DriversLocationProperties driverLocationProperties;

    @Override
    public ResponseEntity<List<LookupTag>> getTags(Integer page, Integer size, String filterTag, List<String> orderBy) {
        Sort sort = ParseOrderByQueryParam.resolveArgument(orderBy, DEFAULT_SORT_FIELD);
        final PageRequest pr = PageRequest.of(page, size, sort);

        Page<LookupTag> tagsPage = null;
        if (null != filterTag) {
            tagsPage = lookupTagService.filterTag(filterTag, pr);
        } else {
            tagsPage = lookupTagService.findAllDistinctTags(pr);
        }

        if (tagsPage.hasContent()) {
            HttpHeaders headers = new HttpHeaders();
            eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<>(this, tagsPage, headers));

            return new ResponseEntity<>(tagsPage.getContent(), headers, HttpStatus.OK);
        }

        return new ResponseEntity<>(tagsPage.getContent(), HttpStatus.NO_CONTENT);
    }

    @Override
    public ResponseEntity<List<String>> getAllDrivers(Integer page, Integer size, List<String> orderBy) {

        List<String> drivers = FindDriverClasses.findClassesImplementing(Driver.class, driverLocationProperties.getLocation());

        return new ResponseEntity<>(drivers, HttpStatus.OK);

    }

    @Override
    public ResponseEntity<List<LookupConnectionType>> listSupportedConnectionTypes() {
        return new ResponseEntity<>(datasourceService.getConnectionTypes(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<LookupDestinationType>> listSupportedDestinationTypes() {
        return new ResponseEntity<>(destinationService.getDestinationTypes(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<LookupExportFormat>> listFileExportFormats() {
        return new ResponseEntity<>(downloadService.listFileExportFormats(), HttpStatus.OK);
    }

}
