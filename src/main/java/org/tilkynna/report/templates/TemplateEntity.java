/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.templates;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.NaturalId;
import org.tilkynna.report.datasource.model.db.DatasourceEntity;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;
import org.tilkynna.report.templates.model.db.TemplateTagEntity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * MetaData for a template
 * 
 * Using suggestions from : https://thoughts-on-java.org/ultimate-guide-to-implementing-equals-and-hashcode-with-hibernate/ for hashCode()
 */
@Getter
@Setter
@Entity
@Table(name = "template")
public class TemplateEntity {

    @Id
    @Column(name = "id", updatable = false, unique = true, nullable = false, columnDefinition = "uuid default gen_random_uuid()")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @NaturalId
    @Column(name = "name", unique = true, nullable = false, length = 255)
    private String name;

    @Column(name = "original_filename", nullable = false, length = 255)
    private String originalFilename;

    public TemplateEntity(String name) {
        this.name = name;
    }

    @SuppressWarnings("unused")
    private TemplateEntity() {
    }

    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "template", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TemplateTagEntity> templateTags = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable( //
            name = "template_datasource", //
            joinColumns = @JoinColumn(name = "template_id", referencedColumnName = "id"), //
            inverseJoinColumns = @JoinColumn(name = "datasource_id", referencedColumnName = "datasource_id"))
    private Set<DatasourceEntity> datasources = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "template", cascade = CascadeType.ALL)
    private Set<GeneratedReportEntity> generatedReports;

    /**
     * Associates tag, and it's value, to a template.
     * 
     * @param tag
     *            The tag to associate with the template.
     * @param value
     *            The value of the tag association.
     * @return An instance of the added template tag.
     */
    public TemplateTagEntity addTag(String value) {
        TemplateTagEntity templateTag = new TemplateTagEntity(this, value);
        templateTags.add(templateTag);

        return templateTag;
    }

    public void addTags(List<String> tags) {
        tags.forEach(tag -> { this.addTag(tag); });
    }

    public boolean hasInActiveDatasources() {
        return datasources.stream().anyMatch(ds -> !ds.isActive());
    }

    public void addDatasource(DatasourceEntity datasourceEntity) {
        if (!getDatasources().contains(datasourceEntity)) {
            getDatasources().add(datasourceEntity);
        }
    }

    public void addDatasources(Set<DatasourceEntity> datasources) {
        for (Iterator<DatasourceEntity> iterator = datasources.iterator(); iterator.hasNext();) {
            DatasourceEntity datasource = iterator.next();
            addDatasource(datasource);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof TemplateEntity)) { return false; }
        TemplateEntity other = (TemplateEntity) obj;
        return Objects.equals(datasources, other.datasources) && Objects.equals(generatedReports, other.generatedReports) && Objects.equals(name, other.name) && Objects.equals(originalFilename, other.originalFilename) && Objects.equals(templateTags, other.templateTags);
    }

}
