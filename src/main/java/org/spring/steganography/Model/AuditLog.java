package org.spring.steganography.Model;
import lombok.Builder;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


import java.time.LocalDateTime;

@Document(collection = "AuditLog")
@Builder
public class AuditLog {

    @Id
    private String id;
    private String action;
    private String performedBy;
    private String targetId;
    @Indexed(expireAfter = "P30D")
    private LocalDateTime timestamp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPerformedBy() {
        return performedBy;
    }

    public void setPerformedBy(String performedBy) {
        this.performedBy = performedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }
}