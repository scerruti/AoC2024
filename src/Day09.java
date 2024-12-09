import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class Day09
{
    private static final int RUN_TYPE_PARAMETER = 0;
    private static ArrayList<Block> diskMap = new ArrayList<>();
    private static int unpackedStart = 0;

    public static void main(String[] args)
    {
        processInput(args[RUN_TYPE_PARAMETER]);
        ArrayList<Block> blocks = new ArrayList<>();
        for (Block block : diskMap) {
            blocks.add( new Block(block));
        }
        printDiskMap();
        System.out.println("Checksum: " + part1());

        diskMap = blocks;
        unpackedStart = 0;
        System.out.println("Optimal Checksum: " + part2());
    }

    private static void processInput(String runType)
    {
        int fileID = 0;
        byte[] rawFileMap = new byte[0];
        try (InputStream dataFile = Day02.class.getResourceAsStream(runType + "/day09.txt"))
        {
            if (dataFile != null)
                rawFileMap = dataFile.readAllBytes();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        boolean freeSpace = false;
        for (Byte fileByte : rawFileMap)
        {
            diskMap.add(new Block(fileID, fileByte - 0x30, freeSpace));
            if (!freeSpace) fileID++;
            freeSpace = !freeSpace;
        }
    }

    public static long part1()
    {
        return pack();
    }

    public static long pack()
    {
//        printDiskMap();

        for (int i = diskMap.size() - 1; i >= unpackedStart; i--)
        {
            if (diskMap.get(i).isFree()) continue;
            Block block = new Block(diskMap.get(i));
            diskMap.get(i).freeSpace = true;

            distributeBlock(block);
//            printDiskMap();
        }

//        printDiskMap();
        return checksum();
    }

    private static void distributeBlock(Block block)
    {
        while (unpackedStart < diskMap.size() && !diskMap.get(unpackedStart).isFree()) unpackedStart++;

        if (unpackedStart >= diskMap.size()) return;

        Block freeBlock = diskMap.get(unpackedStart);
        int freeSpace = freeBlock.length;

        if (freeSpace <= block.length)
        {
            freeBlock.fileID = block.fileID;
            block.length -= freeSpace;
            freeBlock.freeSpace = false;
            if (block.length > 0)
            {
                distributeBlock(block);
            }
        } else
        {
            freeBlock.length -= block.length;
            diskMap.add(unpackedStart, block);
        }
    }

    private static void printDiskMap()
    {
        for (Block block : diskMap)
        {
            for (int i = 0; i < block.length; i++)
            {
                System.out.print(block.isFree() ? "." : block.fileID);
            }
        }
        System.out.println();
    }

    private static long checksum()
    {
        long checksum = 0;
        int position = 0;
        for (Block block : diskMap)
        {
            if (block.isFree())
            {
                position += block.length;
                continue;
            }

            for (int j = 0; j < block.length; j++)
            {
                checksum += (long) block.fileID * position;
                position++;
            }
        }
        return checksum;
    }

    public static long part2()
    {
        return optimalPack();
    }

    public static long optimalPack()
    {
//        printDiskMap();

        for (int i = diskMap.size() - 1; i >= 0; i--)
        {
            if (diskMap.get(i).isFree()) continue;
            Block block = new Block(diskMap.get(i));
            diskMap.get(i).freeSpace = true;

            distributeBlockOptimally(block);
//            printDiskMap();
        }

//        printDiskMap();
        return checksum();
    }

    private static void distributeBlockOptimally(Block block)
    {
        unpackedStart = 0;
        while (unpackedStart < diskMap.size() &&
                !(diskMap.get(unpackedStart).isFree() &&
                diskMap.get(unpackedStart).length >= block.length))
            unpackedStart++;

        if (unpackedStart >= diskMap.size()) {
            return;
        }

        Block freeBlock = diskMap.get(unpackedStart);


        freeBlock.length -= block.length;
        diskMap.add(unpackedStart, block);

    }

    private static class Block
    {
        private int fileID;
        private int length;
        private boolean freeSpace;

        public Block(int fileID, int fileByte, boolean freeSpace)
        {
            this.fileID = fileID;
            length = fileByte;
            this.freeSpace = freeSpace;
        }

        public Block(Block block)
        {
            this.fileID = block.fileID;
            this.length = block.length;
            this.freeSpace = block.freeSpace;
        }

        public boolean isFree()
        {
            return freeSpace;
        }
    }
}
