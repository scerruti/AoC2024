import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class Day12
{
    private static final int RUN_TYPE_PARAMETER = 0;
    private static int width = -1;
    private static int height = 0;
    private static byte[] garden;
    private static int currentArea;
    private static int currentCorners;

    public static void main(String[] args)
    {
        processInput(args[RUN_TYPE_PARAMETER]);
        System.out.println(part1());
        System.out.println(part2());
    }

    private static void processInput(String runType)
    {
        garden = new byte[0];

        try (InputStream dataFile = Day02.class.getResourceAsStream(runType + "/day12.txt"))
        {
            if (dataFile != null)
                garden = dataFile.readAllBytes();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < garden.length; i++)
        {
            if (garden[i] == '\n')
            {
                if (width == -1) width = i;
                height += 1;
            }
        }
        height++;
        String content = new String(garden, StandardCharsets.UTF_8).replaceAll("\\r\\n|\\r|\\n", "");
        garden = content.getBytes(StandardCharsets.UTF_8);
    }

    private static int part1()
    {
        int cost1 = 0;
        int cost2 = 0;

        for (int i = 0; i < garden.length; i++)
        {
            if (garden[i] > 0)
            {
                currentArea = 0;
                currentCorners = 0;
                int regionCost = findCost(garden[i], i);
                System.out.printf("%s %d %d %d%n", (char) (garden[i] * -1), regionCost, currentArea, currentCorners);
                cost1 += regionCost * currentArea;
                cost2 += currentArea * currentCorners;
            }
        }
        System.out.println(cost2);
        return cost1;
    }

    private static int part2()
    {
        return 0;
    }

    private static int findCost(byte plotType, int plot)
    {
        int cost = 0;

        if (plot == -1) return 1;
        if (garden[plot] == -plotType) return 0;
        if (garden[plot] != plotType) return 1;

        garden[plot] *= -1;

        currentArea += 1;

        int north = findCost(plotType, north(plot));
        int east = findCost(plotType, east(plot));
        int south = findCost(plotType, south(plot));
        int west = findCost(plotType, west(plot));

        cost += north + south + east + west;

        boolean[] same = getMatchingNeighbors(plotType, plot);

        if (same[0] && same[2] && !same[1]) currentCorners += 1;
        if (same[2] && same[4] && !same[3]) currentCorners += 1;
        if (same[4] && same[6] && !same[5]) currentCorners += 1;
        if (same[6] && same[0] && !same[7]) currentCorners += 1;

        if (!same[0] && !same[2]) currentCorners += 1;
        if (!same[2] && !same[4]) currentCorners += 1;
        if (!same[4] && !same[6]) currentCorners += 1;
        if (!same[6] && !same[0]) currentCorners += 1;


        return cost;
    }

    private static boolean[] getMatchingNeighbors(byte plotType, int plot)
    {
        int[] neighbors = new int[]{neighbor(plot, -1, 0),
                neighbor(plot, -1, 1),
                neighbor(plot, 0, 1),
                neighbor(plot, 1, 1),
                neighbor(plot, 1, 0),
                neighbor(plot, 1, -1),
                neighbor(plot, 0, -1),
                neighbor(plot, -1, -1)};

        return new boolean[]{
                neighbors[0] != -1 && Math.abs(garden[neighbors[0]]) == plotType,
                neighbors[1] != -1 && Math.abs(garden[neighbors[1]]) == plotType,
                neighbors[2] != -1 && Math.abs(garden[neighbors[2]]) == plotType,
                neighbors[3] != -1 && Math.abs(garden[neighbors[3]]) == plotType,
                neighbors[4] != -1 && Math.abs(garden[neighbors[4]]) == plotType,
                neighbors[5] != -1 && Math.abs(garden[neighbors[5]]) == plotType,
                neighbors[6] != -1 && Math.abs(garden[neighbors[6]]) == plotType,
                neighbors[7] != -1 && Math.abs(garden[neighbors[7]]) == plotType};
    }

    private static int neighbor(int plot, int dr, int dc)
    {
        int row = plot / width + dr;
        int col = plot % width + dc;

        return 0 <= row && row < height && 0 <= col && col < width ? row * width + col : -1;
    }

    private static int north(int plot)
    {
        return neighbor(plot, -1, 0);
    }

    private static int east(int plot)
    {
        return neighbor(plot, 0, 1);
    }

    private static int south(int plot)
    {
        return neighbor(plot, 1, 0);
    }

    private static int west(int plot)
    {
        return neighbor(plot, 0, -1);
    }
}
