package com.epam.library.service;

import com.epam.library.entity.Book;
import com.epam.library.entity.Subscription;
import com.epam.library.entity.User;
import com.epam.library.repository.SubscriptionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

//Service for crud operations with subscriptions
@Service
public class SubscriptionService {

    private static final Logger log = LogManager.getLogger(SubscriptionService.class);
    private final SubscriptionRepository repo;
    private final UserService userService;
    private final BookService bookService;

    public SubscriptionService(SubscriptionRepository repo, UserService userService,
                               BookService bookService) {
        this.repo = repo;
        this.userService = userService;
        this.bookService = bookService;
    }

    public void save(Subscription subscription) {
        log.info("Saving subscription: {}", subscription);
        repo.save(subscription);
    }

    public Optional<Subscription> findById(Integer id) {
        log.info("Finding subscription by id: {}", id);
        return repo.findById(id);
    }

    public List<Subscription> findByUserEmail(String user_email) {
        log.info("Finding subscriptions by user email: {}", user_email);
        return repo.findByUserEmail(user_email);
    }

    //Get a list of all subscriptions
    public List<Subscription> findAll() {
        log.info("Finding all subscriptions");
        return (List<Subscription>) repo.findAll();
    }

    public Subscription orderBook(UserDetails userDetails, Integer id) {
        log.info("Order book id: {} for user: {}", userDetails.getUsername(), id);

        Optional<User> optionalUser = userService.findByEmail(userDetails.getUsername());
        Optional<Book> optionalBook = bookService.getById(id);

        if (optionalUser.isPresent() && optionalBook.isPresent()) {
            User user = optionalUser.get();
            Book book = optionalBook.get();

            if (book.getAvailableCopies() > 0) {
                book.setAvailableCopies(book.getAvailableCopies() - 1);

                Subscription subscription = new Subscription();
                subscription.setUser(user);
                subscription.setBook(book);
                subscription.setStartDate(LocalDate.now());
                subscription.setApproved(false);
                subscription.setPeriod(60);

                save(subscription);
                bookService.save(book);

                log.info("Order placed successfully for user: {} and book: {}", user.getName(), book.getTitle());
                return subscription;
            } else {
                log.warn("Failed to place order for user: {} and book: {}. No such book or no available copies.",
                        userDetails.getUsername(), id);
                return null;
            }
        } else {
            log.warn("Failed to place order for user: {} and book: {}. No such book or no available copies.",
                    userDetails.getUsername(), id);
            return null;
        }
    }

    public Subscription approveSubscription(Integer id) {
        Optional<Subscription> optionalSubscription = findById(id);

        if(optionalSubscription.isPresent() && !optionalSubscription.get().isApproved()) {
            Subscription subscription = optionalSubscription.get();
            subscription.setApproved(true);
            save(subscription);

            log.info("Subscription with id: {} successfully approved", id);
            return subscription;
        } else {
            log.warn("Failed to approve subscription with id:  {}", id);
            return null;
        }
    }
}
