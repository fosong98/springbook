package springbook.user.test.learningtest.jdk;

import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class ReflectionTest {
    @Test
    public void invokeMethod() throws Exception {
        String name = "Spring";

        assertEquals(6, name.length());

        Method lengthMethod = String.class.getMethod("length");
        assertEquals(6, lengthMethod.invoke(name));

        assertEquals('S', name.charAt(0));

        Method charAtMethod = String.class.getMethod("charAt", int.class);
        assertEquals('S', charAtMethod.invoke(name, 0));
    }
}
