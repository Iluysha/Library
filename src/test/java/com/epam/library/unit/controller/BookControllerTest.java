package com.epam.library.unit.controller;

import com.epam.library.controller.BookController;
import com.epam.library.entity.Book;
import com.epam.library.service.BookService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class BookControllerTest {

    @Mock
    private Model model;
    @Mock
    RedirectAttributes redirectAttributes;
    @Mock
    private BookService bookService;
    @InjectMocks
    private BookController controller;

    @Test
    public void testBooks() {
        // Arrange
        Page<Book> page = new PageImpl<>(List.of(new Book()));
        Mockito.when(bookService.getBooks(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
                Mockito.anyString(), Mockito.anyString())).thenReturn(page);

        // Act
        String result = controller.books(1, "title", "asc", "", "",
                redirectAttributes, model);

        // Assert
        assertEquals("books", result);
        Mockito.verify(bookService).getBooks(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testBooksError() {
        // Arrange
        Mockito.when(bookService.getBooks(Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(),
                Mockito.anyString(), Mockito.anyString())).thenThrow(new IllegalArgumentException());

        // Act
        String result = controller.books(1, "title", "asc",
                "", "", redirectAttributes, model);

        // Assert
        assertEquals("redirect:error", result);
        Mockito.verify(redirectAttributes).addFlashAttribute("msg_code", "invalid_page");
        Mockito.verify(bookService).getBooks(Mockito.anyString(), Mockito.anyString(),
                Mockito.anyInt(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testAddBookPage() {
        // Act
        String result = controller.addBookPage();

        // Assert
        assertEquals("add-book", result);
    }

    @Test
    public void testAddBook() throws Exception {
        // Arrange
        Mockito.when(bookService.add(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(new Book());

        // Act
        String result = controller.addBook("Title", "Author", "2023",
                redirectAttributes);

        // Assert
        assertEquals("redirect:books", result);
        Mockito.verify(bookService).add(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testAddBook_Error() throws Exception {
        // Arrange
        Mockito.when(bookService.add(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenThrow(new Exception());

        // Act
        String result = controller.addBook("Title", "Author", "2023",
                redirectAttributes);

        // Assert
        assertEquals("redirect:error", result);
        Mockito.verify(redirectAttributes).addFlashAttribute("msg_code", "invalid_input");
    }

    @Test
    public void testEditBookPage() throws Exception {
        // Assert
        int bookId = 10;

        Mockito.when(bookService.findById(bookId)).thenReturn(new Book());

        // Act
        String result = controller.editBookPage(bookId, redirectAttributes, model);

        // Assert
        assertEquals("edit-book", result);
    }

    @Test
    public void testEditBookPage_Error() throws Exception {
        // Assert
        int bookId = 10;

        Mockito.when(bookService.findById(bookId)).thenThrow(new Exception());

        // Act
        String result = controller.editBookPage(bookId, redirectAttributes, model);

        // Assert
        assertEquals("redirect:error", result);
        Mockito.verify(redirectAttributes).addFlashAttribute("msg_code", "invalid_input");
    }

    @Test
    public void testEditBook() throws Exception {
        // Act
        String result = controller.editBook(1, "Title", "Author", "2023",
                redirectAttributes);

        // Assert
        assertEquals("redirect:books", result);
        Mockito.verify(bookService).edit(1, "Title", "Author", "2023");
    }

    @Test
    public void testEditBook_Error() throws Exception {
        // Arrange
        Book book = new Book("Title", "Author", 2023);
        int bookId = 10;

        Mockito.when(bookService.edit(bookId, book.getTitle(),
                        book.getAuthor(), book.getPublicationYear().toString()))
                .thenThrow(new Exception());

        // Act
        String result = controller.editBook(bookId, book.getTitle(), book.getAuthor(),
                book.getPublicationYear().toString(), redirectAttributes);

        // Assert
        assertEquals("redirect:error", result);
        Mockito.verify(redirectAttributes).addFlashAttribute("msg_code", "invalid_input");
    }
}
