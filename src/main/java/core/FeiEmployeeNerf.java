package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class FeiEmployeeNerf {

    private static final String FEI_URL = "https://www.fei.vsb.cz/460/cs/kontakt/lide/";

    private static final Pattern NAME_PATTERN = Pattern.compile("<a[^>]*class=[\"']name[\"'][^>]*>(.*?)</a>");

    private Set<String> employeeFullNames;
    private boolean loaded = false;

    public void loadEmployeeNames() {
        employeeFullNames = new HashSet<>();
        try {
            URI uri = new URI(FEI_URL);
            HttpURLConnection connection = (HttpURLConnection) uri.toURL().openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");

            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                String webContent = in.lines().collect(Collectors.joining("\n"));

                Matcher matcher = NAME_PATTERN.matcher(webContent);
                while (matcher.find()) {
                    String rawName = matcher.group(1).trim();
                    employeeFullNames.add(rawName);
                }

                loaded = true;
                System.out.println("Nalezeno " + employeeFullNames.size() + " jmen na stránce katedry informatiky.");
            }

        } catch (IOException | URISyntaxException e) {
            System.err.println("Chyba při stahování dat ze stránky FEI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isFeiEmployee(String inputName) {
        if (!loaded || inputName == null || inputName.trim().isEmpty()) {
            return false;
        }

        String normalizedInput = normalizeString(inputName);
        String reversedInput = reverseName(normalizedInput);

        for (String fullName : employeeFullNames) {
            String normalizedFullName = normalizeString(fullName);

            if (normalizedFullName.contains(normalizedInput)) {
                return true;
            }
            if (normalizedFullName.contains(reversedInput)) {
                return true;
            }
        }

        return false;
    }

    private String reverseName(String name) {
        String[] parts = name.split("\\s+");
        if (parts.length >= 2) {
            return parts[parts.length - 1] + " " + parts[0];
        }
        return name;
    }

    private String normalizeString(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return normalized.toLowerCase().trim();
    }
}