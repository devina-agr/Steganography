package org.spring.steganography.Service;

import org.spring.steganography.Model.AuditLog;
import org.spring.steganography.Repository.AuditLogRepo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private final AuditLogRepo auditLogRepo;

    public AuditService(AuditLogRepo auditLogRepo) {
        this.auditLogRepo = auditLogRepo;
    }

    public void log(String action, String performedBy, String targetId) {

        AuditLog log=AuditLog.builder()
                .action(action)
                .performedBy(performedBy)
                .targetId(targetId)
                .timestamp(LocalDateTime.now())
                .build();
        auditLogRepo.save(log);
    }

}
