package springbook.user.test.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public int calcSum(String filePath) throws IOException {
        LineCallback sumCallback =
                new LineCallback() {
                    @Override
                    public Integer doSomethingWithLine(String line, Integer value) {
                        return Integer.valueOf(line) + value;
                    }
                };

        return lineReadTemplate(filePath, sumCallback, 0);
    }

    public int calcMultiply(String filePath) throws IOException {
        LineCallback multiplyCallback =
                new LineCallback() {
                    @Override
                    public Integer doSomethingWithLine(String line, Integer value) {
                        return Integer.valueOf(line) * value;
                    }
                };

        return lineReadTemplate(filePath, multiplyCallback, 1);
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

    public Integer lineReadTemplate(String filePath, LineCallback callback, int initVal) throws IOException {
        try (
                BufferedReader reader = new BufferedReader(new FileReader(filePath))
        ) {
            Integer res = initVal;
            String line = null;
            while ((line = reader.readLine()) != null) {
                res = callback.doSomethingWithLine(line, res);
            }
            return res;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
