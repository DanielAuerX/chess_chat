package com.chrispbacon.chesschat.repository;

import com.chrispbacon.chesschat.model.user.Student;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<Student, UUID> {
  Optional<Student> findByEmail(String email);

  Optional<Student> findByUserName(String userName);
}
