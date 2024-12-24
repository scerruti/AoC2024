import java.util.*;

public class KeypadPathsGenerator
{

    private static final int[] ROWS = {-1, 1, 0, 0};
    private static final int[] COLS = {0, 0, -1, 1};
    private static final String[] DIRECTIONS = {"^", "v", "<", ">"};
    private static final Map<Character, Map<Character, List<String>>> numericPaths;
    private static final Map<Character, Map<Character, List<String>>> directionalPaths;
    private static final char[][] numericKeypad = new char[][]{
            {'7', '8', '9'},
            {'4', '5', '6'},
            {'1', '2', '3'},
            {' ', '0', 'A'} // Blank space at (3, 0)
    };
    private static final char[][] directionalKeypad = new char[][]{
            {' ', '^', 'A'},
            {'<', 'v', '>'}
    };

    public static Map<Character, Map<Character, String>> numericDirectionalPaths;

    static
    {
        numericPaths = generateAllPaths(numericKeypad);
        directionalPaths = generateAllPaths(directionalKeypad);
    }

    public static void main(String[] args)
    {

        // Example: print paths from 9 to 1 for keypad1
        char start = '9';
        char end = '1';
        System.out.printf("Paths from %c to %c on keypad1: %s%n", start, end, numericPaths.get(start).get(end));

        // Example: print paths from ^ to > for keypad2
        char start2 = '^';
        char end2 = '>';
        System.out.printf("Paths from %c to %c on keypad2: %s%n", start2, end2, directionalPaths.get(start2).get(end2));

        generateNumericDirectionalPaths();
        for (int i = 0; i < 3; i++)
        {
            System.out.println(directionalPathsFor("<A^A>^^AvvvA", i));
        }
    }

    public static List<String> getNumericPaths(char start, char end)
    {
        return numericPaths.get(start).get(end);
    }

    public static List<String> getDirectionalPaths(char start, char end)
    {
        return directionalPaths.get(start).get(end);
    }

    public static void generateNumericDirectionalPaths() {
        numericDirectionalPaths = new HashMap<>();
        for (char from : "A0123456789".toCharArray()) {
            HashMap<Character, String> map = new HashMap<>();
            numericDirectionalPaths.put(from, map);
            for (char to : "A0123456789".toCharArray()) {
                List<String> paths = getNumericPaths(from, to);
                List<String> finalPaths = new ArrayList<>();
                for (String path : paths) {
                    map.put(to, directionalPathsFor(path + "A", 1));
                }
            }
        }
    }

    private static String directionalPathsFor(String code, int depth)
    {
        if (depth == 0) {
            return code;
        }

        ArrayDeque<String> stack = new ArrayDeque<>();
        stack.push("");

        char prev = 'A';
        for (int i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            ArrayDeque<String> temp = new ArrayDeque<>();
            while (!stack.isEmpty()) {
                String current = stack.pop();
                for (String path : KeypadPathsGenerator.getDirectionalPaths(prev, c)) {
                    temp.offer(current + path + "A");
                }
            }
            prev = c;
            stack = temp;
        }

        int shortestLength = Integer.MAX_VALUE;
        String result = null;
        for (String expandedCode: stack) {
            String current = directionalPathsFor(expandedCode, depth - 1);
            if (current.length() < shortestLength) {
                shortestLength = current.length();
                result = current;
            }
        }

        return result;
    }

    public static Map<Character, Map<Character, List<String>>> generateAllPaths(char[][] keypad)
    {
        Map<Character, Map<Character, List<String>>> paths = new HashMap<>();

        for (int i = 0; i < keypad.length; i++)
        {
            for (int j = 0; j < keypad[i].length; j++)
            {
                if (keypad[i][j] != ' ')
                {
                    char start = keypad[i][j];
                    paths.put(start, new HashMap<>());

                    for (int k = 0; k < keypad.length; k++)
                    {
                        for (int l = 0; l < keypad[k].length; l++)
                        {
                            if (keypad[k][l] != ' ' && !(i == k && j == l))
                            {
                                char end = keypad[k][l];
                                List<String> pathList = findShortestPaths(i, j, k, l, keypad);
                                paths.get(start).put(end, pathList);
                            }
                        }
                    }

                    // Same button transition
                    paths.get(start).put(start, List.of(""));
                }
            }
        }

        return paths;
    }

    private static List<String> findShortestPaths(int startX, int startY, int endX, int endY, char[][] keypad)
    {
        List<String> paths = new ArrayList<>();
        Queue<Path> queue = new LinkedList<>();
        queue.add(new Path(startX, startY, new StringBuilder()));

        // Used to store visited positions with their path lengths
        Map<String, Integer> visited = new HashMap<>();
        visited.put(startX + "," + startY, 0);

        boolean found = false;
        int targetLength = Integer.MAX_VALUE;

        while (!queue.isEmpty())
        {
            Path current = queue.poll();

            if (found && current.path.length() > targetLength)
            {
                continue;
            }

            if (current.x == endX && current.y == endY)
            {
                if (!found)
                {
                    found = true;
                    targetLength = current.path.length();
                }
                paths.add(current.path.toString());
                continue;
            }

            for (int i = 0; i < ROWS.length; i++)
            {
                int newX = current.x + ROWS[i];
                int newY = current.y + COLS[i];
                String newState = newX + "," + newY;

                if (isValidMove(newX, newY, keypad) && (!visited.containsKey(newState) || visited.get(newState) >= current.path.length() + 1))
                {
                    visited.put(newState, current.path.length() + 1);
                    StringBuilder newPath = new StringBuilder(current.path);
                    newPath.append(DIRECTIONS[i]);
                    queue.add(new Path(newX, newY, newPath));
                }
            }
        }

        return paths;
    }

    private static boolean isValidMove(int x, int y, char[][] keypad)
    {
        return x >= 0 && y >= 0 && x < keypad.length && y < keypad[0].length && keypad[x][y] != ' ';
    }

    private static class Path
    {
        int x, y;
        StringBuilder path;

        Path(int x, int y, StringBuilder path)
        {
            this.x = x;
            this.y = y;
            this.path = path;
        }
    }
}
