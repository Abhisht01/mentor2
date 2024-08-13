package com.example.demo.repository;


import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Integer> {
    Optional<User>findByEmail(String email);
//    List<User> findAllByRole(List<Role> role);
    List<User> findAllByRoleName(String roleName);
}
