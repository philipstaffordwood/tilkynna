/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.generate.model.db;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneratedReportEntityRepository extends JpaRepository<GeneratedReportEntity, UUID>, JpaSpecificationExecutor<GeneratedReportEntity> {

    /**
     * Finds awaiting GenerateReportEntity(report_request)'s that are in 'PENDING' status: <br/>
     * Ordered By Priority, RetryCount, Requested date. <br/>
     * Such that the highest priority requests are dealt with first.
     * 
     * downloadable 'are essentially streamed reports for UI' therefore higher priority <br/>
     * highest retryCount first so that those already failed, can be enqueued first <br/>
     * and oldest requested_at so that earliest requests are dealt with first
     * 
     * Limiting to 30 to align with m
     * 
     * @return String of highest priority correlationId for reportRequest to be enqueued for processing next
     */
    @Query(value = " WITH jobs AS ( " + //
            "       SELECT correlation_id " + //
            "         FROM _reports.generated_report r " + //
            "         JOIN _reports.destination d ON r.destination_id = d.destination_id " + //
            "       WHERE cast(report_status AS varchar) = 'PENDING'  " + //
            "       ORDER BY CASE d.downloadable WHEN true THEN 1 END, retry_count DESC, requested_at ASC LIMIT :batchSize FOR UPDATE SKIP LOCKED " + //
            "   ) " + //
            "   UPDATE  _reports.generated_report r " + //
            "   SET    report_status = 'STARTED' " + //
            "   FROM   jobs " + //
            "   WHERE  r.correlation_id = jobs.correlation_id " + //
            "   RETURNING CAST(r.correlation_id AS VARCHAR) ", nativeQuery = true)
    public List<String> getBatchOfPendingGenerateReportJobs(@Param("batchSize") int batchSize);

    /**
     * Finds awaiting GenerateReportEntity(report_request)'s that are in 'PENDING' status: Ordered By Priority, RetryCount, Requested date. <br/>
     * Such that the highest priority requests are dealt with first.
     * 
     * downloadable 'are essentially streamed reports for UI' therefore higher priority <br/>
     * highest retryCount first so that those already failed, can be enqueued first <br/>
     * and oldest requested_at so that earliest requests are dealt with first
     * 
     * @return GenerateReportEntity(report_request) of hightest priority to be enqueued for processing next
     */
    @Query(value = " SELECT CAST(r.correlation_id AS VARCHAR) " + // downloadable reports have higher priority (they are essentially streamed reports for UI)
            " FROM _reports.generated_report r " + //
            " JOIN _reports.destination d ON r.destination_id = d.destination_id " + // destinations gives us priority
            " WHERE cast(report_status AS varchar) = 'PENDING' " + //
            " ORDER BY CASE d.downloadable WHEN true THEN 1 END, retry_count DESC, requested_at ASC " + " LIMIT :batchSize FOR UPDATE SKIP LOCKED", nativeQuery = true) // ensure locking row, and skip any that are locked already
    public List<String> findReportRequestsToEnqueue(@Param("batchSize") int batchSize);

    @Modifying
    @Query(value = "UPDATE _reports.generated_report " + //
            "   SET report_status = 'STARTED' " + //
            "   WHERE correlation_id in (:correlationIds) ", nativeQuery = true)
    public int markAsStarted(@Param("correlationIds") List<UUID> correlationIds);

    /**
     * Finds GenerateReportEntity(report_request)'s that are in 'FAILED' status for more than x milliseconds: Ordered By Priority, RetryCount, Requested date. <br/>
     * Such that the highest priority requests are dealt with first.
     * 
     * downloadable 'are essentially streamed reports for UI' therefore higher priority <br/>
     * highest retryCount first so that those already failed, can be moved to PENDING again first <br/>
     * and oldest requested_at so that earliest requests are dealt with first
     * 
     * @param backOfIntervalInMilliseconds
     *            time in milliseconds to wait until next retry
     * @return GeneratedReportEntity(report_request) that is currently in FAILED status and needs to be retried
     */
    @Query(value = " SELECT *, CASE  d.downloadable  WHEN true THEN 1 END as priority " + // downloadable reports have higher priority (they are essentially streamed reports for UI)
            " FROM _reports.generated_report r " + //
            " JOIN _reports.destination d ON r.destination_id = d.destination_id " + // destinations gives us priority
            " WHERE cast(report_status AS varchar) = 'FAILED'  " + //
            " AND now() - cast(CONCAT(:backOfIntervalInMilliseconds, 'milliseconds') AS interval) >= generated_at" + // actually just WHERE now() - '120000 milliseconds'::interval >= generated_at in postgres SQL (but needing to get
                                                                                                                     // :backOfIntervalInMilliseconds as dynamic via hibernate into the query)
            " ORDER BY priority, retry_count DESC, requested_at ASC " + " LIMIT 1 FOR UPDATE SKIP LOCKED", nativeQuery = true) // ensure locking row, and skip any that are locked already
    public GeneratedReportEntity findFailedReportRequestsToRetry(@Param("backOfIntervalInMilliseconds") int backOfIntervalInMilliseconds);
}
