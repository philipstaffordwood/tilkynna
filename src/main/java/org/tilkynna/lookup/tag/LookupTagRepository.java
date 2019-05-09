/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.lookup.tag;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.tilkynna.report.templates.model.db.TemplateTagEntity;
import org.tilkynna.report.templates.model.db.TemplateTagId;

// https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
// http://appsdeveloperblog.com/how-to-use-like-expression-in-jpa-sql-query/
@Repository
public interface LookupTagRepository extends PagingAndSortingRepository<TemplateTagEntity, TemplateTagId> {

    @Query(value = "SELECT DISTINCT tag FROM _reports.template_tag AS t WHERE t.tag ilike %:keyword%", nativeQuery = true)
    public Page<String> findDistinctTagsByKeyword(Pageable pageable, @Param("keyword") String keyword);

    @Query(value = "SELECT DISTINCT tag FROM _reports.template_tag", nativeQuery = true)
    public Page<String> findAllDistinctTags(Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM _reports.template_tag AS t WHERE t.template_id = ?1", nativeQuery = true)
    public void deleteAllTemplateTags(UUID tempalteId);
}
