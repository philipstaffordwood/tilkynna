/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource.model.dao;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit4.SpringRunner;
import org.tilkynna.report.datasource.mockdata.DatasouceMockDataGenerator;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;

@RunWith(SpringRunner.class)
@DataJpaTest
public class DatasourceEntitySearchTests {

    @Autowired
    private DatasourceEntityRepository datasourceEntityRepository;

    private DatasourceEntity liabilitiesDatasource;
    private DatasourceEntity assetsDatasource;

    private DatasourceEntity activeDatasource;
    private DatasourceEntity inActiveDatasource;

    @Before
    public void init() {
        liabilitiesDatasource = DatasouceMockDataGenerator.setupJDBCDatasourceEntity("liabilities");
        datasourceEntityRepository.save(liabilitiesDatasource);

        assetsDatasource = DatasouceMockDataGenerator.setupJDBCDatasourceEntity("assets");
        datasourceEntityRepository.save(assetsDatasource);

        activeDatasource = DatasouceMockDataGenerator.setupJDBCDatasourceEntity("activeDatasource");
        activeDatasource.setActive(Boolean.TRUE);
        datasourceEntityRepository.save(activeDatasource);

        inActiveDatasource = DatasouceMockDataGenerator.setupJDBCDatasourceEntity("inActiveDatasource");
        inActiveDatasource.setActive(Boolean.FALSE);
        datasourceEntityRepository.save(inActiveDatasource);
    }

    @Test
    public void givenName_whenGettingListOfDatasources_thenCorrect() {
        Specification<DatasourceEntity> spec = DataSourceEntitySpecBuilder.nameContains("liabilities");

        List<DatasourceEntity> results = datasourceEntityRepository.findAll(spec);

        assertThat("results should include liabilitiesDatasource:", results, hasItem(liabilitiesDatasource));
        assertThat("results should NOT include assetsDatasource:", results, not(hasItem(assetsDatasource)));
    }

    @Test
    public void givenPartOfName_whenGettingListOfDatasources_thenCorrect() {
        Specification<DatasourceEntity> spec = DataSourceEntitySpecBuilder.nameContains("abil");

        List<DatasourceEntity> results = datasourceEntityRepository.findAll(spec);

        assertThat("results should include liabilitiesDatasource:", results, hasItem(liabilitiesDatasource));
        assertThat("results should NOT include assetsDatasource:", results, not(hasItem(assetsDatasource)));
    }

    @Test
    public void givenTrueActiveFlag_whenGettingListOfDatasources_thenCorrect() {
        Specification<DatasourceEntity> spec = DataSourceEntitySpecBuilder.isActive(Boolean.TRUE);
        List<DatasourceEntity> results = datasourceEntityRepository.findAll(spec);

        assertThat("results should include activeDatasource:", results, hasItem(activeDatasource));
        assertThat("results should include assetsDatasource (active default is true):", results, hasItem(assetsDatasource));

        assertThat("results should NOT include inActiveDatasource:", results, not(hasItem(inActiveDatasource)));
    }

    @Test
    public void givenFalseActiveFlag_whenGettingListOfDatasources_thenCorrect() {
        Specification<DatasourceEntity> spec = DataSourceEntitySpecBuilder.isActive(Boolean.FALSE);
        List<DatasourceEntity> results = datasourceEntityRepository.findAll(spec);

        assertThat("results should include inActiveDatasource:", results, hasItem(inActiveDatasource));

        assertThat("results should NOT include activeDatasource:", results, not(hasItem(activeDatasource)));
        assertThat("results should NOT include assetsDatasource (active default is true):", results, not(hasItem(assetsDatasource)));
    }

    @Test
    public void givenFalseActiveFlagAndPartOfName_whenGettingListOfDatasources_thenCorrect() {
        Specification<DatasourceEntity> dataSourceSearchSpec = DataSourceEntitySpecBuilder.dataSourceSearchSpec("iabili", Boolean.FALSE);

        List<DatasourceEntity> results = datasourceEntityRepository.findAll(dataSourceSearchSpec);

        assertThat("results should NOT include inActiveDatasource (name != liabilities):", results, not(hasItem(inActiveDatasource)));
    }

    @Test
    public void givenFalseActiveFlagNullName_whenGettingListOfDatasources_thenCorrect() {
        Specification<DatasourceEntity> dataSourceSearchSpec = DataSourceEntitySpecBuilder.dataSourceSearchSpec(null, Boolean.FALSE);

        List<DatasourceEntity> results = datasourceEntityRepository.findAll(dataSourceSearchSpec);

        assertThat("results should include inActiveDatasource:", results, hasItem(inActiveDatasource));
    }

    @Test
    public void givenFalseActiveFlagEmptyName_whenGettingListOfDatasources_thenCorrect() {
        Specification<DatasourceEntity> dataSourceSearchSpec = DataSourceEntitySpecBuilder.dataSourceSearchSpec("", Boolean.FALSE);

        List<DatasourceEntity> results = datasourceEntityRepository.findAll(dataSourceSearchSpec);

        assertThat("results should include inActiveDatasource:", results, hasItem(inActiveDatasource));
    }
}
