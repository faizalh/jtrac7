package info.jtrac.domain;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class ItemTest {

    @Test
    public void testSetAndGetForCustomInteger() {
        Item item = new Item();
        item.setCusInt01(5);
        assertEquals(item.getCusInt01().intValue(), 5);
    }
    
}
