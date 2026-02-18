package org.spring.steganography.Repository;

import org.spring.steganography.Model.StegoImg;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StegoRecordsRepo extends MongoRepository<StegoImg,String> {
}
