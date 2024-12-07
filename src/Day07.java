import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Day07
{
    public static final int RUN_TYPE_PARAMETER = 0;
    private static ArrayList<String> lines;

    public static void main(String[] args)
    {
        processInput(args[RUN_TYPE_PARAMETER]);
        System.out.println("Total Calibration Result (+, *): " + part1());
        System.out.println("Total Calibration Result (+, *, ||): " + part2());
    }

    private static void processInput(String runType)
    {
        lines = new ArrayList<>();
        try (InputStream dataFile = Day02.class.getResourceAsStream(runType + "/day07.txt"))
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
    }

    public static boolean compute(Long answer, List<Integer> operands, boolean concatenate)
    {
        ArrayList<Integer> copy = new ArrayList<>(operands);
        Integer first = copy.removeFirst();

        if (compute(answer, first.longValue(), copy, concatenate)) return true;

        if (concatenate && !copy.isEmpty()) {
            Integer second = copy.removeFirst();

            long term = Long.parseLong(Integer.toString(first) + second);
            return compute(answer, term, copy, true);
        }
        return false;

    }

    public static boolean compute(Long answer, Long current, List<Integer> operands, boolean concatenate) {
        if (current > answer) {
            return false;
        } else if (operands.isEmpty())
        {
            return (answer.equals(current));
        } else {
            ArrayList<Integer> copy = new ArrayList<>(operands);
            Integer first = copy.removeFirst();
            Long term = Long.parseLong(Long.toString(current) + first);

            return compute(answer, current + first, copy, concatenate) ||
                    compute(answer, current * first, copy, concatenate) ||
                    concatenate && compute(answer, term, copy, true);
        }
    }

    public static long part1() {
        long sum = 0;

        for (String line : lines)
        {
            String[] parts = line.split(":\\s");
            List<Integer> operands = Arrays.stream(parts[1].split("\\s")).map(Integer::parseInt).toList();
            long answer = Long.parseLong(parts[0]);

            if (compute(answer, operands, false)) sum += answer;
        }

        return sum;
    }

    public static long part2() {
        long sum = 0;

        for (String line : lines)
        {
            String[] parts = line.split(":\\s");
            List<Integer> operands = Arrays.stream(parts[1].split("\\s")).map(Integer::parseInt).toList();
            long answer = Long.parseLong(parts[0]);

            if (compute(answer, operands, true)) sum += answer;
        }

        return sum;
    }
}
