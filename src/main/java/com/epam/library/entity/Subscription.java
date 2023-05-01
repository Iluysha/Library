package com.epam.library.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

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

    @Column(name="approved", nullable = false)
    private boolean approved;

    @Column(name="startDate", nullable = false)
    private LocalDateTime startDate;

    @Column(name="period", nullable = false)
    private int period;

    public boolean isApproved() {
        return approved;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public int getPeriod() {
        return period;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public void setStartDate(LocalDateTime startDate) {
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
