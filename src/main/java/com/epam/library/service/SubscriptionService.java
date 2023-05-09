package com.epam.library.service;

import com.epam.library.controller.BookController;
import com.epam.library.entity.Subscription;
import com.epam.library.repository.SubscriptionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

//Service for crud operations with subscriptions
@Service
public class SubscriptionService {

    private static final Logger log = LogManager.getLogger(BookController.class);
    private final SubscriptionRepository repo;

    public SubscriptionService(SubscriptionRepository repo) {
        this.repo = repo;
    }

    public void save(Subscription subscription) {
        log.info("Saving subscription: {}", subscription);
        repo.save(subscription);
    }

    //Get a list of all subscriptions
    public List<Subscription> findAll() {
        log.info("Finding all subscriptions");
        return (List<Subscription>) repo.findAll();
    }

    public Optional<Subscription> findById(Integer id) {
        log.info("Finding subscription by id: {}", id);
        return repo.findById(id);
    }

    public List<Subscription> findByUserEmail(String user_email) {
        log.info("Finding subscriptions by user email: {}", user_email);
        return repo.findByUserEmail(user_email);
    }
}
