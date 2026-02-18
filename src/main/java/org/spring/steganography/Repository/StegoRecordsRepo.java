package org.spring.steganography.Repository;

import org.spring.steganography.Model.StegoRecords;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StegoRecordsRepo extends MongoRepository<StegoRecords,String> {
    List<StegoRecords> findByUserEmail(String userEmail);
}
