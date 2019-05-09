/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource.model.dao;

import org.springframework.data.jpa.domain.Specification;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;

public class DataSourceEntitySpecBuilder {

    private DataSourceEntitySpecBuilder() {

    }

    public static Specification<DatasourceEntity> dataSourceSearchSpec(String name, Boolean isActive) {
        Specification<DatasourceEntity> nameSpec = DataSourceEntitySpecBuilder.nameContains(name);
        Specification<DatasourceEntity> activeSpec = DataSourceEntitySpecBuilder.isActive(isActive);

        return Specification.where(activeSpec).and(nameSpec);
    }

    public static Specification<DatasourceEntity> nameContains(String name) {
        return (datasource, cq, cb) -> cb.like(cb.lower(datasource.get("name")), getContainsLikePattern(name));
    }

    public static Specification<DatasourceEntity> isActive(Boolean isActive) {
        return (datasource, cq, cb) -> cb.equal(datasource.get("isActive"), isActive);
    }

    private static String getContainsLikePattern(String searchTerm) {
        if (searchTerm == null || searchTerm.isEmpty()) {
            return "%";
        } else {
            return "%" + searchTerm.toLowerCase() + "%";
        }
    }

}
