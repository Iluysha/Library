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

/**
 * Controller class for managing book-related operations.
 */
@Controller
public class BookController {

    private static final Logger log = LogManager.getLogger(BookController.class);

    private final BookService bookService;

    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Handles the GET request to the /books URL.
     * Retrieves a list of books in the library and returns the "books" page,
     * which displays the list with the option to order the books.
     *
     * @param pageNo        The page number to retrieve (default: 1).
     * @param sortField     The field to sort the books by (default: title).
     * @param sortOrder     The sort order ("asc" for ascending, "desc" for descending) (default: asc).
     * @param searchQuery   The search query to filter the books by (default: "").
     * @param searchField   The field to search for the query in (default: "").
     * @param attributes    RedirectAttributes for adding flash attributes.
     * @param model         The Model object for passing data to the view.
     * @return The view name for rendering the "books" page.
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
            return "redirect:error";
        }
    }

    /**
     * Handles the GET request to the /add-book URL.
     * Returns the "add-book" page.
     *
     * @return The view name for rendering the "add-book" page.
     */
    @GetMapping("/add-book")
    public String addBookPage() {
        log.info("Received request for add book page");
        return "add-book";
    }

    /**
     * Handles the POST request to the /add-book URL.
     * Adds a new book with the specified title, author, and publication year.
     * If the book is added successfully, redirects to the "books" page.
     * Otherwise, redirects to the "error" page with a flash attribute indicating invalid input.
     *
     * @param bookTitle        The title of the book.
     * @param bookAuthor       The author of the book.
     * @param publicationYear  The publication year of the book.
     * @param attributes       RedirectAttributes for adding flash attributes.
     * @return The view name for redirection.
     */
    @PostMapping("/add-book")
    public String addBook(@RequestParam("bookTitle") String bookTitle,
                          @RequestParam("bookAuthor") String bookAuthor,
                          @RequestParam("publicationYear") String publicationYear,
                          RedirectAttributes attributes) {
        if(bookService.add(bookTitle, bookAuthor, publicationYear) != null) {
            return "redirect:books";
        } else {
            attributes.addFlashAttribute("msg_code", "invalid_input");
            return "redirect:error";
        }
    }
}