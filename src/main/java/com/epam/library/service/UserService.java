package com.epam.library.service;

import com.epam.library.entity.User;
import com.epam.library.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//Service for crud operations with users
@Service
public class UserService {

    @Autowired
    private UserRepository repo;

    public void save(User user) {
        repo.save(user);
    }

    //Get a list of all users
    public List<User> findAll() {
        return (List<User>) repo.findAll();
    }
}
