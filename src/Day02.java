import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * --- Day 2: Red-Nosed Reports ---
 * See <a href='https://adventofcode.com/2024/day/2'>Advent of Code: Day 2</a> for problem description
 */
public class Day02
{

    public static final int RUN_TYPE_PARAMETER = 0;

    public static void main(String[] args)
    {
        System.out.println("Safe Reports: " + part1(args[RUN_TYPE_PARAMETER]));
        System.out.println("Safe with Damping: " + part2(args[RUN_TYPE_PARAMETER]));
    }

    /**
     * Part 1 of <a href='https://adventofcode.com/2024/day/2'>Advent of Code: Day 2</a>.
     *
     * @param runType values of "example" or "puzzle" represents the resource subdirectory for the data
     * @return total distance between lists
     */
    private static int part1(String runType)
    {
        int safe = 0;

        try (InputStream dataFile = Day02.class.getResourceAsStream(runType + "/day02.txt"))
        {
            if (dataFile == null) {throw new IOException("File not found: " + runType + "/day02.txt");}
            Scanner data = new Scanner(dataFile);
            while (data.hasNext())
            {
                System.out.println("Report");
                String report = data.nextLine();
                String[] levelsStrings = report.split("\\s+");
                int[] levels = new int[levelsStrings.length];
                for (int i = 0; i < levelsStrings.length; i++) {
                    levels[i] = Integer.parseInt(levelsStrings[i]);
                }

                if (isReportSafe(levels)) safe += 1;
                System.out.println();
            }
        } catch (IOException e) {throw new RuntimeException(e);}
        return safe;
    }

    private static boolean isReportSafe(int[] levels)
    {
        boolean safeReport = true;
        int direction = (levels[1] > levels[0]) ? 1 : -1;

        for (int i = 1; i < levels.length; i++) {
            if (isUnsafe(levels[i-1], levels[i], direction)) {
                safeReport = false;
                System.out.print("Unsafe");
                break;
            }
        }

        return safeReport;
    }

    /**
     *  Part 2 of <a href='https://adventofcode.com/2024/day/2'>Advent of Code: Day 2</a>.
     *
     * @param runType values of "example" or "puzzle" represents the resource subdirectory for the data
     * @return similarity score
     */
    private static int part2(String runType)
    {
       return 0;
    }

    private static boolean isUnsafe(int firstValue, int secondValue, int direction)
    {
        int difference = secondValue - firstValue;
        System.out.print(difference + " ");

        if (difference == 0) return true;
        int absDifference = Math.abs(difference);

        return (direction != (difference / absDifference) || absDifference < 1 || absDifference > 3);
    }

}
