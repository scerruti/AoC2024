import java.awt.desktop.SystemSleepEvent;
import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Day13
{

    public static final MathContext MC = new MathContext(5);
    public static final BigDecimal TOLERANCE = BigDecimal.valueOf(0.01);

    public static void main(String[] args)
    {
        List<int[]> lines = extractNumericDataFromFile("puzzle/day13.txt");

        long tokes = 0;
        BigInteger tokens = new BigInteger("0");
        for (int i = 0; i < lines.size(); i += 6)
        {
            tokes += compute1(lines.get(i)[0], lines.get(i + 1)[0],
                    lines.get(i + 2)[0], lines.get(i + 3)[0],
                    lines.get(i + 4)[0], lines.get(i + 5)[0]);
            tokens = tokens.add(compute2(lines.get(i)[0], lines.get(i + 1)[0],
                    lines.get(i + 2)[0], lines.get(i + 3)[0],
                    lines.get(i + 4)[0], lines.get(i + 5)[0]));
        }

        System.out.println(tokes);
        System.out.println(tokens);
    }

    public static long compute1(int xA, int yA, int xB, int yB, int x, int y)
    {
        double aM = (double) yA / xA;
        double bM = (double) yB / xB;

        double x1 = ((bM * x) - y) / (bM - aM);
        double y1 = aM * x1;

        double x2 = ((aM * x) - y) / (aM - bM);
        double y2 = bM * x2;

        System.out.printf("(%.2f, %.2f) (%.2f, %.2f)%n)", x1, y1, x2, y2);

        int aPresses1 = 0, bPresses1 = 0, aPresses2 = 0, bPresses2 = 0;
        boolean valid1 = false, valid2 = false;
        if (Math.abs(x1 - (int) (x1 + 0.5) + y1 - (int) (y1 + 0.5)) < 0.0001)
        {
            aPresses1 = (int) (y1 / yA + 0.5);
            bPresses1 = (int) ((y - y1) / yB + 0.5);
            if (0 < aPresses1 && aPresses1 <= 100 && 0 < bPresses1 && bPresses1 <= 100) valid1 = true;
        }
        ;
        if (Math.abs(x2 - (int) (x2 + 0.5) + y1 - (int) (y2 + 0.5)) < 0.0001)
        {
            aPresses2 = (int) (y2 / yA + 0.5);
            bPresses2 = (int) ((y - y2) / yB + 0.5);
            if (0 < aPresses2 && aPresses2 <= 100 && 0 < bPresses2 && bPresses2 <= 100) valid2 = true;
        }
        ;

//        if (valid1) System.out.printf("1 Button A: %d Button B: %d %n", aPresses1, bPresses1);
//        if (valid2) System.out.printf("2 Button A: %d Button B: %d %n", valid2, aPresses2, bPresses2);

        int aPresses, bPresses;
        if (valid1 && valid2)
        {
            if (aPresses1 < bPresses1)
            {
                aPresses = aPresses1;
                bPresses = bPresses1;
            } else
            {
                aPresses = aPresses2;
                bPresses = bPresses2;
            }
        } else if (valid1)
        {
            aPresses = aPresses1;
            bPresses = bPresses1;
        } else if (valid2)
        {
            aPresses = aPresses2;
            bPresses = bPresses2;
        } else
        {
            return 0;
        }

//        System.out.printf("Button A: %d Button B: %d %n",
//                aPresses, bPresses);
//
        int tokens = aPresses * 3 + bPresses;
//        System.out.printf("> %d tokens%n%n", tokens);
        return tokens;
    }

    public static BigInteger compute2(int xA, int yA, int xB, int yB, int x, int y)
    {
        BigDecimal xL = new BigDecimal(x).add(new BigDecimal("10000000000000"));
        BigDecimal yL = new BigDecimal(y).add(new BigDecimal("10000000000000"));

        double aM = (double) yA / xA;
        double bM = (double) yB / xB;

        BigDecimal x1 = xL.multiply(BigDecimal.valueOf(bM)).subtract(yL);
        x1 = x1.divide(BigDecimal.valueOf(bM - aM), RoundingMode.HALF_UP);
        BigDecimal y1 = x1.multiply(BigDecimal.valueOf(aM));

        BigDecimal x2 = xL.multiply(BigDecimal.valueOf(aM)).subtract(yL);
        x2 = x2.divide(BigDecimal.valueOf(aM - bM), RoundingMode.HALF_UP);
        BigDecimal y2 = x2.multiply(BigDecimal.valueOf(bM));

        BigInteger aPresses1 = BigInteger.ZERO, bPresses1 = BigInteger.ZERO, aPresses2 = BigInteger.ZERO, bPresses2 = BigInteger.ZERO;
        boolean valid1 = false, valid2 = false;

        if (isApproximatelyWholeNumber(x1) && isApproximatelyWholeNumber(y1))
        {
            aPresses1 = y1.divide(BigDecimal.valueOf(yA), 0, RoundingMode.HALF_UP).toBigInteger();
            bPresses1 = yL.subtract(y1).divide(BigDecimal.valueOf(yB), 0, RoundingMode.HALF_UP).toBigInteger();
            if (aPresses1.compareTo(BigInteger.ZERO) > 0 && bPresses1.compareTo(BigInteger.ZERO) > 0) valid1 = true;
            if (valid1) System.out.printf("1 a: %s b: %s%n", aPresses1.toString(), bPresses1.toString());
        }

        if (isApproximatelyWholeNumber(x2) && isApproximatelyWholeNumber(y2))
        {
            aPresses2 = y2.divide(BigDecimal.valueOf(yA), 0, RoundingMode.HALF_UP).toBigInteger();
            bPresses2 = yL.subtract(y2).divide(BigDecimal.valueOf(yB), 0, RoundingMode.HALF_UP).toBigInteger();
            if (aPresses2.compareTo(BigInteger.ZERO) > 0 && bPresses2.compareTo(BigInteger.ZERO) > 0) valid2 = true;
            if (valid2) System.out.printf("2 a: %s b: %s%n", aPresses2.toString(), bPresses2.toString());
        }
        ;

        BigInteger aPresses, bPresses;
        if (valid1 && valid2)
        {
            if (aPresses1.multiply(BigInteger.valueOf(3)).add(bPresses1)
            .compareTo(aPresses2.multiply(BigInteger.valueOf(3)).add(bPresses2)) < 0)
            {
                aPresses = aPresses1;
                bPresses = bPresses1;
            } else
            {
                aPresses = aPresses2;
                bPresses = bPresses2;
            }
        } else if (valid1)
        {
            aPresses = aPresses1;
            bPresses = bPresses1;
        } else if (valid2)
        {
            aPresses = aPresses2;
            bPresses = bPresses2;
        } else
        {
            System.out.println("Can't win");
            return BigInteger.ZERO;
        }

        System.out.printf("Button A: %d Button B: %d %n",
                aPresses, bPresses);

        BigInteger tokens = aPresses.multiply(BigInteger.valueOf(3)).add(bPresses);
        System.out.printf("> %s tokens%n%n", tokens);
        return tokens;
    }


    /**
     * Generated by copilot
     *
     * @param filename
     * @return
     */
    public static List<int[]> extractNumericDataFromFile(String filename)
    {
        List<int[]> numericData = new ArrayList<>();
        Pattern pattern = Pattern.compile("(\\d+)");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(Day13.class.getResourceAsStream(filename))))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find())
                {
                    String match = matcher.group();
                    numericData.add(new int[]{Integer.parseInt(match)});
                }
            }
        } catch (IOException e) {System.err.println("Error reading file: " + e.getMessage());}
        return numericData;
    }

    public static boolean isApproximatelyWholeNumber(BigDecimal value)
    {
        // Get the nearest integer value
        BigDecimal nearestInteger = value.setScale(0, RoundingMode.HALF_UP);

        // Calculate the difference between the value and the nearest integer
        BigDecimal difference = value.subtract(nearestInteger).abs();

        // Check if the difference is within the tolerance
        return difference.compareTo(TOLERANCE) <= 0;
    }
}
