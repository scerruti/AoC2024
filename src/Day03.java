import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * --- Day 3: Mull It Over ---
 * See <a href='https://adventofcode.com/2024/day/3'>Advent of Code: Day 3</a> for problem description
 */
public class Day03
{

    public static final int RUN_TYPE_PARAMETER = 0;
    public static final String DO_CONDITIONAL = "do()";
    public static final String DONT_CONDITIONAL = "don't()";

    public static void main(String[] args)
    {
        System.out.println("Result: " + part1(args[RUN_TYPE_PARAMETER]));
        System.out.println("Result with conditionals: " + part2(args[RUN_TYPE_PARAMETER]));
    }

    /**
     * Part 1 of <a href='https://adventofcode.com/2024/day/2'>Advent of Code: Day 2</a>.
     *
     * @param runType values of "example" or "puzzle" represents the resource subdirectory for the data
     * @return total distance between lists
     */
    private static long part1(String runType)
    {
        String memory = getMemory(runType);

        ArrayList<Integer> products = getValidProducts(memory);

        long sum = 0;
        for (int product : products) {
            sum += product;
        }

        return sum;
    }

    private static ArrayList<Integer> getValidProducts(String memory) {
        int pos = 0;
        ArrayList<Integer> products = new ArrayList<>();

        // valid instructions start with "mul("
        while ((pos = memory.indexOf("mul(", pos)) != -1)
        {
            pos = getProduct(memory, pos, products);
        }

        return products;
    }

    private static int getProduct(String memory, int pos, ArrayList<Integer> products)
    {
        int firstNumber = 0;
        int secondNumber = 0;

        pos += "mul(".length();
        boolean valid = false;
        // instructions next have an integer
        while (Character.isDigit(memory.charAt(pos)))
        {
            // valid numbers have at least 1 digit
            valid = true;

            firstNumber = firstNumber * 10 + memory.charAt(pos) - 0x30;
            pos += 1;
        }

        // next comes a ","
        if (!valid || memory.charAt(pos) != ',') return pos;
        pos += 1;

        // then another integer
        valid = false;
        while (Character.isDigit(memory.charAt(pos)))
        {
            valid = true;
            secondNumber = secondNumber * 10 + memory.charAt(pos) - 0x30;
            pos += 1;
        }

        // finally a ")"
        if (!valid || memory.charAt(pos) != ')') return pos;
        pos += 1;

        products.add(firstNumber * secondNumber);
        return pos;
    }

    private static String getMemory(String runType)
    {
        try (InputStream dataFile = Day02.class.getResourceAsStream(runType + "/day03.txt")) {
            Scanner scanner = new Scanner(dataFile);
            String memory = "";
            while (scanner.hasNextLine())
            {
                memory += scanner.nextLine();
            }
            return memory;
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     *  Part 2 of <a href='https://adventofcode.com/2024/day/2'>Advent of Code: Day 2</a>.
     *
     * @param runType values of "example" or "puzzle" represents the resource subdirectory for the data
     * @return similarity score
     */
    private static long part2(String runType)
    {
        String memory = getMemory(runType);

        ArrayList<Integer> products = getValidProductsWithConditional(memory);

        long sum = 0;
        for (int product : products) {
            sum += product;
        }

        return sum;
    }

    private static ArrayList<Integer> getValidProductsWithConditional(String memory) {
        int pos = 0;
        ArrayList<Integer> products = new ArrayList<>();
        String conditional = DONT_CONDITIONAL;

        // valid instructions start with "mul("
        while (true)
        {
            int multPosition = memory.indexOf("mul(", pos);
            int conditionalPosition = memory.indexOf(conditional, pos);

            if (multPosition == -1 && conditionalPosition == -1) break;

            if (conditionalPosition == -1 || multPosition < conditionalPosition)
            {
//                System.out.println(memory.substring(multPosition, multPosition+7));
                if (conditional.equals(DONT_CONDITIONAL))
                {
                    pos = getProduct(memory, multPosition, products);
                } else {
                    pos = multPosition + 1;
                }
            } else {
//                System.out.println(memory.substring(conditionalPosition, conditionalPosition+7));
                if (conditional.equals(DO_CONDITIONAL)) {
                    conditional = DONT_CONDITIONAL;
                    pos += DO_CONDITIONAL.length();
                } else {
                    conditional = DO_CONDITIONAL;
                    pos += DONT_CONDITIONAL.length();
                }
            }
        }

        return products;
    }
}
