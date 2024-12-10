import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public class Day10
{
    private static final int RUN_TYPE_PARAMETER = 0;
    private static int[] trailMap;
    private static final ArrayList<Integer> trailHeads = new ArrayList<>();
    private static int width  = -1;
    private static int height = 0;


    public static void main(String[] args)
    {
        processInput(args[RUN_TYPE_PARAMETER]);

        System.out.println("Score: " + part1());

        System.out.println("Discrete Score: " + part2());
    }

    private static void processInput(String runType)
    {
        byte[] rawFileMap = new byte[0];

        try (InputStream dataFile = Day02.class.getResourceAsStream(runType + "/day10.txt"))
        {
            if (dataFile != null)
                rawFileMap = dataFile.readAllBytes();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        for (int i = 0; i < rawFileMap.length; i++) {
            if (rawFileMap[i] == '\n')
            {
                if (width == -1) width = i;
                height += 1;
            }
        }
        height++;

        trailMap = new int[width * height];
        int position = 0;
        for (byte b : rawFileMap)
        {
            if (b != '\n')
            {
                trailMap[position] = b - '0';
                if (b == '0') trailHeads.add(position);

                position += 1;
            }

        }


    }

    public static long part1()
    {
        long count = 0;
        for (Integer trailHead : trailHeads) {
            HashSet<Integer> paths = new HashSet<>();
            walkTrail(paths, trailHead, 0);
            count += paths.size();
        }
        return count;
    }

    public static void walkTrail(Collection<Integer> paths, int position, int depth) {
//        System.out.printf("Position: (%d, %d) Depth: %d%n", position / width, position % width, depth);
        if (depth != trailMap[position]) return;
        if (depth == 9) paths.add(position);

        ArrayList<Integer> nextSteps = getValidPositions(position);

        for (Integer nextStep : nextSteps) {
            walkTrail(paths, nextStep, depth+1);
        }
    }

    public static ArrayList<Integer> getValidPositions(int position)
    {
        ArrayList<Integer> validPositions = new ArrayList<>();

        int row = position / width;
        int col = position % width;

        if (row > 0) validPositions.add(position - width);
        if (row < width-1) validPositions.add(position + width);
        if (col > 0) validPositions.add(position - 1);
        if (col < width-1) validPositions.add(position + 1);

        return validPositions;
    }

    public static long part2()
    {
        long count = 0;
        for (Integer trailHead : trailHeads) {
            ArrayList<Integer> paths = new ArrayList<>();
            walkTrail(paths, trailHead, 0);
            count += paths.size();
        }
        return count;    }

}
