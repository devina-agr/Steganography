package org.spring.steganography.Repository;

import org.spring.steganography.Model.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepo extends MongoRepository<AuditLog, String> {
}
