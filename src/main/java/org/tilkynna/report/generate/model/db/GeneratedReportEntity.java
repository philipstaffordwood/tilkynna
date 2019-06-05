/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.model.db;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.UpdateTimestamp;
import org.tilkynna.common.hibernate.PostgreSQLEnumType;
import org.tilkynna.report.destination.model.db.DestinationEntity;
import org.tilkynna.report.destination.model.db.SelectedDestinationParameterEntity;
import org.tilkynna.report.templates.TemplateEntity;

import lombok.Getter;
import lombok.Setter;

/**
 * Using suggestions from : https://thoughts-on-java.org/ultimate-guide-to-implementing-equals-and-hashcode-with-hibernate/ for hashCode()
 */
@Getter
@Setter
@Entity
@Table(name = "generated_report")
@TypeDef(name = "pgsql_enum", typeClass = PostgreSQLEnumType.class)
public class GeneratedReportEntity {

    @Id
    @Column(name = "correlation_id", updatable = false, unique = true, nullable = false, columnDefinition = "uuid default gen_random_uuid()")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID correlationId;

    @Column(name = "requested_by", updatable = false, nullable = false)
    private UUID requestedBy;

    @Column(name = "requested_at")
    @CreationTimestamp
    private ZonedDateTime requestedAt;

    @Column(name = "generated_at")
    @UpdateTimestamp
    private ZonedDateTime generatedAt;

    @Column(name = "retry_count", updatable = true)
    private Short retryCount;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private TemplateEntity template;

    @ManyToOne
    @JoinColumn(name = "destination_id")
    private DestinationEntity destination;

    @ManyToOne
    @JoinColumn(name = "export_format_id")
    private ExportFormatEntity exportFormat;

    @Column(name = "request_body", updatable = false, nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String requestBody;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_status", nullable = false)
    @Type(type = "pgsql_enum")
    private ReportStatusEntity reportStatus; //

    @Column(name = "processed_by")
    @Type(type = "org.hibernate.type.TextType")
    private String proccesedBy;

    @OneToMany(mappedBy = "generatedReport", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<SelectedDestinationParameterEntity> selectedDestinationParameters = new HashSet<>();

    public boolean isPending() {
        return reportStatus.equals(ReportStatusEntity.PENDING);
    }

    public boolean isFinished() {
        return reportStatus.equals(ReportStatusEntity.FINISHED);
    }

    @Override
    public int hashCode() {
        return Objects.hash(destination, exportFormat, generatedAt, reportStatus, requestBody, requestedAt, requestedBy, retryCount, selectedDestinationParameters, template, proccesedBy);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof GeneratedReportEntity)) {
            return false;
        }
        GeneratedReportEntity other = (GeneratedReportEntity) obj;
        return Objects.equals(destination, other.destination) && Objects.equals(exportFormat, other.exportFormat) && Objects.equals(generatedAt, other.generatedAt) && reportStatus == other.reportStatus
                && Objects.equals(requestBody, other.requestBody) && Objects.equals(requestedAt, other.requestedAt) && Objects.equals(requestedBy, other.requestedBy) && Objects.equals(retryCount, other.retryCount)
                && Objects.equals(selectedDestinationParameters, other.selectedDestinationParameters) && Objects.equals(template, other.template) && Objects.equals(proccesedBy, other.proccesedBy);
    }

}
