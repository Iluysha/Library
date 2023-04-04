package com.epam.library.entity;

import jakarta.persistence.*;

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

    @Column(length = 45, nullable = false)
    private boolean approved;

    @Column(length = 45, nullable = false)
    private String startDate;

    @Column(length = 45, nullable = false)
    private int period;

    public boolean isApproved() {
        return approved;
    }

    public String getStartDate() {
        return startDate;
    }

    public int getPeriod() {
        return period;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public void setStartDate(String startDate) {
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
}
