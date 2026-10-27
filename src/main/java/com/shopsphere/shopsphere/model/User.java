package com.shopsphere.shopsphere.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

// @Entity tells Hibernate: this Java class maps to a database table
// Without this, Hibernate ignores this class completely
@Entity

// @Table tells Hibernate which exact table this maps to
// name="users" must match exactly what Flyway created in V1
@Table(name = "users")

// @Data is Lombok — auto-generates getters, setters, toString, equals, hashCode
// Without Lombok you'd write 100+ lines of boilerplate code manually
@Data

// @Builder allows: User.builder().email("a@b.com").role("BUYER").build()
// Clean way to create objects without calling setters one by one
@Builder

// @NoArgsConstructor — generates empty constructor: new User()
// JPA requires an empty constructor to create objects when reading from DB
@NoArgsConstructor

// @AllArgsConstructor — generates constructor with all fields
// Used by @Builder internally
@AllArgsConstructor
public class User {

    // @Id tells Hibernate: this is the primary key column
    @Id

    // @GeneratedValue tells Hibernate: don't set this manually
    // IDENTITY strategy = use PostgreSQL's BIGSERIAL auto-increment
    // Every new user gets the next available ID automatically
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @Column maps this Java field to the database column
    // name="full_name" matches the column in our V1 migration
    // nullable=false means NOT NULL — enforced at both Java and DB level
    // length=100 matches VARCHAR(100) in our SQL
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    // unique=true means no two users can have the same email
    // This is enforced at DB level — matches UNIQUE constraint in V1
    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    // We never store plain passwords — only bcrypt hashes
    // Column name matches password_hash in our SQL
    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    // Role can be BUYER, SELLER, or ADMIN
    // Default value set in DB, but we also set it here as safety
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private String role = "BUYER";

    // Soft delete flag — false means account is deactivated
    // We NEVER hard delete users in production
    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    // @Column(updatable=false) means Hibernate never updates this column
    // Once set at creation, createdAt never changes
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // @PrePersist runs automatically BEFORE a new record is inserted into DB
    // This sets createdAt and updatedAt when user is first created
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    // @PreUpdate runs automatically BEFORE an existing record is updated
    // Every time user data changes, updatedAt gets refreshed automatically
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}