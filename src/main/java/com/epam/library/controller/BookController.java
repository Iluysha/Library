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

        Page<Book> page = bookService.getBooks(query, field, pageNo, sortField, sortOrder);

        model.addAttribute("books", page.getContent());
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
        bookService.add(bookTitle, bookAuthor, publicationYear);
        return "redirect:/books";
    }
}