package com.epam.library.controller;

import com.epam.library.entity.Book;
import com.epam.library.service.BookService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BookController {

    private static final Logger log = LogManager.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /*
        Method, handling get request to the /books URL.
        Gets the list of books in the library and returns the page ´books´,
        which prints this list with possibility of ordering.
    */
    @GetMapping("/books")
    public String books(@RequestParam(defaultValue = "1") int pageNo,
                        @RequestParam(name = "sortField", defaultValue = "title") String sortField,
                        @RequestParam(name = "sortOrder", defaultValue = "asc") String sortOrder,
                        @RequestParam(name = "query", required = false) String query,
                        @RequestParam(name = "field", required = false) String field,
                        Model model) {

        log.info("Received request to fetch books. PageNo: {}, SortField: {}, SortOrder: {}, Query: {}, Field: {}",
                pageNo, sortField, sortOrder, query, field);

        Page<Book> page;

        if (query != null && field != null && !query.equals("")) {
            page = bookService.searchBooks(query, field, pageNo, sortField, sortOrder);
        } else {
            page = bookService.getBooks(pageNo, sortField, sortOrder);
        }

        List<Book> books = page.getContent();

        model.addAttribute("books", books);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", Math.max(1, page.getTotalPages()));
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortOrder", sortOrder);

        return "books";
    }

    @GetMapping("/add-book")
    public String addBookPage() {
        log.info("Received request for add book page");
        return "add-book";
    }

    @PostMapping("/add-book")
    public String addBook(@RequestParam("bookTitle") String bookTitle,
                          @RequestParam("bookAuthor") String bookAuthor,
                          @RequestParam("publicationYear") String publicationYear) {
        log.info("Received request to add a book. Title: {}, Author: {}, PublicationYear: {}",
                bookTitle, bookAuthor, publicationYear);

        Book newBook = new Book();
        newBook.setTitle(bookTitle);
        newBook.setAuthor(bookAuthor);
        newBook.setPublicationYear(Integer.valueOf(publicationYear));

        bookService.add(newBook);

        return "redirect:/books";
    }
}