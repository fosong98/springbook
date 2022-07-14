package springbook.user.test.learningtest.template;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public int calcSum(String filePath) throws IOException {
        BufferedReaderCallback sumCallback =
                new BufferedReaderCallback() {
                    @Override
                    public Integer doSomethingWithReader(BufferedReader reader) throws IOException {
                        Integer sum = 0;
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            sum += Integer.parseInt(line);
                        }
                        return sum;
                    }
                };

        return fileReaderTemplate(filePath, sumCallback);
    }

    public int calcMultiply(String filePath) throws IOException {
        BufferedReaderCallback multiplyCallback =
                new BufferedReaderCallback() {
                    @Override
                    public Integer doSomethingWithReader(BufferedReader reader) throws IOException {
                        int multiply = 1;
                        String line = null;
                        while ((line = reader.readLine()) != null) {
                            multiply *= Integer.valueOf(line);
                        }

                        return multiply;
                    }
                };

        return fileReaderTemplate(filePath, multiplyCallback);
    }

    public Integer fileReaderTemplate(String filePath, BufferedReaderCallback callback) throws IOException {
        try (
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
        ) {
            return callback.doSomethingWithReader(reader);
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
