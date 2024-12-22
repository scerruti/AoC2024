import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.LinkedTransferQueue;

public class Day20
{
    private static int width = 0;
    private static int start = 0;
    private static int end = 0;
    private static char[] map;
    private static int[] distance;
    static HashMap<Integer, HashMap<Integer, Integer>> cheats = new HashMap<>();

    public static void main(String[] args)
    {
        readMap();
        distance = new int[map.length];
        Arrays.fill(distance, Integer.MAX_VALUE);

        ArrayDeque<Integer> stack = new ArrayDeque<>();
        stack.push(start);
        distance[start] = 0;

        while (!stack.isEmpty()) {

            int curr = stack.pop();
            int step = distance[curr] + 1;
            findCheats(curr, step, 20);

            for (int offset : new int[]{-width, 1, width, -1}){
                if (map[curr + offset] == '.' && distance[curr + offset] > step) {
                    distance[curr + offset] = step;
                    stack.push(curr + offset);
                }
            }
        }

        printMap();
        System.out.println("Race Length: " + distance[end]);
        System.out.println(cheats);

        int total = 0;
        HashMap<Integer, Integer> frequency = new HashMap<>();
        for (HashMap<Integer, Integer> cheat : cheats.values()) {
            for (Integer i : cheat.values())
            {
                if (i < 100) continue;
                frequency.put(i, frequency.getOrDefault(i, 0) + 1);
                total += 1;
            }
        }

        for (int i : frequency.keySet().stream().sorted().toList()) {
            System.out.println(i +": " + frequency.get(i));
        }

        System.out.println("Part 1: " + total);
    }

    private static void findCheats(int curr, int step, int length)
    {
        ArrayDeque<Integer> stack = new ArrayDeque<>();

        for (int cheatLength = 2; cheatLength <= length; cheatLength++)
        {
            for (int rowOffset = -cheatLength; rowOffset <= cheatLength; rowOffset++) {
                int colOffset = cheatLength - Math.abs(rowOffset);
                int jump = curr + rowOffset * width + colOffset;

                if (!(0 > jump || jump >= map.length || curr / width != (jump / width - rowOffset) || map[jump] != '.')) {
                    if (step - distance[jump] > cheatLength + 1) {
                        cheats.putIfAbsent(jump, new HashMap<>());
                        cheats.get(jump).putIfAbsent(curr, step - distance[jump] - (cheatLength + 1));
                    }
                }

                jump = curr + rowOffset * width - colOffset;
                if (!(0 > jump || jump >= map.length || curr / width != (jump / width - rowOffset) || map[jump] != '.')) {
                    if (step - distance[jump] > cheatLength + 1) {
                        cheats.putIfAbsent(jump, new HashMap<>());
                        cheats.get(jump).putIfAbsent(curr, step - distance[jump] - (cheatLength + 1));
                    }
                }
            }
        }


    }


    private static void readMap()
    {
        StringBuilder mapString = new StringBuilder();

        InputStream stream = Day20.class.getResourceAsStream("puzzle/day20.txt");
        if (stream == null)
        {
            System.err.println("File not found");
            return;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            String line;
            int row = 0;

            while ((line = reader.readLine()) != null) {
                if (line.indexOf('S') != -1) {
                    width = line.trim().length();
                    start = line.indexOf('S') + row * width;
                    line = line.replace('S', '.');
                }
                if (line.indexOf('E') != -1) {
                    width = line.trim().length();
                    end = line.indexOf('E') + row * width;
                    line = line.replace('E', '.');
                }
                mapString.append(line.trim());
                row++;
            }
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        map = mapString.toString().toCharArray();
    }

    private static void printMap() {
        for (int row = 0; row < (map.length / width); row++) {
            for (int col = 0; col < width; col++)
            {
                System.out.print(map[row*width + col]);
            }
            System.out.print("\t\t");
            for (int col = 0; col < width; col++)
            {
                System.out.printf("%2d ", distance[row*width + col] < 100 ? distance[row*width + col] : 0 );
            }
            System.out.println();
        }
    }
}
