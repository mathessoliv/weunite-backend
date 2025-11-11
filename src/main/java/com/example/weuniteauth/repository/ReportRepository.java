package com.example.weuniteauth.repository;

import com.example.weuniteauth.domain.report.Report;
import com.example.weuniteauth.domain.users.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("SELECT r.entityId, r.type, COUNT(r) " +
           "FROM Report r " +
           "WHERE r.type = :type AND r.status = 'PENDING' " +
           "GROUP BY r.entityId, r.type " +
           "HAVING COUNT(r) >= :threshold " +
           "ORDER BY COUNT(r) DESC")
    List<Object[]> findEntitiesWithManyReports(@Param("type") Report.ReportType type, @Param("threshold") Long threshold);

    @Query("SELECT r.entityId, r.type, COUNT(r) " +
           "FROM Report r " +
           "WHERE r.type = :type " +
           "GROUP BY r.entityId, r.type " +
           "HAVING COUNT(r) >= :threshold " +
           "ORDER BY COUNT(r) DESC")
    List<Object[]> findAllEntitiesWithReports(@Param("type") Report.ReportType type, @Param("threshold") Long threshold);

    Long countByEntityIdAndTypeAndStatus(Long entityId, Report.ReportType type, Report.ReportStatus status);

    List<Report> findByEntityIdAndTypeAndStatus(Long entityId, Report.ReportType type, Report.ReportStatus status);

    List<Report> findByEntityIdAndType(Long entityId, Report.ReportType type);

    @Query("SELECT r FROM Report r WHERE r.status = 'PENDING' ORDER BY r.createdAt DESC")
    List<Report> findAllPendingReports();

    @Query("SELECT r FROM Report r ORDER BY r.createdAt DESC")
    List<Report> findAllReports();

    @Query("SELECT r FROM Report r WHERE r.status = :status ORDER BY r.createdAt DESC")
    List<Report> findAllReportsByStatus(@Param("status") Report.ReportStatus status);

    // Novos métodos para moderação
    @Query("SELECT r FROM Report r WHERE r.reporter = :user AND r.status = com.example.weuniteauth.domain.report.Report.ReportStatus.PENDING")
    List<Report> findPendingReportsByUser(@Param("user") User user);

    @Modifying
    @Query("UPDATE Report r SET r.status = :status, r.actionTaken = :action, r.resolvedByAdminId = :adminId, r.resolvedAt = :resolvedAt WHERE r.id = :reportId")
    void updateReportStatus(
            @Param("reportId") Long reportId,
            @Param("status") Report.ReportStatus status,
            @Param("action") Report.ActionTaken action,
            @Param("adminId") Long adminId,
            @Param("resolvedAt") Instant resolvedAt
    );

    @Modifying
    @Query("UPDATE Report r SET r.status = :status, r.actionTaken = :action, r.resolvedByAdminId = :adminId, r.resolvedAt = :resolvedAt WHERE r.reporter = :user AND r.status = com.example.weuniteauth.domain.report.Report.ReportStatus.PENDING")
    void resolveAllUserReports(
            @Param("user") User user,
            @Param("status") Report.ReportStatus status,
            @Param("action") Report.ActionTaken action,
            @Param("adminId") Long adminId,
            @Param("resolvedAt") Instant resolvedAt
    );
}

