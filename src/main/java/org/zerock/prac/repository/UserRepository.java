package org.zerock.prac.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.prac.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
