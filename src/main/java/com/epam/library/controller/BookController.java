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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
                        @RequestParam(name = "searchQuery", defaultValue = "") String searchQuery,
                        @RequestParam(name = "searchField", defaultValue = "") String searchField,
                        RedirectAttributes attributes,
                        Model model) {

        Page<Book> page = bookService.getBooks(searchQuery, searchField, pageNo, sortField, sortOrder);

        if(page != null) {
            model.addAttribute("books", page.getContent());
            model.addAttribute("pageNo", pageNo);
            model.addAttribute("totalPages", Math.max(1, page.getTotalPages()));
            model.addAttribute("sortField", sortField);
            model.addAttribute("sortOrder", sortOrder);
            model.addAttribute("searchQuery", searchQuery);
            model.addAttribute("searchField", searchField);

            return "books";
        } else {
            attributes.addFlashAttribute("msg_code", "invalid_page");
            return "redirect:/error";
        }
    }

    @GetMapping("/add-book")
    public String addBookPage() {
        log.info("Received request for add book page");
        return "add-book";
    }

    @PostMapping("/add-book")
    public String addBook(@RequestParam("bookTitle") String bookTitle,
                          @RequestParam("bookAuthor") String bookAuthor,
                          @RequestParam("publicationYear") String publicationYear,
                          RedirectAttributes attributes) {
        if(bookService.add(bookTitle, bookAuthor, publicationYear) != null) {
            return "redirect:/books";
        } else {
            attributes.addFlashAttribute("msg_code", "invalid_input");
            return "redirect:/error";
        }
    }
}