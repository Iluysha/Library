package com.epam.library.service;

import com.epam.library.entity.Subscription;
import com.epam.library.repository.SubscriptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//Service for crud operations with subscriptions
@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository repo;

    public void save(Subscription subscription) {
        repo.save(subscription);
    }

    //Get a list of all subscriptions
    public List<Subscription> findAll() {
        return (List<Subscription>) repo.findAll();
    }
}
