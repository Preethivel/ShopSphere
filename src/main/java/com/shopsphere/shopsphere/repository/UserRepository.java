package com.shopsphere.shopsphere.repository;

import com.shopsphere.shopsphere.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

// @Repository tells Spring: this is a data access component
// Spring registers it and makes it available for injection everywhere
@Repository

// JpaRepository<User, Long>:
// User = which entity/table this repository manages
// Long = the data type of the primary key (id column is BIGINT = Long in Java)
// By extending JpaRepository, you get these methods FOR FREE — no code needed:
// save(user) → INSERT or UPDATE
// findById(id) → SELECT WHERE id = ?
// findAll() → SELECT * FROM users
// delete(user) → DELETE
// count() → SELECT COUNT(*)
public interface UserRepository extends JpaRepository<User, Long> {

    // Spring reads this method name and auto-generates the SQL:
    // SELECT * FROM users WHERE email = ?
    // You don't write any SQL — Spring builds it from the method name
    Optional<User> findByEmail(String email);

    // Generates: SELECT COUNT(*) > 0 FROM users WHERE email = ?
    // Used during registration to check if email already exists
    boolean existsByEmail(String email);
}