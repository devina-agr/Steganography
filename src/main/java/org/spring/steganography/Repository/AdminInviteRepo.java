package org.spring.steganography.Repository;

import org.spring.steganography.Model.AdminInvite;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminInviteRepo extends MongoRepository<AdminInvite,String> {
}
