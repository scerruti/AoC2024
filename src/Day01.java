import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * --- Day 1: Historian Hysteria ---
 * See <a href='https://adventofcode.com/2024/day/1'>Advent of Code: Day 1</a> for problem description
 */
public class Day01
{

    public static final int RUN_TYPE_PARAMETER = 0;

    public static void main(String[] args)
    {
        System.out.println("Total Distance: " + part1(args[RUN_TYPE_PARAMETER]));
        System.out.println("Similarity Score: " + part2(args[RUN_TYPE_PARAMETER]));
    }

    /**
     * Part 1 of <a href='https://adventofcode.com/2024/day/1'>Advent of Code: Day 1</a>.
     *
     * @param runType values of "example" or "puzzle" represents the resource subdirectory for the data
     * @return total distance between lists
     */
    private static int part1(String runType)
    {
        int result = 0;
        ArrayList<Integer> leftLocationList = new ArrayList<>();
        ArrayList<Integer> rightLocationList = new ArrayList<>();

        readData(runType, leftLocationList, rightLocationList, null);

        leftLocationList.sort(Integer::compareTo);
        rightLocationList.sort(Integer::compareTo);

        for (int i = 0; i < leftLocationList.size() && i < rightLocationList.size(); i++)
        {
            int distance = Math.abs(rightLocationList.get(i) - leftLocationList.get(i));
            result += distance;
        }

        return result;
    }

    /**
     *  Part 2 of <a href='https://adventofcode.com/2024/day/1'>Advent of Code: Day 1</a>.
     *
     * @param runType values of "example" or "puzzle" represents the resource subdirectory for the data
     * @return similarity score
     */
    private static int part2(String runType)
    {
        int result = 0;
        ArrayList<Integer> leftLocationList = new ArrayList<>();
        Map<Integer, Integer> rightLocationFrequencyMap = new HashMap<>();

        readData(runType, leftLocationList, null, rightLocationFrequencyMap);

        for (Integer locationID : leftLocationList)
        {
            if (rightLocationFrequencyMap.containsKey(locationID))
            {
                int similarity = locationID * rightLocationFrequencyMap.get(locationID);
                result += similarity;
            }
        }

        return result;
    }

    private static void readData(String runType, List<Integer> leftLocationList, List<Integer> rightLocationList, Map<Integer, Integer> rightLocationFrequencyMap)
    {
        try (InputStream dataFile = Day01.class.getResourceAsStream(runType + "/day01.txt"))
        {
            if (dataFile == null) {throw new IOException("File not found: " + runType + "/day01.txt");}
            Scanner data = new Scanner(dataFile);
            while (data.hasNext())
            {
                int leftLocationID = data.nextInt();
                int rightLocationID = data.nextInt();
                leftLocationList.add(leftLocationID);
                if (rightLocationList != null) {rightLocationList.add(rightLocationID);}
                if (rightLocationFrequencyMap != null) {rightLocationFrequencyMap.merge(rightLocationID, 1, Integer::sum);}
            }
        } catch (IOException e) {throw new RuntimeException(e);}
    }
}
