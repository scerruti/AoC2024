import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

public class Day22
{

    public static final int PRUNE_FACTOR = 16777216;
    private static final HashMap<Integer, HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>>> ledger = new HashMap<>();
    private static Set<String> contracts = new HashSet<>();

    public static void main(String[] args)
    {
        long total = 0;
        long count = 0;

//        int[] starts = {1, 2, 3, 2024};

        int[] starts = readIntegersFromFile("puzzle/day22.txt");

        Map<String, Integer> offers = new HashMap<>();
        ArrayList<String> contracts = new ArrayList<>();

        for (int start : starts)
        {
            contracts.clear();

            int secret = start;
            int prev = secret % 10;
            int[] sequence = new int[2000];
            for (int i = 0; i < 2000; i++)
            {
                secret = calculateSecret(secret);

                int offer = secret % 10;

                sequence[i] = offer - prev;
                if (i > 2)
                {
                    String contractString = String.format("%d %d %d %d", sequence[i-3], sequence[i-2], sequence[i-1], sequence[i]);
                    if (!contracts.contains(contractString)) {
                        offers.putIfAbsent(contractString, 0);
                        offers.merge(contractString, offer, Integer::sum);
                        if (contractString.equals("1 -3 3 1")) {
                            count++;
                            System.out.println(offer);
                        }
                        contracts.add(contractString);
                    }
                }
                prev = offer;
            }

//            System.out.printf("%d: %d%n", start, secret);
            total += secret;
        }

        System.out.printf("%nPart 1: %d%n", total);
//        total = maxBananas();
        total = offers.values().stream().max(Integer::compareTo).get();
        System.out.printf("%nPart 2: %d%n", total);
    }

    public static void updateTree(int[] keys, int offer)
    {
        ledger.computeIfAbsent(keys[0], k -> new HashMap<>())
                .computeIfAbsent(keys[1], k -> new HashMap<>())
                .computeIfAbsent(keys[2], k -> new HashMap<>()).
                putIfAbsent(keys[3], 0);
        ledger.get(keys[0]).get(keys[1]).get(keys[2]).merge(keys[3], offer, Integer::sum);
//        if (keys[0] == 1 && keys[1] == -3 && keys[2] == 3 && keys[3] == 1)
//        if (keys[0] == -2 && keys[1] == 1 && keys[2] == -1 && keys[3] == 3)
//            System.out.println(ledger.get(keys[0]).get(keys[1]).get(keys[2]).get(keys[3]));
    }

    /**
     * Calculate the result of multiplying the secret number by 64.
     * Then, mix this result into the secret number.
     * Finally, prune the secret number.
     * <br>
     * Calculate the result of dividing the secret number by 32.
     * Round the result down to the nearest integer.
     * Then, mix this result into the secret number.
     * Finally, prune the secret number.
     * <br>
     * Calculate the result of multiplying the secret number by 2048. T
     * Then, mix this result into the secret number.
     * Finally, prune the secret number.
     * <br>
     *
     * @param secret secret number
     * @return next secret number in sequence
     */
    private static int calculateSecret(int secret)
    {
        int result = secret * 64;
        result = mix(secret, result);
        secret = prune(result);

        result = secret / 32;
        secret = mix(secret, result);
        secret = prune(secret);

        result = secret * 2048;
        result = mix(secret, result);
        secret = prune(result);

        return secret;
    }

    /**
     * To mix a value into the secret number, calculate the bitwise XOR of the given value and the secret number.
     * Then, the secret number becomes the result of that operation.
     * (If the secret number is 42, and you were to mix 15 into the secret number, the secret number would become 37.)
     *
     * @param secret the original secret number
     * @param number the modified secret number
     * @return the result of mixing
     */
    static int mix(int secret, int number)
    {
        return secret ^ number;
    }

    /**
     * To prune the secret number, calculate the value of the secret number modulo 16777216.
     * Then, the secret number becomes the result of that operation.
     * (If the secret number is 100000000, and you were to prune the secret number,
     * the secret number would become 16113920.)
     *
     * @param secret secret number to be pruned
     * @return pruned secret number
     */
    static int prune(int secret)
    {
        int result = secret % PRUNE_FACTOR;
        if (result < 0) result += PRUNE_FACTOR;
        return result;
    }

    public static int[] readIntegersFromFile(String filePath)
    {
        List<Integer> integers = new ArrayList<>();
        InputStream stream = Day22.class.getResourceAsStream(filePath);
        if (stream == null)
        {
            System.err.println("File not found: " + filePath);
            return new int[0];
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                String[] parts = line.split("\\s+"); // Split by whitespace
                for (String part : parts)
                {
                    try
                    {
                        int number = Integer.parseInt(part);
                        integers.add(number);
                    } catch (NumberFormatException e)
                    {
                        // Handle the exception if needed
                        System.err.println("Invalid number format: " + part);
                    }
                }
            }
        } catch (IOException e)
        {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }

        // Convert the list to an array and return
        return integers.stream().mapToInt(Integer::intValue).toArray();
    }

    private static int maxBananas()
    {
        int maxValue = Integer.MIN_VALUE;

        for (int firstKey : ledger.keySet())
        {
            HashMap<Integer, HashMap<Integer, HashMap<Integer, Integer>>> secondLayer = ledger.get(firstKey);

            for (int secondKey : secondLayer.keySet())
            {
                HashMap<Integer, HashMap<Integer, Integer>> thirdLayer = secondLayer.get(secondKey);

                for (int thirdKey : thirdLayer.keySet())
                {
                    Map<Integer, Integer> fourthLayer = thirdLayer.get(thirdKey);

                    for (int fourthKey : fourthLayer.keySet()) {
                        int leafValue = fourthLayer.get(fourthKey);
                        if (leafValue > maxValue) {
                        System.out.printf("%d: [%d, %d, %d, %d]%n",
                                leafValue,
                                fourthKey,
                                thirdKey,
                                secondKey,
                                firstKey);

                            maxValue = leafValue;
                        }
                    }
                }
            }
        }
        return maxValue;
    }
}
