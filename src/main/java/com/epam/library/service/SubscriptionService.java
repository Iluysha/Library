package com.epam.library.service;

import com.epam.library.entity.Book;
import com.epam.library.entity.Subscription;
import com.epam.library.entity.User;
import com.epam.library.repository.SubscriptionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class for managing subscriptions.
 */
@Service
public class SubscriptionService {

    private static final Logger log = LogManager.getLogger(SubscriptionService.class);
    private final SubscriptionRepository repo;
    private final UserService userService;
    private final BookService bookService;
    private static final int dayFine = 10;

    public SubscriptionService(SubscriptionRepository repo, UserService userService,
                               BookService bookService) {
        this.repo = repo;
        this.userService = userService;
        this.bookService = bookService;
    }

    /**
     * Saves the subscription to the repository.
     *
     * @param subscription The subscription to save.
     */
    public void save(Subscription subscription) {
        log.info("Saving subscription: {}", subscription);
        repo.save(subscription);
    }

    /**
     * Finds a subscription by its ID.
     *
     * @param id The ID of the subscription to find.
     * @return the subscription if found, or throws exception otherwise.
     */
    public Subscription findById(Integer id) throws Exception {
        log.info("Finding subscription by id: {}", id);
        return repo.findById(id).orElseThrow(() -> new Exception("Subscription not found: " + id));
    }

    /**
     * Finds subscriptions by the user's email.
     *
     * @param user_email The email of the user.
     * @return A list of subscriptions belonging to the user.
     */
    public List<Subscription> findByUserEmail(String user_email) {
        log.info("Finding subscriptions by user email: {}", user_email);
        return repo.findByUserEmail(user_email);
    }

    /**
     * Retrieves a list of all subscriptions.
     *
     * @return A list of all subscriptions.
     */
    public List<Subscription> findAll() {
        log.info("Finding all subscriptions");
        return (List<Subscription>) repo.findAll();
    }

    /**
     * Places an order for a book by a user.
     *
     * @param userDetails The details of the authenticated user.
     * @param id           The ID of the book to order.
     * @return The created subscription if the order is successful, or throws exception otherwise.
     */
    @Transactional
    public Subscription orderBook(UserDetails userDetails, Integer id) throws Exception {
        log.info("Order book id: {} for user: {}", id, userDetails.getUsername());

        User user;
        Book book;

        try {
            user = userService.findByEmail(userDetails.getUsername());
            book = bookService.findById(id);
        } catch (Exception e) {
            log.error("Failed to place order for user: {} and book: {}. No such book.",
                    userDetails.getUsername(), id);
            throw e;
        }

        if (book.getAvailableCopies() < 0) {
            log.error("Failed to place order for user: {} and book: {}. No available copies.",
                        userDetails.getUsername(), id);
            throw new Exception("Failed to place order for user: " + userDetails.getUsername() +
                        " and book: " + id + ". No available copies.");
        }

        book.setAvailableCopies(book.getAvailableCopies() - 1);

        Subscription subscription = new Subscription(user, book);

        save(subscription);
        bookService.save(book);

        log.info("Order placed successfully for user: {} and book: {}", user.getName(), book.getTitle());
        return subscription;
    }

    /**
     * Approves a subscription by setting the approved flag and updating other attributes.
     *
     * @param id The ID of the subscription to approve.
     * @return The approved subscription if successful, or throws exception otherwise.
     */
    @Transactional
    public Subscription approveSubscription(Integer id) throws Exception {
        log.info("Approve subscription with id: {}", id);

        Subscription subscription;

        try {
            subscription = findById(id);
        } catch (Exception e) {
            log.error("Failed to approve subscription with id:  {}", id);
            throw new Exception("Failed to approve subscription with id: " + id);
        }

        if(subscription.isApproved()) {
            log.warn("Failed to approve subscription with id:  {}", id);
            throw new Exception("Failed to approve subscription with id: " + id);
        }

        subscription.setApproved(true);
        subscription.setStartDate(LocalDate.now());
        subscription.setPeriod(60);
        subscription.setFine(0);
        save(subscription);

        log.info("Subscription with id: {} successfully approved", id);
        return subscription;
    }

    /**
     * Calculates and adds fines to subscriptions.
     * This method is scheduled to run at midnight every day.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void calculateAndAddFines() {
        log.info("Updating subscriptions fines");

        List<Subscription> subscriptions = (List<Subscription>) repo.findAll();

        for (Subscription subscription : subscriptions) {
            subscription.updateFine(dayFine);
        }
    }
}
