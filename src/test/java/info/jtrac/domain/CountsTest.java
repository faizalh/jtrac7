package info.jtrac.domain;

import junit.framework.TestCase;
import org.junit.Test;

public class CountsTest extends TestCase {

    @Test
    public void testCountsLogic() {
        Counts c = new Counts(false);
        c.add(Counts.ASSIGNED_TO_ME, 1, 5);        
        assertEquals(0, c.getTotal());
        assertEquals(5, c.getAssignedToMe());
    }    
}
