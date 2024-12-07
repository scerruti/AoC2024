import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;

public class Day06
{
    public static final int RUN_TYPE_PARAMETER = 0;
    private static final String OBSTRUCTED_CHARACTERS = "#O";
    private static final String UNVISITED_CHARACTERS = ".";
    private static final char VISITED_CHARACTER = 'X';
    public static final int ROW_INDEX = 1;
    public static final int COL_INDEX = 0;
    private static char[][] map;
    private static int width;
    private static int start;
    private static final int[][] DIRECTIONS = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
    private static final HashSet<Integer[]> obstructionCandidate = new HashSet<>();
    private static final State visits = new State();


    public static void main(String[] args)
    {
        processInput(args[RUN_TYPE_PARAMETER]);
        System.out.println("Distinct visited positions: " + part1());
        System.out.println("Possible obstruction spots: " + part2());
    }

    private static void processInput(String runType)
    {
        ArrayList<String> lines = new ArrayList<>();
        try (InputStream dataFile = Day02.class.getResourceAsStream(runType + "/day06.txt"))
        {
            Scanner scanner;
            if (dataFile != null)
            {
                scanner = new Scanner(dataFile);
            } else {
                throw new RuntimeException("Unable to find run type " + runType);
            }
            while (scanner.hasNextLine())
            {
                lines.add(scanner.nextLine());
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        width = lines.getFirst().length();
        map = new char[lines.size()][width];
        for (int i = 0; i < lines.size(); i++)
        {
            map[i] = lines.get(i).toCharArray();
            if (lines.get(i).contains("^"))
            {
                start = i * width + lines.get(i).indexOf('^');
            }
        }
    }

    private static boolean onMap(int row, int col, int[] next)
    {
        return 0 <= row + next[ROW_INDEX] && row + next[ROW_INDEX] < width &&
                0 <= col + next[COL_INDEX] && col + next[COL_INDEX] < width;
    }

    private static boolean isObstructed(int[] position) {
        return isObstructed(position[ROW_INDEX], position[COL_INDEX]);
    }

    private static boolean isObstructed(int row, int column) {
        return OBSTRUCTED_CHARACTERS.contains(String.valueOf(map[row][column]));
    }

    private static boolean isUnvisited(int[] position)
    {
        return isUnvisited(position[ROW_INDEX], position[COL_INDEX]);
    }

    private static boolean isUnvisited(int row, int column)
    {
        return UNVISITED_CHARACTERS.contains(String.valueOf(map[row][column]));
    }

    private static int[] newPosition(int row, int col, int[] direction) {
        return new int[] { col + direction[COL_INDEX], row + direction[ROW_INDEX],};
    }

    private static int col(int position)
    {
        return position % width;
    }

    private static int row(int position)
    {
        return position / width;
    }

    /**
     * Part 1 of <a href='https://adventofcode.com/2024/day/6'>Advent of Code: Day 5</a>.
     *
     * @return total distance between lists
     */
    private static long part1()
    {
        try
        {
            return runSimulation(true);
        } catch (Exception e)
        {
            /* Ignore */
        }
        return -999;
    }

    private static long runSimulation(boolean saveVisits) throws Exception
    {
        int col = col(start);
        int row = row(start);
        int moves = 1;
        int next = 0;

        State state = new State();

        int consecutiveTurns = 0;
        while (onMap(row, col, DIRECTIONS[next]))
        {
//            printMap();
            int[] pos = newPosition(row, col, DIRECTIONS[next]);
            if (saveVisits) {
                visits.addIfNew(new int[] {row, col}, next);
            }

            if (isObstructed(pos))
            {
                next = (next + 1) % DIRECTIONS.length;
                consecutiveTurns += 1;
                if (consecutiveTurns > 3) throw new Exception("Stuck turning");
            } else
            {
                row = pos[ROW_INDEX];
                col = pos[COL_INDEX];

                consecutiveTurns = 0;
                if (isUnvisited(pos))
                {
                    map[row][col] = VISITED_CHARACTER;
                    moves++;
                }
                if (!state.addIfNew(pos, next)) {
                    throw new Exception("Stuck in loop");
                }
            }
        }

        return moves;
    }

    private static void printMap()
    {
        for (char[] row : map)
        {
            for (char c : row) System.out.print(c);
            System.out.println();
        }
        System.out.println();
    }


    public static int part2()
    {
//        System.out.println("Total Candidates: " + map.length * width);
//        for (int row = 0; row < map.length; row++)
//        {
//            for (int col = 0; col < map[row].length; col++)
            for (Integer[] visit : visits.uniqueLocations())
            {

                int row = visit[0];
                int col = visit[1];
                System.out.printf("(%d,%d) ", col, row);
                if (!isObstructed(row, col))
                {
                    char temp = map[row][col];
                    try
                    {
                        map[row][col] = 'O';
                        runSimulation(false);
                    } catch (Exception e)
                    {
                        System.out.println(e);
                        obstructionCandidate.add(new Integer[] {row, col});
                    }
                    map[row][col] = temp;
                }
            }
//        }
        for (Integer[] obstruction : obstructionCandidate) {
            System.out.printf("Candidate: (%d, %d)%n", obstruction[ROW_INDEX], obstruction[COL_INDEX]);
        }
        return obstructionCandidate.size();
    }

//    private static boolean isLoop(int startPosition, int startChange)
//    {
//        int position = startPosition;
//        int change = startChange;
//        State state = new State();
//        state.addIfNew(position, change);
////        System.out.printf("%nLoop to: %d %d%n", startPosition, startChange);
//
//        int consecutiveTurns = 0;
//        while (onMap(position, change))
//        {
//            if (isObstructed(position, change))
//            {
//                change = change == -width ? 1 : change == 1 ? width : change == width ? -1 : change == -1 ? -width : -999;
//                consecutiveTurns += 1;
//                if (consecutiveTurns > 3) return true;
//            } else
//            {
//                consecutiveTurns = 0;
//                position += change;
////                System.out.printf("\t%d %d%n", position, change);
//                if (!state.addIfNew(position, change)) {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }


//    private static void buildTree() {
//        for (int row = 0; row < map.length; row++) {
//            for (int col = 0; col < width; col++) {
//                if (map[row][col] == '.') continue;
//
//
//
//                for (int i = 0; i < blocks.length; i++) {
//                    int blockCol = col + blocks[i][0];
//                    int blockRow = row + blocks[i][1];
//                    if (0 > blockCol || blockCol >= width || 0 > blockRow || blockRow >= map.length) continue;
//                    if (isRCObstructed(blockRow, blockCol)) continue;
//
//                    // loop in block direction until you hit a node or you go off the map.
//                    int[] block = blocks[(i - 1) % blocks.length];
//                    while (rcOnMap())
//                }
//            }
//        }
//    }


    private static class State
    {
        HashSet<Integer[]> pathSteps = new HashSet<>();

        public boolean addIfNew(int[] position, int next)
        {
            for (Integer[] step : pathSteps) {
                if (step[0] == position[0] && step[1] == position[1] && step[2] == next) return false;
            }

            pathSteps.add(new Integer[]{position[0], position[1], next});
            return true;
        }

        public Set<Integer[]> uniqueLocations()
        {
            HashSet<Integer[]> uniqueLocations = new HashSet<>();
            HashSet<Integer> positions = new HashSet<>();

            for (Integer[] step : pathSteps) {
                int position = step[0] * width + step[1];
                boolean newPosition = positions.contains(position);
                positions.add(position);

                if (newPosition) continue;

                uniqueLocations.add(new Integer[]{step[0], step[1]});
            }

            return uniqueLocations;
        }
    }
}
