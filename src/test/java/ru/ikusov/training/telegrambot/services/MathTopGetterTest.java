package ru.ikusov.training.telegrambot.services;

import junit.framework.TestCase;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import ru.ikusov.training.telegrambot.MyConfig;

import java.util.Optional;

public class MathTopGetterTest extends TestCase {
    private String AVTFTALK_CHAT_ID;
    private AnnotationConfigApplicationContext context;
    private MathTopGetter mathTopGetter;

    public void setUp() throws Exception {
        super.setUp();
        context = new AnnotationConfigApplicationContext(MyConfig.class);
        mathTopGetter = context.getBean(MathTopGetter.class);
        AVTFTALK_CHAT_ID =
                Optional.ofNullable((System.getenv("avtftalk_chat_id"))).orElse("");
    }

    public void tearDown() throws Exception {
        context.close();
    }

    public void testGetMathTop() {
        assertNotNull(AVTFTALK_CHAT_ID);
        assertNotSame(AVTFTALK_CHAT_ID, "");

        var mathTop = mathTopGetter.getMathTop(AVTFTALK_CHAT_ID);
        assertNotNull(mathTop);
        var size = mathTop.size();
        if (size > 1) {
            long first = mathTop.get(0).getValue(),
                    second = mathTop.get(1).getValue(),
                    beforeLast = mathTop.get(size-2).getValue(),
                    last = mathTop.get(size-1).getValue();
            assertTrue(first >= second);
            assertTrue(second >= beforeLast);
            assertTrue(beforeLast >= last);
        }
    }
}