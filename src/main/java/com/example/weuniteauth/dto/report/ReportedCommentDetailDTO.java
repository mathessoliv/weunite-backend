package com.example.weuniteauth.dto.report;

import com.example.weuniteauth.dto.CommentDTO;

import java.util.List;

public record ReportedCommentDetailDTO(
        CommentDTO comment,
        List<ReportDTO> reports,
        Long totalReports,
        String status
) {
}
