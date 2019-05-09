/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource;

import java.util.List;
import java.util.UUID;

import org.openapitools.api.DatasourcesApi;
import org.openapitools.model.DataSource;
import org.openapitools.model.DataSourceConnection;
import org.openapitools.model.DataSourceCreate;
import org.openapitools.model.DataSourceHeader;
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
import org.tilkynna.report.datasource.model.dao.DataSourceEntitySpecBuilder;

@RestController
@PreAuthorize("hasRole('TILKYNNA_USER') or hasRole('TILKYNNA_ADMIN')")
public class DatasourcesApiController implements DatasourcesApi {

    private static final String DEFAULT_SORT_FIELD = "name";

    @Autowired
    private DatasourceService datasourceService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Override
    public ResponseEntity<DataSource> createDataSource(DataSourceCreate dataSourceCreate) {
        return new ResponseEntity<>(datasourceService.save(dataSourceCreate), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<List<DataSourceHeader>> getAllDataSources(Integer page, Integer size, String filterName, String filterStatus, List<String> orderBy) {
        Sort sort = ParseOrderByQueryParam.resolveArgument(orderBy, DEFAULT_SORT_FIELD);
        final PageRequest pr = PageRequest.of(page, size, sort);

        Page<DataSourceHeader> dataSourceHeaders = datasourceService.findAll(DataSourceEntitySpecBuilder.nameContains(filterName), pr);

        if (dataSourceHeaders.getContent().isEmpty()) {
            return new ResponseEntity<>(dataSourceHeaders.getContent(), HttpStatus.NO_CONTENT);
        } else {
            HttpHeaders headers = new HttpHeaders();
            eventPublisher.publishEvent(new PaginatedResultsRetrievedEvent<>(this, dataSourceHeaders, headers));

            return new ResponseEntity<>(dataSourceHeaders.getContent(), headers, HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<DataSource> getDataSource(UUID datasourceId) {
        return new ResponseEntity<>(datasourceService.getDataSource(datasourceId), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> validateDataSource(UUID datasourceId) {
        if (datasourceService.validateConnection(datasourceId)) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Void> validateDataSourceNoId(DataSourceConnection dataSourceConnection) {
        if (datasourceService.validateConnection(dataSourceConnection)) {
            return new ResponseEntity<>(null, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<Void> inactivateDataSource(UUID datasourceId) {
        datasourceService.inactivateDataSource(datasourceId);

        return new ResponseEntity<>(null, HttpStatus.OK);

    }
}
