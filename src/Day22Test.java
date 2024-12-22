import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Day22Test
{

    @Test
    void testMix()
    {
        assertEquals(37, Day22.mix(42, 15));
    }

    @Test
    void testPrune()
    {
        assertEquals(16113920, Day22.prune(100000000));
    }
}