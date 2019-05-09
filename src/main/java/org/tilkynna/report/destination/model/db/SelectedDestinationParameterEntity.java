/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.model.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.tilkynna.report.generate.model.db.GeneratedReportEntity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "selected_destination_parameter")
public class SelectedDestinationParameterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "value", nullable = true)
    @Type(type = "org.hibernate.type.TextType")
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_parameter_id")
    private DestinationParameterEntity destinationParameter;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "correlation_id")
    private GeneratedReportEntity generatedReport;

    public SelectedDestinationParameterEntity() {

    }

    public SelectedDestinationParameterEntity(DestinationParameterEntity destinationParameter, GeneratedReportEntity generatedReport, String value) {
        this.destinationParameter = destinationParameter;
        this.generatedReport = generatedReport;
        this.value = value;
    }
}
