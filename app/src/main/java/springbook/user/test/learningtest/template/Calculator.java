package springbook.user.test.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public int calcSum(String filePath) throws IOException {
        LineCallback<Integer> sumCallback =
                new LineCallback<Integer>() {
                    @Override
                    public Integer doSomethingWithLine(String line, Integer value) {
                        return Integer.valueOf(line) + value;
                    }
                };

        return lineReadTemplate(filePath, sumCallback, 0);
    }

    public int calcMultiply(String filePath) throws IOException {
        LineCallback<Integer> multiplyCallback =
                new LineCallback<Integer>() {
                    @Override
                    public Integer doSomethingWithLine(String line, Integer value) {
                        return Integer.valueOf(line) * value;
                    }
                };

        return lineReadTemplate(filePath, multiplyCallback, 1);
    }

    public String concatenate(String filepath) throws IOException {
        LineCallback<String> concatenateCallback =
                new LineCallback<String>() {
                    @Override
                    public String doSomethingWithLine(String line, String value) {
                        return value + line;
                    }
                };

        return lineReadTemplate(filepath, concatenateCallback, "");
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

    public <T> T lineReadTemplate(String filePath, LineCallback<T> callback, T initVal) throws IOException {
        try (
                BufferedReader reader = new BufferedReader(new FileReader(filePath))
        ) {
            T res = initVal;
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
