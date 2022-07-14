package springbook.user.test.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public Integer calcSum(String filePath) throws IOException {
        try (
            BufferedReader reader  = new BufferedReader(new FileReader(filePath));
        ) {
            Integer sum = 0;
            String line = null;
            while ((line = reader.readLine()) != null) {
                sum += Integer.parseInt(line);
            }
            return sum;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
