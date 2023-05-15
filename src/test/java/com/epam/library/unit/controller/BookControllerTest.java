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
                Mockito.anyString(), Mockito.anyString())).thenReturn(null);

        // Act
        String result = controller.books(1, "title", "asc",
                "", "", redirectAttributes, model);

        // Assert
        assertEquals("redirect:/error", result);
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
    public void testAddBook() {
        // Arrange
        Mockito.when(bookService.add(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(new Book());

        // Act
        String result = controller.addBook("Title", "Author", "2023",
                redirectAttributes);

        // Assert
        assertEquals("redirect:/books", result);
        Mockito.verify(bookService).add(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void testAddBookError() {
        // Arrange
        Mockito.when(bookService.add(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(null);

        // Act
        String result = controller.addBook("Title", "Author", "2023",
                redirectAttributes);

        // Assert
        assertEquals("redirect:/error", result);
        Mockito.verify(redirectAttributes).addFlashAttribute("msg_code", "invalid_input");
        Mockito.verify(bookService).add(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());
    }
}
