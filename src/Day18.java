import java.util.*;

public class Day18
{
    private static final int SIDE_LENGTH = 71;
    private static final String FILENAME = "puzzle/day18.txt";
    private static final int PART1_LENGTH = 1024;
    private static final int[][] map = new int[SIDE_LENGTH][SIDE_LENGTH];
    private static final ArrayList<int[]> memoryError = new ArrayList<>();
    
    public static void main(String[] args)
    {
        readMemory();
        int t = 0;
        initializeMap();
        solveFrom(new int[] {0, 0});
        int part1 = -1;

        System.out.println("Time: " + t + " Map Length: " + map[SIDE_LENGTH - 1][SIDE_LENGTH - 1]);
        printMap();
        while (t < memoryError.size() ) {
            dropByte(memoryError.get(t));
            if (t == PART1_LENGTH - 1) part1 =  map[SIDE_LENGTH - 1][SIDE_LENGTH - 1];
            if (map[SIDE_LENGTH-1][SIDE_LENGTH-1] == Integer.MAX_VALUE) break;
            t += 1;
            System.out.println("Time: " + t + " Map Length: " + map[SIDE_LENGTH - 1][SIDE_LENGTH - 1]);
//            printMap();
        }

        System.out.println("Part One: " + part1);
        System.out.println("Part Two: " + memoryError.get(t)[0] + "," + memoryError.get(t)[1]);
    }

    private static void printMap()
    {
        for (int y = 0; y < SIDE_LENGTH; y++)
        {
            for (int x = 0; x < SIDE_LENGTH; x++) {
                System.out.printf("%2s ", map[x][y] == -1 ? "#" : map[x][y] == -2 ? "@" : Integer.toString(map[x][y]));
            }
            System.out.println();
        }
        System.out.println();
    }

    private static void dropByte(int[] badByte)
    {
        map[badByte[0]][badByte[1]] = -1;

        for (int y = 0; y < SIDE_LENGTH; y++) {
            for (int x = 0; x < SIDE_LENGTH; x++) {
                if (map[x][y] > 0) {
                    map[x][y] = Integer.MAX_VALUE;
                }
            }
        }
        solveFrom(new int[] {0,0});
    }

    private static void initializeMap()
    {

        for (int i = 0; i < SIDE_LENGTH; i++)
        {
            for (int j = 0; j < SIDE_LENGTH; j++)
            {
                map[i][j] = Integer.MAX_VALUE;
            }
        }
        map[0][0] = 0;
    }

    private static void solveFrom(int[] cell) {
        ArrayDeque<int[]> stack = new ArrayDeque<>();
        stack.offer(cell);

        while (!stack.isEmpty()) {
            int[] current = stack.pop();
            int step = map[current[0]][current[1]] + 1;

            for (int[] neighbor : neighborFrom(current)) {
                if (map[neighbor[0]][neighbor[1]] > step) {
                    map[neighbor[0]][neighbor[1]] = step;
                    stack.offer(neighbor);
                }
            }
        }
    }

    private static int[][] neighborFrom(int[] current)
    {
        ArrayList<int[]> neighbors = new ArrayList<>();

        for (int[] offset : new int[][]{{-1, 0}, {0, 1}, {1, 0}, {0, -1}}) {
            int x = current[0] + offset[0];
            int y = current[1] + offset[1];

            if (0 <= x && x < SIDE_LENGTH && 0 <= y && y < SIDE_LENGTH && map[x][y] > 0) {
                neighbors.add(new int[] {x, y});
            }
        }

        return neighbors.toArray(new int[][]{});
    }

    private static void readMemory()
    {
        try (Scanner scanner = new Scanner(Objects.requireNonNull(Day18.class.getResourceAsStream(FILENAME)))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] numbers = line.trim().split(","); // Split by whitespace
                int[] row = new int[numbers.length];

                for (int i = 0; i < numbers.length; i++) {
                    row[i] = Integer.parseInt(numbers[i]);
                }

                memoryError.add(row);
            }
        }
    }
}
