/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.model.db;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;

//TODO can these be cached?

@Getter
@Setter
@Entity
@Table(name = "export_format")
public class ExportFormatEntity {

    @Id
    @Column(name = "id", columnDefinition = "SMALLINT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Short id;

    @Column(name = "name")
    @Type(type = "org.hibernate.type.TextType")
    private String name;

    @Column(name = "media_type")
    @Type(type = "org.hibernate.type.TextType")
    private String mediaType;

    @Override
    public int hashCode() {
        return Objects.hash(isActive, mediaType, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof ExportFormatEntity)) { return false; }
        ExportFormatEntity other = (ExportFormatEntity) obj;
        return Objects.equals(isActive, other.isActive) && Objects.equals(mediaType, other.mediaType) && Objects.equals(name, other.name);
    }

    @Column(name = "is_active")
    private Boolean isActive;

}
