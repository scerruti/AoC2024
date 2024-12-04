import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

/**
 * --- Day 4: Ceres Search ---
 * See <a href='https://adventofcode.com/2024/day/4'>Advent of Code: Day 4</a> for problem description
 */
public class Day04
{
    public static final int RUN_TYPE_PARAMETER = 0;
    public static final String[] WORD_LIST = {"XMAS"};

    public static void main(String[] args)
    {
        System.out.println("Number of words found: " + part1(args[RUN_TYPE_PARAMETER]));
        System.out.println("Result with conditionals: " + part2(args[RUN_TYPE_PARAMETER]));
    }

    /**
     * Part 1 of <a href='https://adventofcode.com/2024/day/2'>Advent of Code: Day 2</a>.
     *
     * @param runType values of "example" or "puzzle" represents the resource subdirectory for the data
     * @return total distance between lists
     */
    private static long part1(String runType)
    {
        int counter = 0;
        Puzzle wordSearch = getPuzzle(runType);

        for (String word : WORD_LIST) {
            System.out.println("\nHorizontal");
            counter += wordSearch.findHorizontal(word);
            System.out.println("\nVertical");
            counter += wordSearch.findVertical(word);
            System.out.println("\nPositive");
            counter += wordSearch.findDiagonal(word, 1);
            System.out.println("\nNegative");
            counter += wordSearch.findDiagonal(word, -1);
        }
        return counter;
    }

    private static Puzzle getPuzzle(String runType)
    {
        try (InputStream dataFile = Day02.class.getResourceAsStream(runType + "/day04.txt")) {
            Scanner scanner = new Scanner(dataFile);
            StringBuilder memory = new StringBuilder();
            int rowLength = 0;
            while (scanner.hasNextLine())
            {
                memory.append(scanner.nextLine());
                if (rowLength == 0) rowLength = memory.length();
            }
            return new Puzzle(rowLength, memory.toString());
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     *  Part 2 of <a href='https://adventofcode.com/2024/day/2'>Advent of Code: Day 2</a>.
     *
     * @param runType values of "example" or "puzzle" represents the resource subdirectory for the data
     * @return similarity score
     */
    private static long part2(String runType)
    {
        long counter = 0;
        Puzzle wordSearch = getPuzzle(runType);


        return wordSearch.getXmas();
    }

    private static class Puzzle
    {
        private final int rowLength;
        private final String puzzleLetters;

        public Puzzle(int rowLength, String puzzleLetters)
        {
            this.rowLength = rowLength;
            this.puzzleLetters = puzzleLetters;
        }

        private int countWordsInText(String word, String text) {
            int counter = 0;
            int pos = 0;

            System.out.printf("Find %s in %s\n", word, text);

            while ((pos = text.indexOf(word, pos)) != -1) {
                System.out.printf("\t%d%n", pos);
                counter++;
                pos++;
            }
            return counter;
        }

        public int findHorizontal(String word) {
            int counter = 0;
            for (int i = 0; i < puzzleLetters.length(); i += rowLength) {
                String row = puzzleLetters.substring(i, i + rowLength);
                counter += countWordsInText(word, row);
                counter += countWordsInText(word, new StringBuilder(row).reverse().toString());
            }
            return counter;
        }

        public int findVertical(String word) {
            int counter = 0;
            for (int i = 0; i < rowLength; i++) {
                StringBuilder rowBuilder = new StringBuilder();
                for (int c = i; c < puzzleLetters.length(); c += rowLength) {
                    rowBuilder.append(puzzleLetters.charAt(c));
                }
                String row = rowBuilder.toString();
                counter += countWordsInText(word, row);
                counter += countWordsInText(word, new StringBuilder(row).reverse().toString());
            }
            return counter;
        }

        public int findDiagonal(String word, int direction) {
            int counter = 0;
            for (int col = 0; col < rowLength; col++)
            {
                StringBuilder rowBuilder = new StringBuilder();
                int end;
                if (direction > 0) end = (rowLength - col) * rowLength ;
                else end = col * rowLength + 1;
                for (int i = col; i < end && i < puzzleLetters.length(); i += rowLength + direction)
                {;
                    rowBuilder.append(puzzleLetters.charAt(i));
                }
                if (rowBuilder.toString().length() < word.length()) continue;
                String text = rowBuilder.toString();
                counter += countWordsInText(word, text);
                counter += countWordsInText(word, new StringBuilder(text).reverse().toString());
            }

            for (int row = 1; row < puzzleLetters.length() / rowLength; row++) {
                StringBuilder rowBuilder = new StringBuilder();
                int start = direction == 1 ? row * rowLength : (row + 1) * rowLength - 1;
                for (int i = start; i < puzzleLetters.length(); i += rowLength + direction)
                {
                    rowBuilder.append(puzzleLetters.charAt(i));
                }
                if (rowBuilder.toString().length() < word.length()) continue;
                String text = rowBuilder.toString();
                counter += countWordsInText(word, text);
                counter += countWordsInText(word, new StringBuilder(text).reverse().toString());
            }
            return counter;
        }

        public long getXmas()
        {
            int count = 0;

            for (int row = 1; row < puzzleLetters.length() / rowLength - 1; row++) {
                for (int col = 1; col < rowLength - 1; col++) {
                    int position = row * rowLength + col;
                    if (puzzleLetters.charAt(position) != 'A') continue;

                    System.out.printf("Candidate (%d, %d)%n", row, col);

                    String positive = "" + puzzleLetters.charAt(position - rowLength - 1) + puzzleLetters.charAt(position + rowLength + 1);
                    String negative = "" + puzzleLetters.charAt(position - rowLength + 1) + puzzleLetters.charAt(position + rowLength - 1);

                    if (positive.indexOf('S') != -1 && negative.indexOf('S') != -1 &&
                            positive.indexOf('M') != -1 && negative.indexOf('M') != -1) {
                        count++;
                        System.out.printf("Match (%d, %d)%n", row, col);
                    }
                }
            }
            return count;
        }
    }
}
