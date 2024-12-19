import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Pattern;

public class Day19
{

    private static final ArrayList<String> brandPatterns = new ArrayList<>();
    private static final String FILE_NAME = "puzzle/day19.txt";
    private static List<String> towels = new ArrayList<>();
    private static Pattern pattern;
    private static Pattern incompletePattern;
    private static final HashMap<String, Long> memo = new HashMap<>();

    public static void main(String[] args)
    {
        int matched = 0;

        readDataFromFile(FILE_NAME);
        // Print the results for verification
        System.out.println("First Line: " + towels);
        System.out.println("Following Lines: " + brandPatterns);

        part1(matched);
        part2();
    }

    private static void part1(int matched)
    {
        // Create a regex pattern from the words
        String regexPattern = "^(" + String.join("|", towels) + ")+";
        incompletePattern = Pattern.compile(regexPattern);
        regexPattern += "$";
        pattern = Pattern.compile(regexPattern);

        for (String candidate : brandPatterns) {
            // Match the candidate string against the pattern
            if (matches(candidate)) {
                matched++;
            }
        }
        System.out.println("Matched: " + matched);
    }

    private static boolean matches(String brandPattern)
    {
        return matches(brandPattern, true);
    }

    private static boolean matches(String brandPattern, boolean complete) {
        if (complete)
            return pattern.matcher(brandPattern).matches();
        else
            return incompletePattern.matcher(brandPattern).matches();
    }

    private static void part2() {
        long combinations = 0;

        for (String brandPattern : brandPatterns) {
            if (!matches(brandPattern)) continue;
            combinations += combinationsOf(brandPattern);
            System.out.println("Combinations: " + combinations);
        }

        System.out.println("Part 2: "+ combinations);
    }

    private static long combinationsOf(String brandPattern)
    {
        if (brandPattern.isEmpty()) return 1;

        if (memo.containsKey(brandPattern)) return memo.get(brandPattern);

        long total = 0;
        for (String towel : towels) {
            if (brandPattern.startsWith(towel)) {
                long combinations = combinationsOf(brandPattern.substring(towel.length()));
                if (combinations != 0) {
                    total += combinations;
                    memo.merge(brandPattern, combinations, Long::sum);
                }
            }
        }
        return total;
    }

    private static void oldPart2Again() {
        HashMap<Character, ArrayList<String>> towelForLetter = new HashMap<>();
        for (String towel : towels ) {
            char letter = towel.charAt(0);
            towelForLetter.putIfAbsent(letter, new ArrayList<>());
            towelForLetter.get(letter).add(towel);
        }

        int combinations = 0;

        for (String brandPattern : brandPatterns) {
            if (!matches(brandPattern)) continue;
            combinations += arrangements(new StringBuilder(), brandPattern, towelForLetter);
            System.out.println(combinations);
        }

        System.out.println("Part 2: " + combinations);
    }

    private static int arrangements(StringBuilder s, String brandPattern, HashMap<Character, ArrayList<String>> towelForLetter)
    {
        if (s.toString().equals(brandPattern)) {
            return 1;
        } else if (s.length() > brandPattern.length() || !brandPattern.startsWith(s.toString())) {
            return 0;
        }

        int matched = 0;
        char nextLetter = brandPattern.charAt(s.length());
        ArrayList<String> towels = towelForLetter.get(nextLetter);
        if (towels == null) return 0;

        for (String towel : towelForLetter.get(nextLetter)) {
            s.append(towel);
            matched += arrangements(s, brandPattern, towelForLetter);
            s.delete(s.length() - towel.length(), s.length());
        }
        return matched;
    }

    private static void oldPart2() {
        int valid = 0;
        for (String candidate : brandPatterns) {
            if (!matches(candidate)) continue;

            System.out.printf("######## %s%n", candidate);
            List<String> validTowels = towels.stream().filter(candidate::contains).toList();

            List<String> combinations = generateCombinations(validTowels, candidate);
            valid += combinations.size();
            for (String combination : combinations) {
                System.out.printf("\t%s%n", combination);
            }
        }
        System.out.println("Part 2: " + valid);
    }

    public static List<String> generateCombinations(List<String> words, String candidate) {
        List<String> combinations = new ArrayList<>();
        generateCombinationsRecursive(words, candidate, new StringBuilder(), combinations, new ArrayList<>());
        return combinations;
    }

    private static void generateCombinationsRecursive(List<String> words, String candidate, StringBuilder current, List<String> combinations, ArrayList<Integer> visited) {
        if (current.toString().equals(candidate)) {
            combinations.add(current.toString());
            return;
        } else if (current.length() > candidate.length() ||
                (!current.isEmpty() && !candidate.startsWith(current.toString()))) {
            return;
        }

        if (visited.size() > 20)
           System.out.printf("%08d %-58s [%d] %s\r", combinations.size(), current, visited.size(), visited.subList(0, 20));
        visited.add(0);
        for (int i = 0; i < words.size(); i++) {
            visited.set(visited.size() - 1, i);
            current.append(words.get(i));
            generateCombinationsRecursive(words, candidate, current, combinations, new ArrayList<>(visited));
            current.delete(current.length() - words.get(i).length(), current.length());
        }
    }

    private static void readDataFromFile(String fileName)
    {
        InputStream stream = Day19.class.getResourceAsStream(fileName);
        if (stream == null)
        {
            System.err.println("File not found: " + fileName);
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            String line;
            // Read the first line and split it into individual strings
            if ((line = reader.readLine()) != null)
            {
                towels = Arrays.asList(line.split(", "));
            }
            reader.readLine();

            // Read the following lines and add each line to the followingLines list
            while ((line = reader.readLine()) != null)
            {
                brandPatterns.add(line);
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
