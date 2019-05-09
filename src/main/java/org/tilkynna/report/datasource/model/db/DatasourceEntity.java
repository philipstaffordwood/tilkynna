/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource.model.db;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.tilkynna.report.templates.TemplateEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author melissap
 *
 */

@Getter
@Setter
@Entity
@Table(name = "datasource")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DatasourceEntity {

    @Id
    @Column(name = "datasource_id", updatable = false, unique = true, nullable = false, columnDefinition = "uuid default gen_random_uuid()")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "name", unique = true, nullable = false, length = 255)
    private String name;

    @Column(name = "type", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String type;

    @Column(name = "description")
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = "is_active")
    private boolean isActive;

    @ManyToMany(mappedBy = "datasources")
    private Set<TemplateEntity> templates = new HashSet<>();

    public abstract void setPassword(byte[] password);

    public abstract byte[] getPassword();

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof DatasourceEntity))
            return false;
        DatasourceEntity other = (DatasourceEntity) obj;
        return Objects.equals(description, other.description) && isActive == other.isActive && Objects.equals(name, other.name) && Objects.equals(templates, other.templates) && Objects.equals(type, other.type);
    }

}
