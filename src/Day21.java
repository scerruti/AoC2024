import java.util.*;
import java.util.stream.Collectors;

public class Day21
{
    private static final char[][] NUMERIC_KEYS = {{'7', '8', '9'}, {'4', '5', '6'}, {'1', '2', '3'}, {'x', '0', 'A'}};
    private static final char[][] DIRECTIONAL_KEYS = {{'x', '^', 'A'}, {'<', 'v', '>'}};
    private static final int NUM_KEYPADS = 2;
    private static Map<Character, Map<Character, ArrayList<String>>> numericPaths = new HashMap<>();
    private static Map<Character, Map<Character, ArrayList<String>>> directionalPaths = new HashMap<>();


    public static void main(String[] args)
    {
        int score = 0;
        int seed = 29;
        String[] testInput = new String[]{"029A", "980A", "179A", "456A", "379A"};
        String[] input = new String[]{"879A", "508A", "463A", "593A", "189A"};

        for (String code : input)
        {
            score += processCode(code);
        }

        System.out.println(score);

    }

    private static int processCode(String code)
    {
        int seed = Integer.parseInt(code.substring(0, code.length() - 1));

        ArrayDeque<StringBuilder> stack = new ArrayDeque<>();
        stack.offer(new StringBuilder());
        char prev = 'A';
        for (char c : code.toCharArray()) {
            ArrayDeque<StringBuilder> temp = new ArrayDeque<>();
            while (!stack.isEmpty()) {
                StringBuilder current = stack.pop();
                for (String path : KeypadPathsGenerator.getNumericPaths(prev, c)) {
                    temp.offer(new StringBuilder(current).append(path).append("A"));
                }
            }
            prev = c;
            stack = temp;
        }

        int shortestLength = Integer.MAX_VALUE;
        StringBuilder result = null;
        for (StringBuilder expandedCode: stack) {
            StringBuilder current = generateNumericCode(expandedCode, 25);
            if (current.length() < shortestLength) {
                shortestLength = current.length();
                result = current;
            }
        }

        return score(seed, result);
    }

    private static StringBuilder generateNumericCode(StringBuilder code, int depth)
    {
        Set<StringBuilder> paths = new HashSet<>();
        paths.add(code);


        for (int level = depth; level > 0; level--) {
            System.out.println(level + " " + paths.size());
            Set<StringBuilder> temp = new HashSet<>();
            for (StringBuilder s : paths)
            {
                temp.addAll(encode(s));
            }

            int length = Integer.MAX_VALUE;
            for (StringBuilder s : temp) {
                if (s.length() < length) {
                    length = s.length();
                }
            }

            int finalLength = length;
            paths = temp.stream().filter(s -> s.length() <= finalLength).collect(Collectors.toSet());
        }

        return paths.stream()
                .min(Comparator.comparingInt(StringBuilder::length))
                .orElse(null);
    }

    private static List<StringBuilder> encode(StringBuilder code) {
        ArrayDeque<StringBuilder> stack = new ArrayDeque<>();
        stack.offer(new StringBuilder());
        char prev = 'A';
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            ArrayDeque<StringBuilder> temp = new ArrayDeque<>();
            while (!stack.isEmpty()) {
                StringBuilder current = stack.pop();
                for (String path : KeypadPathsGenerator.getDirectionalPaths(prev, c)) {
                    temp.offer(new StringBuilder(current).append(path).append("A"));
                }
            }
            prev = c;
            stack = temp;
        }
        return stack.stream().toList();
    }

//    private static StringBuilder generateNumericCode(StringBuilder code, int depth)
//    {
//        if (depth == 0) {
//            return code;
//        }
//
//        ArrayDeque<StringBuilder> stack = new ArrayDeque<>();
//        stack.offer(new StringBuilder());
//        char prev = 'A';
//        for (int i = 0; i < code.length(); i++) {
//            char c = code.charAt(i);
//            ArrayDeque<StringBuilder> temp = new ArrayDeque<>();
//            while (!stack.isEmpty()) {
//                StringBuilder current = stack.pop();
//                for (String path : KeypadPathsGenerator.getDirectionalPaths(prev, c)) {
//                    temp.offer(new StringBuilder(current).append(path).append("A"));
//                }
//            }
//            prev = c;
//            stack = temp;
//        }
//
//        int shortestLength = Integer.MAX_VALUE;
//        StringBuilder result = null;
//        for (StringBuilder expandedCode: stack) {
//            StringBuilder current = generateNumericCode(expandedCode, depth - 1);
//            if (current.length() < shortestLength) {
//                shortestLength = current.length();
//                result = current;
//            }
//        }
//
//        return result;
//    }

    private static int score(int seed, StringBuilder sequenceBuilder)
    {
        String sequence = sequenceBuilder.toString();
        int length = sequence.length();
        System.out.printf("%d: %s%n", length, sequence);
        return seed * length;
    }

    private static Map<Character, Map<Character, ArrayList<String>>> buildKeypadMap(char[][] keys)
    {
        Map<Character, Map<Character, ArrayList<String>>> map = new HashMap<>();
        int width = keys[0].length;


        for (int from = 0; from < keys.length * width; from++)
        {
            HashMap<Character, ArrayList<String>> allPaths = new HashMap<>();
            map.put(keys[from / width][from % width], allPaths);

            for (int to = 0; to < keys.length * width; to++)
            {
                int shortestPath = Integer.MAX_VALUE;
                int[] steps = new int[keys.length * width];
                Arrays.fill(steps, Integer.MAX_VALUE);

                ArrayList<String> paths = new ArrayList<>();
                allPaths.put(keys[to / width][to % width], paths);

                ArrayDeque<int[]> stack = new ArrayDeque<>();
                stack.push(new int[]{from});

                while (!stack.isEmpty())
                {
                    int[] current = stack.pop();
                    int lastValue = current[current.length - 1];

                    if (lastValue == to)
                    {
                        paths.add(Arrays.stream(current)
                                .mapToObj(i -> String.valueOf(keys[i / width][i % width]))
                                .reduce("", String::concat));
                        continue;
                    } else if (steps[lastValue] > shortestPath)
                    {
                        continue;
                    }

                    for (int offset : new int[]{-width, 1, width, -1})
                    {
                        int neighbor = lastValue + offset;
                        if (neighbor < 0 || keys.length <= neighbor ||
                                (Math.abs(offset) == 1 && lastValue / width != neighbor / width))
                        {
                            continue;
                        }

                        if (steps[neighbor] <= steps[lastValue] + 1)
                        {
                            continue;

                        }
                        int[] pathCopy = Arrays.copyOf(current, current.length + 1);
                        pathCopy[pathCopy.length - 1] = neighbor;
                        stack.offer(pathCopy);
                        steps[neighbor] = steps[lastValue] + 1;
                    }

                }
            }
        }
        return map;
    }


}
