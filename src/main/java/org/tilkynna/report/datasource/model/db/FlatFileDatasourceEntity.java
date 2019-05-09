/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.datasource.model.db;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "flat_file")
public class FlatFileDatasourceEntity extends DatasourceEntity {

    public FlatFileDatasourceEntity() {
        this.setType(DataSourceTypes.Values.FLAT_FILE);
    }

    @Column(name = "file_uri", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String fileUri;

    @Column(name = "char_set", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String charSet;

    @Column(name = "flat_file_style", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String flatFileStyle;

    @Column(name = "first_line_header")
    private boolean firstLineHeader;

    @Column(name = "second_line_data_type_indicator")
    private boolean secondLineDataTypeIndicator;

    /**
     * This datasource does not have a password
     */
    @Override
    public byte[] getPassword() {
        throw new UnsupportedOperationException();
    }

    /**
     * This datasource does not have a password
     */
    @Override
    public void setPassword(byte[] password) {

        throw new UnsupportedOperationException();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(charSet, fileUri, firstLineHeader, flatFileStyle, secondLineDataTypeIndicator);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (!super.equals(obj)) { return false; }
        if (!(obj instanceof FlatFileDatasourceEntity)) { return false; }
        FlatFileDatasourceEntity other = (FlatFileDatasourceEntity) obj;
        return Objects.equals(charSet, other.charSet) && Objects.equals(fileUri, other.fileUri) && firstLineHeader == other.firstLineHeader && Objects.equals(flatFileStyle, other.flatFileStyle) && secondLineDataTypeIndicator == other.secondLineDataTypeIndicator;
    }

}
