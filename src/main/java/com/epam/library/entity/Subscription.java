package com.epam.library.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    @ManyToOne
    @JoinColumn(name="book_id", nullable=false)
    private Book book;

    @Column(nullable = false)
    private boolean approved;

    @Column()
    private LocalDate startDate;

    @Column()
    private int period;

    @Column(nullable = false)
    private long fine;

    public Subscription() {}
    public Subscription(User user, Book book) {
        this.user = user;
        this.book = book;
        this.approved = false;
    }

    public boolean isApproved() {
        return approved;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public int getPeriod() {
        return period;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getId() {
        return id;
    }

    public long getFine() {
        return fine;
    }

    public void setFine(long fine) {
        this.fine = fine;
    }

    public void updateFine(int dayFine) {
        LocalDate currentDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(period);

        if (currentDate.isAfter(endDate)) {
            fine += dayFine;
            user.addFine(dayFine);
        }
    }
}
