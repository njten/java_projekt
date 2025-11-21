package core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private static final Pattern NAME_PATTERN = Pattern.compile("<a class='name' href='[^']*'>([^<]+)</a>");

    private Set<String> employeeFullNames;
    private boolean loaded = false;

    public void loadEmployeeNames() {
        employeeFullNames = new HashSet<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(new URI(FEI_URL).toURL().openStream(), StandardCharsets.UTF_8))) {
            String webContent = in.lines().collect(Collectors.joining("\n"));

            Matcher matcher = NAME_PATTERN.matcher(webContent);
            while (matcher.find()) {
                employeeFullNames.add(matcher.group(1));
            }

            loaded = true;
            System.out.println("Nalezeno " + employeeFullNames.size() + " jmen na stránce katedry informatiky.");

            if (!employeeFullNames.isEmpty()) {
                System.out.println("Příklad nalezených jmen: " + employeeFullNames.stream().limit(5).collect(Collectors.toList()));
            } else {
                System.out.println("Nepodařilo se nalézt žádná jména. Regulární výraz nebo struktura stránky může být opět chybná.");
            }

        } catch (IOException | URISyntaxException e) {
            System.err.println("Chyba při stahování nebo parsování dat ze stránky FEI: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean isFeiEmployee(String inputName) {
        if (!loaded) {
            System.err.println("Seznam zaměstnanců ještě není načten.");
            return false;
        }
        if (inputName == null || inputName.trim().isEmpty()) {
            return false;
        }

        String normalizedInput = normalizeString(inputName);

        for (String fullName : employeeFullNames) {
            String normalizedFullName = normalizeString(fullName);
            if (normalizedFullName.contains(normalizedInput)) {
                return true;
            }
        }

        return false;
    }

    private String normalizeString(String text) {
        if (text == null) return "";
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return normalized.toLowerCase();
    }
}