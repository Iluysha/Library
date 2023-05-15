package com.epam.library.unit.controller;

import com.epam.library.controller.MainController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MainControllerTest {

    @InjectMocks
    private MainController controller;

    @Test
    public void testEmpty() {
        // Act
        String result = controller.empty();

        // Assert
        assertEquals("redirect:books", result);
    }

    @Test
    public void testLogin() {
        // Act
        String result = controller.login();

        // Assert
        assertEquals("login", result);
    }
}
