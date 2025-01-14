package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.*;

public class SimpleTemplateSubstitutor {

    public static void main(String[] args) {
        try {
            String template = readFileFromResources("template.txt");
            Map<String, String> substitutions = readSubstitutionsFromResources("substitutions.txt");

            List<String> sortedVariableNames = new ArrayList<>(substitutions.keySet());
            sortedVariableNames.sort((a, b) -> b.length() - a.length());

            String result = applySubstitutions(template, substitutions, sortedVariableNames);

            System.out.println(result);

        } catch (IOException e) {
            System.err.println("Error reading files: " + e.getMessage());
        }
    }

    private static String readFileFromResources(String resourcePath) throws IOException {
        InputStream inputStream = SimpleTemplateSubstitutor.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new FileNotFoundException("File not found in resources: " + resourcePath);
        }
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString().trim();
    }

    private static Map<String, String> readSubstitutionsFromResources(String resourcePath) throws IOException {
        InputStream inputStream = SimpleTemplateSubstitutor.class.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new FileNotFoundException("File not found in resources: " + resourcePath);
        }
        Map<String, String> substitutions = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String varName = parts[0];
                    String varValue = parts[1];
                    substitutions.put(varName, varValue);
                }
            }
        }
        return substitutions;
    }

    private static String applySubstitutions(String template,
                                             Map<String, String> substitutions,
                                             List<String> sortedVariableNames) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < template.length()) {
            char currentChar = template.charAt(i);

            if (currentChar == '$') {
                boolean foundVariable = false;
                for (String varName : sortedVariableNames) {
                    int varLen = varName.length();
                    int endPos = i + 1 + varLen;
                    if (endPos <= template.length()) {
                        String candidate = template.substring(i + 1, endPos);
                        if (candidate.equals(varName)) {
                            sb.append(substitutions.getOrDefault(varName, ""));
                            i = endPos;
                            foundVariable = true;
                            break;
                        }
                    }
                }
                if (!foundVariable) {
                    sb.append('$');
                    i++;
                }
            } else {
                sb.append(currentChar);
                i++;
            }
        }
        return sb.toString();
    }
}