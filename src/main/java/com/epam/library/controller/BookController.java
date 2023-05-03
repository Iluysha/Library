package com.epam.library.controller;

import com.epam.library.entity.Book;
import com.epam.library.entity.Subscription;
import com.epam.library.service.BookService;
import com.epam.library.service.SubscriptionService;
import com.epam.library.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
public class BookController {

    private final UserService userService;

    private final SubscriptionService subscriptionService;

    private final BookService bookService;

    public BookController(UserService userService, SubscriptionService subscriptionService,
                          BookService bookService) {
        this.userService = userService;
        this.subscriptionService = subscriptionService;
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

    /*
        Method, handling get request to the /order URL after user press ´Order´ button.
        Gets the id of the book from the request, checks if this book exists in the library
        and there are available copies. Then reduce number of available copies by one and saves the book.
        Then returns order page with the message about the order.
        If there is no such a book in the library or no available copies, redirects to the error page.
    */
    @PostMapping("/order")
    public String orderBook(@AuthenticationPrincipal UserDetails userDetails,
                            @RequestParam("bookId") Integer id, Model model) {
        Optional<Book> optionalBook = bookService.getById(id);

        if(optionalBook.isPresent() && optionalBook.get().getAvailableCopies() > 0) {
            Book book = optionalBook.get();
            book.setAvailableCopies(book.getAvailableCopies() - 1);

            Subscription subscription = new Subscription();
            subscription.setUser(userService.findByEmail(userDetails.getUsername()).orElseThrow(() ->
                    new UsernameNotFoundException("User not found")));
            subscription.setBook(book);
            subscription.setStartDate(LocalDate.now());
            subscription.setApproved(false);
            subscription.setPeriod(60);

            subscriptionService.save(subscription);
            bookService.save(book);

            String message = "You have successfully ordered " + book.getTitle() + ".";
            model.addAttribute("message", message);

            return "order";
        } else {
            return "redirect:/error";
        }
    }

    @GetMapping("/add-book")
    public String addBookPage() {
        return "add-book";
    }

    @PostMapping("/add-book")
    public String addBook(@RequestParam("bookTitle") String bookTitle,
                          @RequestParam("bookAuthor") String bookAuthor,
                          @RequestParam("publicationYear") String publicationYear) {
        System.out.println(Integer.valueOf(publicationYear));

        Book newBook = new Book();
        newBook.setTitle(bookTitle);
        newBook.setAuthor(bookAuthor);
        newBook.setPublicationYear(Integer.valueOf(publicationYear));

        bookService.add(newBook);

        return "redirect:/books";
    }
}
