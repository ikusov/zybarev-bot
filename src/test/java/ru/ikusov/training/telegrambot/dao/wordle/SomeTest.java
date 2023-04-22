package ru.ikusov.training.telegrambot.dao.wordle;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class SomeTest {
    @Mock
    List<String> mockList;

    String EXPECTED = "abuba";

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void test1() {
        int expected = 4;

        int actual = 2 * 2;

        assertEquals(expected, actual);
    }

    @Test
    void test2() {
        when(mockList.get(anyInt())).thenReturn(EXPECTED);

        String actual = mockList.get(15);

        assertEquals(EXPECTED, actual);
        verify(mockList).get(15);
        verifyNoMoreInteractions(mockList);
    }
}