import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day14
{
    public static void main(String[] args)
    {
//        Robot joe = new Robot(2,4,2,-3);
//        List<Robot> testBots = new ArrayList<>();
//        testBots.add(joe);
//        for (int i = 0; i < 5; i++) {
//            System.out.println(Robot.robotMap(testBots));
//            joe.move(1);
//        }
//        System.out.println(Robot.robotMap(testBots));


        List<Robot> robots = extractRobots("puzzle/day14.txt");
//        System.out.println(Robot.robotMap(robots));

        long maxDensity = Long.MIN_VALUE;
        long t = 0;
        while (true)
        {
            t += 1;
            long[] quadrants = {0L, 0L, 0L, 0L, 0L};
            for (Robot robot : robots)
            {
                robot.move(1);
                quadrants[robot.getQuadrant()] += 1L;
            }

            String map = Robot.robotMap(robots);
            long density = density(map);
            if (density >  maxDensity)
            {
                maxDensity = density;
                System.out.println(map);
                System.out.print(t);
                System.out.println(" " + quadrants[1]);
                try
                {
                    Thread.sleep(250);
                } catch (InterruptedException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }

//        System.out.println(Robot.robotMap(robots));
//
//        System.out.println(Arrays.toString(quadrants));
//        long safteyScore = Arrays.stream(Arrays.copyOfRange(quadrants, 1, 5)).reduce(1L, (long a, long b) -> a * b);
//        System.out.println(safteyScore);
    }

    private static long density(String map)
    {
        long density = 0;
        for (int row = 1; row < map.length() / Robot.WIDTH - 1; row++) {
            for (int col = 1; col < Robot.WIDTH - 1; col++) {
                int location = row * Robot.WIDTH + col;
                if (map.charAt(location) != '.') {
                    long localDensity = 0;
                    for (int offset : new int[] {-Robot.WIDTH, 1, Robot.WIDTH, -1}) {
                        char neighbor = map.charAt(location + offset);
                        localDensity += neighbor != '.' ? neighbor - '0' : 0;
                    }
                    density += localDensity * (map.charAt(location) - '0');
                }
            }
        }
        return density;
    }

    /**
     * Generated from Day 13 written by copilot
     *
     * @param filename name of the resource file including relative path
     * @return a list of robot
     */
    private static List<Robot> extractRobots(String filename)
    {
        List<Robot> robots = new ArrayList<>();
        Pattern pattern = Pattern.compile("p=(-?\\d+),(-?\\d+)\\s+v=(-?\\d+),(-?\\d+)");
        InputStream stream = Day14.class.getResourceAsStream(filename);
        if (stream == null)
        {
            System.err.println("File not found: " + filename);
            return robots;
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find())
                {
                    robots.add(
                            new Robot(
                                    Integer.parseInt(matcher.group(1)),
                                    Integer.parseInt(matcher.group(2)),
                                    Integer.parseInt(matcher.group(3)),
                                    Integer.parseInt(matcher.group(4))
                            ));
                }
            }
        } catch (IOException e) {System.err.println("Error reading file: " + e.getMessage());}
        return robots;
    }

    static class Robot
    {
        private static final int WIDTH = 101;
        private static final int HEIGHT = 103;
        private static long cycle = 1;
        private final int velX;
        private final int velY;
        private int posX;
        private int posY;

        public Robot(int x, int y, int dx, int dy)
        {
            this.posX = x;
            this.posY = y;
            this.velX = dx;
            this.velY = dy;
            cycle = lcm(cycle, getCycle());
        }

        public static long lcm(long a, long b)
        {
            return (a * b) / gcd(a, b);
        }

        private static long gcd(long a, long b)
        {
            while (b != 0)
            {
                long temp = b;
                b = a % b;
                a = temp;
            }
            return a;
        }

        public static String robotMap(List<Robot> robots)
        {
            char[][] map = new char[HEIGHT][WIDTH];


            for (Robot robot : robots)
            {
                map[robot.posY][robot.posX] += 1;
            }

            String mapString = "";
            for (int i = 0; i < map.length; i++)
            {
                for (int j = 0; j < map[i].length; j++)
                {
                    mapString += map[i][j] == 0 ? "." : (int) map[i][j];
                }
                mapString += "\n";
            }
            return mapString;
        }

        public long getCycle()
        {
            long cycleX = lcm(Math.abs(velX), WIDTH);
            long cycleY = lcm(Math.abs(velY), HEIGHT);
            return lcm(cycleX, cycleY);
        }

        public void move(int n)
        {
            posX = (posX + velX * n) % WIDTH;
            while (posX < 0) posX += WIDTH;
            posY = (posY + velY * n) % HEIGHT;
            while (posY < 0) posY += HEIGHT;
        }

        public int getQuadrant()
        {
            if (posX < WIDTH / 2 && posY < HEIGHT / 2) return 1;
            if (posX > WIDTH / 2 && posY < HEIGHT / 2) return 2;
            if (posX < WIDTH / 2 && posY > HEIGHT / 2) return 3;
            if (posX > WIDTH / 2 && posY > HEIGHT / 2) return 4;
            return 0;
        }
    }
}
