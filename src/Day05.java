import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * --- Day 5: Ceres Search ---
 * See <a href='https://adventofcode.com/2024/day/5'>Advent of Code: Day 5</a> for problem description
 */
public class Day05
{
    public static final int RUN_TYPE_PARAMETER = 0;
    private static HashMap<Integer, ArrayList<Integer>> pageOrder = new HashMap<>();
    public static ArrayList<Integer[]> updates = new ArrayList<Integer[]>();
    public static ArrayList<Integer[]> incorrectUpdates = new ArrayList<Integer[]>();

    public static void main(String[] args)
    {
        processInput(args[RUN_TYPE_PARAMETER]);
        System.out.println("Sum of middle page numbers of valid updates: " + part1(args[RUN_TYPE_PARAMETER]));
        System.out.println("Sum of middle page numbers of corrected updates: " + part2(args[RUN_TYPE_PARAMETER]));
    }

    private static void processInput(String runType)
    {
        try (InputStream dataFile = Day02.class.getResourceAsStream(runType + "/day05.txt")) {
            Scanner scanner = new Scanner(dataFile);
            StringBuilder memory = new StringBuilder();
            int rowLength = 0;
            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                if (line.isEmpty()) break;

                List<Integer> values = Arrays.stream(line.split("\\|")).map(Integer::parseInt).toList();
                // Check if the key exists in the map
                if (!pageOrder.containsKey(values.get(0))) {
                    // If the key doesn't exist, create a new ArrayList and add the value
                    pageOrder.put(values.get(0), new ArrayList<>());
                }

                // Add the value to the ArrayList associated with the key
                pageOrder.get(values.get(0)).add(values.get(1));
            }

            while (scanner.hasNextLine())
            {
                String line = scanner.nextLine();
                if (line.isEmpty()) break;

                updates.add((Integer[]) Arrays.stream(line.split(",")).map(Integer::parseInt).toArray(Integer[]::new));
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * Part 1 of <a href='https://adventofcode.com/2024/day/5'>Advent of Code: Day 5</a>.
     *
     * @param runType values of "example" or "puzzle" represents the resource subdirectory for the data
     * @return total distance between lists
     */
    private static long part1(String runType)
    {
        int counter = 0;

        System.out.println(pageOrder);
        System.out.println(updates);

        for (Integer[] update: updates) {
            boolean valid = isValidUpdate(update);
            if (valid) {
                System.out.println("|"+ update[update.length / 2] + "|");
                counter += update[update.length / 2];
            } else {
                incorrectUpdates.add(update);
            }
        }
        return counter;
    }

    private static boolean isValidUpdate(Integer[] update)
    {
        System.out.println(update);
        boolean valid = true;
        for (int i = 0; i < update.length - 1 && valid; i++) {
            valid = isPageInOrder(update, i, pageOrder.get(update[i]));
        }
        return valid;
    }

    private static boolean isPageInOrder(Integer[] update, int position, List<Integer> followingPages) {
        if (followingPages == null) {
            return false;
        }
        for (int i = position + 1; i < update.length; i++) {
            System.out.println("\tDoes "+ followingPages + " contain " + update[i]);
            if (followingPages.indexOf(update[i]) == -1) {
                System.out.println("No");
                return false;
            }
        }
        return true;
    }

    /**
     *  Part 2 of <a href='https://adventofcode.com/2024/day/5'>Advent of Code: Day 5</a>.
     *
     * @param runType values of "example" or "puzzle" represents the resource subdirectory for the data
     * @return similarity score
     */
    private static long part2(String runType)
    {
        long counter = 0;

        for (Integer[] update: incorrectUpdates) {
            System.out.println("Check: " + Arrays.toString(update));
            for (int i = 0; i < update.length - 1; i++)
            {
                System.out.println("Check " + i + ": " + update[i] + " of " + Arrays.toString(update));
                if (incorrectPageOrder(update, i, pageOrder.get(update[i]))) {
                    System.out.println("Incorrect");
                    i = -1;
                }
            }
            System.out.println("Final order; " + Arrays.toString(update));
                System.out.println("|"+ update[update.length / 2] + "|");
            counter += update[update.length / 2];
        }
        return counter;
    }

    private static boolean incorrectPageOrder(Integer[] update, int position, List<Integer> followingPages) {
        if (followingPages == null) {
            swap(update, position, update.length - 1);
            return true;
        }
        for (int i = position + 1; i < update.length; i++) {
            System.out.println("\tDoes "+ followingPages + " contain " + update[i]);
            if (followingPages.indexOf(update[i]) == -1) {
                System.out.printf("swap %d %d%n", update[i], update[position]);
                swap(update, i, position);
                return true;
            }
        }
        return false;
    }

    private static void swap(Integer[] update, int i, int j)
    {
        int temp = update[i];
        update[i] = update[j];
        update[j] = temp;
    }

}
