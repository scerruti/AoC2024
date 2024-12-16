import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Day15
{
    private static char[] warehouse;
    private static String moves = "";
    private static int width;
    private static int position;
    private static HashMap<Integer, Integer> columns = new HashMap<>();

    public static void main(String[] args)
    {
        readWarehouse();

        printMap();
        System.out.println("\n" + moves);

        for (char move : moves.toCharArray())
        {
            move1(move);
        }
        printMap();
        System.out.println("\n" + gpsSum());
    }

    private static void move1(char move)
    {
        int offset = 0;
        System.out.println("\n" + move);
        switch (move)
        {
            case '^':
                offset = -width;
                break;
            case 'v':
                offset = width;
                break;
            case '<':
                offset = -1;
                break;
            case '>':
                offset = 1;
                break;
            default:
                System.err.println("Invalid move: " + move);
                System.exit(1);
                break;
        }

        if (canPush(position, offset)) {
            push(position, offset);
            position += offset;
        }
//        printMap();
    }

    private static boolean canPush(int position, int offset) {
        int next = position + offset;
        switch (warehouse[next])
        {
            case '#':
                return false;
            case '.':
                return true;
            case '[':
                if (Math.abs(offset) == 1)
                {
                    return canPush(next, offset);
                } else {
                    return canPush(next, offset) && canPush(next + 1, offset);
                }
            case ']':
                if (Math.abs(offset) == 1)
                {
                    return canPush(next, offset);
                } else {
                    return canPush(next - 1, offset) && canPush(next, offset);
                }
        }
        return false;
    }

    private static boolean push(int position, int offset) {
        int next = position + offset;
        switch (warehouse[next])
        {
            case '#':
                return false;
            case '.':
                break;
            case '[':
                if (Math.abs(offset) == 1)
                {
                    push(next, offset);
                } else {
                    push(next, offset);
                    push(next + 1, offset);
                }
                break;
            case ']':
                if (Math.abs(offset) == 1)
                {
                    push(next, offset);
                } else {
                    push(next - 1, offset);
                    push(next, offset);
                }
                break;
        }
        warehouse[next] = warehouse[position];
        warehouse[position] = '.';

        return true;
    }

    private static boolean isSpace(int position, int offset)
    {
        if (Math.abs(offset) == 1) return warehouse[position + offset] == '.';

        int row = position / width;
        for (int column: columns.keySet()) {
            if (warehouse[row * width + column] == '.') {
                columns.merge(column, 1, Integer::sum);
            };
        }

        return columns.values().stream().allMatch(x -> x > 0);
    }

    private static boolean isBlocked(int position, int offset)
    {
        if (Math.abs(offset) == 1) return warehouse[position + offset] == '#';

        int row = position / width;
        for (int column: columns.keySet()) {
            if (warehouse[row * width + column] == '#') {
                if (!columns.containsKey(column) || columns.get(column) == 0) return true;
                else columns.merge(column, -1, Integer::sum);
            }
        }
        return false;
    }


    private static long gpsSum()
    {
        long sum = 0;
        for (int row = 0; row < warehouse.length / width; row++)
        {
            for (int col = 0; col < width; col++)
                sum += warehouse[row * width + col] == '[' ? gps(row, col) : 0;
        }
        return sum;
    }

    private static long gps(int row, int col)
    {
        return row * 100L + col;
    }

    private static void printMap()
    {
        for (int row = 0; row < warehouse.length / width; row++)
        {
            for (int col = 0; col < width; col++)
                System.out.print(position == row * width + col ? '@' : warehouse[row * width + col]);
            System.out.println();
        }
        System.out.println(position);

    }

    /**
     * Generated from Day 13 written by copilot
     */
    private static void readWarehouse()
    {
        StringBuilder warehouseString = new StringBuilder();
        StringBuilder movesBuilder = new StringBuilder();

        InputStream stream = Day15.class.getResourceAsStream("puzzle/day15.txt");
        if (stream == null)
        {
            System.err.println("File not found: " + "example/day15c.txt");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            String line;
            boolean finished = false;
            int row = 0;

            while ((line = reader.readLine()) != null)
                if (line.isEmpty())
                {
                    finished = true;
                } else if (!finished)
                {
                    String currentRow = line.trim();
                    width = currentRow.length()*2;
                    for (int pos = 0; pos < currentRow.length(); pos++) {
                        if (currentRow.charAt(pos) == '@') {
                            position = row * width + pos*2;
                            warehouseString.append("..");
                        } else if (currentRow.charAt(pos) == '.') {
                            warehouseString.append("..");
                        } else if (currentRow.charAt(pos) == '#') {
                            warehouseString.append("##");
                        } else if (currentRow.charAt(pos) == 'O') {
                            warehouseString.append("[]");
                        }
                    }
                    row += 1;
                } else
                {
                    movesBuilder.append(line.trim());
                }
        } catch (IOException e) {System.err.println("Error reading file: " + e.getMessage());}
        warehouse = warehouseString.toString().toCharArray();
        moves = movesBuilder.toString();
    }
}
