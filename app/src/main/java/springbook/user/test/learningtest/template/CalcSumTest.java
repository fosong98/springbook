package springbook.user.test.learningtest.template;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

public class CalcSumTest {
    @Test
    public void sumOfNumbers() throws IOException {
        Calculator calculator = new Calculator();
        int sum = calculator.calcSum(new ClassPathResource("/numbers.txt").getPath());
        assertEquals(sum, 10);
    }
}
