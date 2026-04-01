package com.sujith.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.springframework.stereotype.Service;

@Service
public class CodeExecutor {

    public String runCode(String code) {
        File file = new File("Solution.java");

        try (FileWriter writer = new FileWriter(file)) {
            writer.write(code);
        } catch (IOException e) {
            return "Error writing code: " + e.getMessage();
        }

        try {
            Process compile = new ProcessBuilder("javac", "Solution.java")
                    .redirectErrorStream(true)
                    .start();
            String compileOutput = readStream(compile.getInputStream());
            int compileExit = compile.waitFor();
            if (compileExit != 0) {
                return "Compilation failed:\n" + compileOutput;
            }

            Process run = new ProcessBuilder("java", "Solution")
                    .redirectErrorStream(true)
                    .start();
            String programOutput = readStream(run.getInputStream());
            run.waitFor();
            return programOutput;

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Error: " + e.getMessage();
        } finally {
            file.delete();
        }
    }

    private String readStream(java.io.InputStream stream) throws IOException {
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (output.length() > 0) {
                    output.append(System.lineSeparator());
                }
                output.append(line);
            }
        }
        return output.toString();
    }
}