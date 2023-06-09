package com.epam.library.entity;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name="books")
public class Book {

    //Unique id for each book, autogenerated when creating a book
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //The title of the book
    @Column(length = 64, nullable = false, unique = true)
    private String title;

    //The book author´s name
    @Column(length = 64, nullable = false)
    private String author;

    //The year of publication of the book
    @Column(nullable = false)
    private int publicationYear;

    //The number of copies in this library
    @Column(nullable = false)
    private int numOfCopies;

    //The number of books, available for ordering by readers
    @Column(nullable = false)
    private int availableCopies;

    public Book() {}

    public Book(String title, String author, Integer publicationYear) {
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.numOfCopies = 1;
        this.availableCopies = 1;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getNumOfCopies() {
        return numOfCopies;
    }

    public void setNumOfCopies(int numOfCopies) {
        this.numOfCopies = numOfCopies;
    }

    public Integer getId() {
        return id;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public void addCopy() {
        numOfCopies++;
        availableCopies++;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPublicationYear() {
        return publicationYear;
    }

    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Book book = (Book) o;
        return publicationYear == book.publicationYear && numOfCopies == book.numOfCopies &&
                availableCopies == book.availableCopies && title.equals(book.title) && author.equals(book.author);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, author, publicationYear, numOfCopies, availableCopies);
    }
}
