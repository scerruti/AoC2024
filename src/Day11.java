import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Day11
{
    /**
     * If the stone is engraved with the number 0, it is replaced by a stone engraved with the number 1.
     * If the stone is engraved with a number that has an even number of digits, it is replaced by two stones. The left half of the digits are engraved on the new left stone, and the right half of the digits are engraved on the new right stone. (The new numbers don't keep extra leading zeroes: 1000 would become stones 10 and 0.)
     * If none of the other rules apply, the stone is replaced by a new stone; the old stone's number multiplied by 2024 is engraved on the new stone.
     */
    public static void main(String[] args)
    {
        System.out.println(part1());
    }

    public static long part1()
    {
//        ArrayList<Stone> stones = new ArrayList<>(List.of(new Stone[]{new Stone(125L), new Stone(17L)}));
        ArrayList<Stone> stones = new ArrayList<>(List.of(
                new Stone[]{new Stone(6571L), new Stone(0L), new Stone(5851763L), new Stone(526746L), new Stone(23L), new Stone(69822L), new Stone(9L), new Stone(989L)}));

        System.out.println(Arrays.toString(stones.toArray()));
        for (int i = 1; i <= 75; i++) {
            process(stones);
            reduce(stones);
            System.out.printf("%d: %d%n", i, stones.size());
//            System.out.println(Arrays.toString(stones.toArray()));
        }
        System.out.printf("%d %s%n", stones.size(), length(stones).toString());

        return stones.size();
    }

    private static void process(ArrayList<Stone> stones)
    {
        for (int i = 0; i < stones.size(); i++)
        {
            if (stones.get(i).current == 0) {
                stones.get(i).current = 1L;
            } else if (Long.toString(stones.get(i).current).length() % 2 == 0) {
                int base = (int) Math.pow(10, stones.get(i).current.toString().length() / 2.0);
                long left = stones.get(i).current / base;
                stones.get(i).current = stones.get(i).current % base;
                stones.add(i, new Stone(left, stones.get(i).quantity));
                i += 1;
            } else {
                stones.get(i).current *= 2024;
            }
        }
    }

    private static void reduce(ArrayList<Stone> stones) {
        for (int i = 0; i < stones.size(); i++) {
            for (int j = i + 1; j < stones.size(); j++) {
                if (Objects.equals(stones.get(i).current, stones.get(j).current)) {
                    stones.get(i).quantity += stones.get(j).quantity;
                    stones.remove(j);
                    j -= 1;
                }
            }
        }
    }

    private static BigInteger length(ArrayList<Stone> stones)
    {
        BigInteger count = new BigInteger("0");
        for (Stone stone : stones)
        {
            count = count.add(BigInteger.valueOf(stone.quantity));
        }
        return count;
    }

        private static class Stone {
        Long current;
        Long quantity;

        public Stone(Long current)
        {
            this.current = current;
            this.quantity = 1L;
        }

        public Stone(long current, long quantity)
        {
            this.current = current;
            this.quantity = quantity;
        }
    }
}
