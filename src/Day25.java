import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day25
{
    private static final List<int[]> locks = new ArrayList<>();
    private static final List<int[]> keys = new ArrayList<>();

    public static void main(String[] args)
    {
        readLocksKeys("puzzle/day25.txt");

        System.out.println(locks.stream().map(Arrays::toString).collect(Collectors.joining(",")));
        System.out.println(keys.stream().map(Arrays::toString).collect(Collectors.joining(",")));

        int fit = 0;
        for (int[] lock : locks)
        {
            for (int[] key : keys)
            {
                boolean fits = true;
                for (int pin = 0; pin < key.length; pin++)
                {
                    if (lock[pin] + key[pin] > 5)
                    {
                        fits = false;
                        break;
                    }
                }
                if (fits) fit++;
            }
        }

        System.out.println(fit);
    }

    public static void readLocksKeys(String filePath)
    {
        InputStream stream = Day23.class.getResourceAsStream(filePath);
        if (stream == null)
        {
            System.err.println("File not found: " + filePath);
            return;
        }

        StringBuilder lines = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                lines.append(line).append("\n");
            }
        } catch (IOException e)
        {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }

        String[] devices = lines.toString().split("\n\n");
        for (String device : devices) {
            int[] pins = new int[5];
            String[] rows = device.split("\n");

            for (int row = 1; row < 6; row++) {
                for (int col = 0; col < rows[1].length(); col++) {
                    if (rows[row].charAt(col) == '#') pins[col]++;
                }
            }

            if (device.charAt(0) == '#') {
                locks.add(pins);
            } else {
                keys.add(pins);
            }
        }
    }
}
