package com.epam.library.repository;

import com.epam.library.entity.User;
import org.springframework.data.repository.CrudRepository;

//Crud repository for the work with ´users´ table
public interface UserRepository extends CrudRepository<User, Integer> {
}
