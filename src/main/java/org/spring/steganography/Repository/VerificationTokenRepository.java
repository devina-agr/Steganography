package org.spring.steganography.Repository;

import org.spring.steganography.Model.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
    Optional<VerificationToken> findByToken(String token);

    void deleteByExpiryBefore(LocalDateTime now);
}
