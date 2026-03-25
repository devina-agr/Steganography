package org.spring.steganography.Repository;

import org.spring.steganography.Model.Role;
import org.spring.steganography.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepo extends MongoRepository<User,String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    List<User> findByRoleContaining(Role role);

    long countByRoleContaining(Role role);

    Page<User> findByRoleContaining(Role role, Pageable pageable);

   Optional<User> findByIdAndRole(String id, Role attr0);
}
