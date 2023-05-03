package com.epam.library.repository;

import com.epam.library.entity.Subscription;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

//Crud repository for the work with ´subscription´ table
public interface SubscriptionRepository extends CrudRepository<Subscription, Integer> {
    List<Subscription> findByUserEmail(String user_email);
}
