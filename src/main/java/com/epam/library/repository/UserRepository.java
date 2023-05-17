package com.epam.library.repository;

import com.epam.library.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

//Crud repository for the work with ´users´ table
public interface UserRepository extends CrudRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    List<User> findByRoleNot(User.Role admin);

    void deleteByEmail(String email);
}
