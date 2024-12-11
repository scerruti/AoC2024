import java.math.BigInteger;
import java.util.*;

public class Day11
{
    private static final HashMap<Long, HashMap<Integer, BigInteger>> stoneMemory = new HashMap<>();
    // Example Data:
    private static final ArrayList<Long> stones = new ArrayList<>(List.of(new Long[]{125L, 17L}));


    public static void main(String[] args)
    {
        System.out.println(part1());
        System.out.println(part2());
    }

    public static BigInteger part1()
    {
        return blink(25);
    }

    public static BigInteger part2()
    {
        return blink(75);
    }

    private static BigInteger blink(int blinks)
    {
        BigInteger sum = BigInteger.ZERO;
        for (Long stone : stones)
        {
            sum = sum.add(process(stone, blinks));
        }

        return sum;
    }


    /**
     * If the stone is engraved with the number 0, it is replaced by a stone engraved with the number 1.
     * If the stone is engraved with a number that has an even number of digits, it is replaced by two stones. The left half of the digits are engraved on the new left stone, and the right half of the digits are engraved on the new right stone. (The new numbers don't keep extra leading zeroes: 1000 would become stones 10 and 0.)
     * If none of the other rules apply, the stone is replaced by a new stone; the old stone's number multiplied by 2024 is engraved on the new stone.
     */
    private static BigInteger process(Long stone, int blinks) {
        if (!stoneMemory.containsKey(stone)) {
            stoneMemory.put(stone, new HashMap<>());
        }

        if (blinks == 0) {
            return BigInteger.ONE;
        }

        if (stoneMemory.containsKey(stone) && stoneMemory.get(stone).containsKey(blinks)) {
            return stoneMemory.get(stone).get(blinks);
        }

        BigInteger result;
        if (stone == 0) {
            result = process(1L, blinks - 1);
        } else if (Long.toString(stone).length() % 2 == 0) {
            int base = (int) Math.pow(10, stone.toString().length() / 2.0);
            result = process(stone / base, blinks - 1).add(process(stone % base, blinks - 1));
        } else {
            result = process(stone * 2024, blinks - 1);
        }

        stoneMemory.get(stone).put(blinks, result);
        return result;
    }
}
