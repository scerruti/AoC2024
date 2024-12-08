import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Day08
{
    public static final int RUN_TYPE_PARAMETER = 0;
    private static final HashMap<Character, ArrayList<Integer>> antennas = new HashMap<>();
    private static int width;
    private static int rows;
    private static final HashSet<Integer> antinodes = new HashSet<>();

    public static void main(String[] args)
    {
        processInput(args[RUN_TYPE_PARAMETER]);
        System.out.println("Single antinodes: " + part1());
        System.out.println("All antinodes: " + part2());
    }

    private static void processInput(String runType)
    {
        ArrayList<String> lines = new ArrayList<>();
        try (InputStream dataFile = Day02.class.getResourceAsStream(runType + "/day08.txt"))
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
        rows = lines.size();
        for (int row = 0; row < lines.size(); row++) {
            for (int col = 0; col < width; col++) {
                char c = lines.get(row).charAt(col);
                if (Character.isLetterOrDigit(c)) {
                    antennas.computeIfAbsent(c, k -> new ArrayList<>()).add(row* width + col);
                }
            }
        }
    }

    public static long part1()
    {
        return findantinodes(true);
    }

    public static long findantinodes(boolean single) {
        for (Character c : antennas.keySet()) {
            for (int i = 0; i < antennas.get(c).size(); i++)
            {
                for (int j = i+1; j < antennas.get(c).size(); j++)
                {
                    int p0 = antennas.get(c).get(i);
                    int p0x = p0 % width;
                    int p0y = p0 / width;
//                    System.out.printf("p0 %d (%d, %d)%n", p0, p0y, p0x);

                    int p1 = antennas.get(c).get(j);
                    int p1x = p1 % width;
                    int p1y = p1 / width;
//                    System.out.printf("p1 %d (%d, %d)%n", p1, p1y, p1x);

                    int dy = p1y - p0y;
                    int dx = p1x - p0x;

                    // Compute Possible Antinodes
                    if (!single) antinodes.add(p1);
                    int a1x = p1x + dx;
                    int a1y = p1y + dy;
                    while (0 <= a1x && a1x < width && 0 <= a1y && a1y < rows)
                    {
//                        System.out.printf("a1 (%d, %d)%n", a1y, a1x);
                        int a1 = a1y * width + a1x;
                        antinodes.add(a1);
                        a1x += dx;
                        a1y += dy;
                        if (single) break;
                    }

                    if (!single) antinodes.add(p0);
                    int a2x = p0x - dx;
                    int a2y = p0y - dy;
                    while (0 <= a2x && a2x < width && 0 <= a2y && a2y < rows)
                    {
//                        System.out.printf("a2 (%d, %d)%n", a2y, a2x);
                        int a2 = a2y * width + a2x;
                        antinodes.add(a2);
                        a2x -= dx;
                        a2y -= dy;
                        if (single) break;
                    }
                }
            }
        }

        return antinodes.size();
    }

    public static long part2() {
        return findantinodes(false);
    }
}
