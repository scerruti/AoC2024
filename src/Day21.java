import java.util.*;

public class Day21
{
    private static final char[][] NUMERIC_KEYS = {{'7', '8', '9'},{'4', '5', '6'}, {'1', '2', '3'}, {'x', '0', 'A'}};
    private static final char[][] DIRECTIONAL_KEYS = {{'x','^','A'}, {'<','v', '>'}};
    private static final int NUM_KEYPADS = 2;
    private static Map<Character, Map<Character, String>> numericPaths = new HashMap<>();
    private static Map<Character, Map<Character, String>> directionalPaths = new HashMap<>();


    public static void main(String[] args)
    {
        int score = 0;
        int seed = 29;
        buildNumericPaths();
        buildDirectionalPaths();

        for (String code : new String[]{"029A", "980A", "179A", "456A", "379A"})
        {
            score += processCode(code);
        }

        System.out.println(score);

    }

    private static int processCode(String code)
    {
        int seed = Integer.parseInt(code.substring(0, code.length() - 1));
        StringBuilder sequenceBuilder = generateNumericCode(code);

        char prev;
        String sequence;
        for (int keypad = 0; keypad < NUM_KEYPADS; keypad++)
        {
            sequence = sequenceBuilder.toString();
            sequenceBuilder = new StringBuilder();
            prev = 'A';
            for (char c : sequence.toCharArray())
            {
                sequenceBuilder.append(directionalPaths.get(prev).get(c));
                prev = c;
            }
            System.out.println(sequenceBuilder.toString());
        }
        System.out.print(code + " ");
        return score(seed, sequenceBuilder);
    }

    private static StringBuilder generateNumericCode(String code)
    {
        char prev = 'A';

        StringBuilder sequenceBuilder = new StringBuilder();
        for (char c : code.toCharArray())
        {
            sequenceBuilder.append(numericPaths.get(prev).get(c));
            prev = c;
        }
        System.out.println(sequenceBuilder.toString());
        return sequenceBuilder;
    }

    private static int score(int seed, StringBuilder sequenceBuilder)
    {
        String sequence = sequenceBuilder.toString();
        int length = sequence.length();
        System.out.printf("%d: %s%n", length, sequence);
        return seed * length;
    }

    private static Map<Character, Map<Character, String>> buildKeypadMap(char[][] keys) {
        Map<Character, Map<Character, String>> map = new HashMap<>();
        HashMap<Character, int[]> keyLocations = new HashMap<>();
        int width = keys[0].length;

        for (int r = 0; r < keys.length; r++)
        {
            for (int c = 0; c < keys[r].length; c++) {
                keyLocations.put(keys[r][c], new int[]{r, c});
            }
        }

        boolean topBias =  keyLocations.get('x')[0] == 0;

        for (int i = 0; i < keys.length * width; i++)
        {
            char from = keys[i / width][i % width];
            HashMap<Character, String> path = new HashMap<>();
            map.put(from, path);

            for (int j = 0; j < keys.length * width; j++)
            {
                char to = keys[j / width][j % width];
                StringBuilder sequence = new StringBuilder();
                int[] fromCoord = keyLocations.get(from);
                int[] toCoord = keyLocations.get(to);

                int row = toCoord[0] - fromCoord[0];
                int col = toCoord[1] - fromCoord[1];

                if (topBias && fromCoord[0] == 0 && fromCoord[1] == -col)
                {
                    sequence.append("v".repeat(Math.max(0, 1)));
                    row--;

                } else if (!topBias && fromCoord[0] == 3 && fromCoord[1] == -col)
                {
                    sequence.append("^".repeat(Math.max(0, -1)));
                    row++;
                }
                sequence.append(">".repeat(Math.max(0, col)));
                sequence.append("<".repeat(Math.max(0, -col)));
                sequence.append("v".repeat(Math.max(0, row)));
                sequence.append("^".repeat(Math.max(0, -row)));

                sequence.append("A");
                path.put(to, sequence.toString());
            }
        }
        return map;
    }

    private static void buildNumericPaths()
    {
        numericPaths = buildKeypadMap(NUMERIC_KEYS);
    }

    private static void buildDirectionalPaths()
    {
       directionalPaths = buildKeypadMap(DIRECTIONAL_KEYS);
    }
}
