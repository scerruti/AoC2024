import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;

public class Day17
{
    private static long A, B, C;
    private static int IP = 0;
    private static final ArrayList<Byte> outputBuffer = new ArrayList<>();
    private static List<Byte> program;
    private static int opcode;


    public static void main(String[] args)
    {
        Runnable[] operations = new Runnable[]{
                Day17::adv,
                Day17::bxl,
                Day17::bst,
                Day17::jnz,
                Day17::bxc,
                Day17::out,
                Day17::bdv,
                Day17::cdv};
        readProgram();

        part1(operations);
        part2(operations);
    }

    private static void part1(Runnable[] operations)
    {
        for (IP = 0; IP < program.size(); IP += 2)
        {
            System.out.printf("%d: %d %d ", IP, program.get(IP), program.get(IP + 1));
            System.out.printf("%d %d %d%n", A, B, C);
            opcode = program.get(IP + 1);
            operations[program.get(IP)].run();
        }

        System.out.println(outputBuffer.stream().map(String::valueOf).collect(Collectors.joining(",")));
    }

    private static void part2(Runnable[] operations)
    {
        ArrayDeque<Long> possibilities = new ArrayDeque<>(List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L));
//        ArrayList<Long> answers = new ArrayList<>();

        while (!possibilities.isEmpty())
        {
            long aStored = A = possibilities.poll();
            outputBuffer.clear();
            IP = 0;


            while (IP < program.size() && outputBuffer.size() < program.size())
            {
                //                System.out.printf("%d: %d %d ", IP, program.get(IP), program.get(IP + 1));
                //                System.out.printf("%d %d %d%n", A, B, C);
                opcode = program.get(IP + 1);
                operations[program.get(IP)].run();
                IP += 2;
            }

            int n = outputBuffer.size();
            if (n <= program.size() && program.subList(program.size() - n, program.size()).equals(outputBuffer)) {
                if (n == program.size()) {
//                    System.out.printf("%s%n%s%n",
//                            outputBuffer.stream().map(String::valueOf).collect(Collectors.joining(",")),
//                            outputBuffer.stream().map(String::valueOf).collect(Collectors.joining(",")));
                    System.out.println("Answer: " +aStored);
                    return;
//                    answers.add(aStored);
                }

                // Originally did push by mistake and resolved it by finding the minimum of all solutions.
//                for (int i = 0; i < 8; i++) possibilities.push((aStored << 3) + i);
                for (int i = 0; i < 8; i++) possibilities.offer((aStored << 3) + i);
            }
        }

//        System.out.println(answers.stream().min(Long::compareTo).orElse(0L));
    }

    /**
     * Generated from Day 13 written by copilot
     */
    private static void readProgram()
    {
        String programString;

        InputStream stream = Day15.class.getResourceAsStream("puzzle/day17.txt");
        if (stream == null)
        {
            System.err.println("File not found");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            Scanner input = new Scanner(reader);
            input.next();
            input.next();
            A = input.nextInt();
            input.nextLine();
            input.next();
            input.next();
            B = input.nextInt();
            input.nextLine();
            input.next();
            input.next();
            C = input.nextInt();
            input.nextLine();
            input.next();
            programString = input.nextLine().trim();

        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        program = Arrays.stream(programString.split(",")).map(Byte::parseByte).toList();
    }
    

    /**
     * The value of a combo operand can be found as follows:
     * <p>
     * Combo operands 0 through 3 represent literal values 0 through 3.
     * Combo operand 4 represents the value of register A.
     * Combo operand 5 represents the value of register B.
     * Combo operand 6 represents the value of register C.
     * Combo operand 7 is reserved and will not appear in valid programs.
     *
     * @return combo value
     */
    public static long combo()
    {
        return switch (opcode)
        {
            case 0, 1, 2, 3 -> opcode;
            case 4 -> A;
            case 5 -> B;
            case 6 -> C;
            default -> throw new RuntimeException("Invalid opcode");
        };
    }

    /**
     * The adv instruction (opcode 0) performs division. The numerator is the value in the A register.
     * The denominator is found by raising 2 to the power of the instruction's combo operand.
     * (So, an operand of 2 would divide A by 4 (2^2); an operand of 5 would divide A by 2^B.)
     * The result of the division operation is truncated to an integer and then written to the A register.
     */
    private static void adv()
    {
        A = A >> combo();
    }

    /**
     * The bxl instruction (opcode 1) calculates the bitwise XOR of register B and the instruction's
     * literal operand, then stores the result in register B.
     */
    private static void bxl()
    {
        B ^= opcode;
    }

    /**
     * The bst instruction (opcode 2) calculates the value of its combo operand modulo 8
     * (thereby keeping only its lowest 3 bits), then writes that value to the B register.
     */
    private static void bst()
    {
        B = combo() & 0x7;
    }

    /**
     * The jnz instruction (opcode 3) does nothing if the A register is 0.
     * However, if the A register is not zero, it jumps by setting the instruction pointer
     * to the value of its literal operand; if this instruction jumps, the instruction pointer
     * is not increased by 2 after this instruction.
     */
    private static void jnz()
    {
        if (A != 0)
        {
            IP = opcode - 2;
        }
    }

    /**
     * The bxc instruction (opcode 4) calculates the bitwise XOR of register B and register C,
     * then stores the result in register B. (For legacy reasons, this instruction reads an
     * operand but ignores it.)
     */
    private static void bxc()
    {
        B ^= C;
    }

    /**
     * The out instruction (opcode 5) calculates the value of its combo operand modulo 8,
     * then outputs that value. (If a program outputs multiple values, they are separated by commas.)
     */
    private static void out()
    {
        outputBuffer.add((byte) (combo() & 0x7));
    }

    /**
     * The bdv instruction (opcode 6) works exactly like the adv instruction except that the result
     * is stored in the B register. (The numerator is still read from the A register.)
     */
    private static void bdv()
    {
        B = A >> combo();
    }

    /**
     * The bdv instruction (opcode 7) works exactly like the adv instruction except that the result
     * is stored in the B register. (The numerator is still read from the A register.)
     */
    private static void cdv()
    {
        C = A >> combo();
    }

}
