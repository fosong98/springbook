package springbook.user.test.learningtest.template;

import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Optional;

public class CalcSumTest {
    Calculator calculator;
    String filePath;

    @Before
    public void setUp() {
        calculator = new Calculator();
        filePath = new ClassPathResource("/numbers.txt").getPath();
    }

    @Test
    public void sumOfNumbers() throws IOException {
        assertEquals(calculator.calcSum(filePath), 10);
    }

    @Test
    public void multiplyOfNumbers() throws IOException {
        assertEquals(calculator.calcMultiply(filePath), 24);
    }
}
