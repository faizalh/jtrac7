package info.jtrac.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ValidationUtilsTest  {

    @Test
    public void testUpperCase() {
        assertTrue(ValidationUtils.isAllUpperCase("ABCD"));
        assertTrue(ValidationUtils.isAllUpperCase("AB123CD"));
        assertFalse(ValidationUtils.isAllUpperCase("ABCD-ABCD"));
        assertFalse(ValidationUtils.isAllUpperCase("AB CD"));        
    }

    @Test
    public void testValidLoginName() {
        assertTrue(ValidationUtils.isValidLoginName("abcd"));
        assertTrue(ValidationUtils.isValidLoginName("abcd123"));
        assertTrue(ValidationUtils.isValidLoginName("ab-cd"));
        assertTrue(ValidationUtils.isValidLoginName("ab.cd"));
        assertTrue(ValidationUtils.isValidLoginName("ab_cd"));
        assertTrue(ValidationUtils.isValidLoginName("Ab-Cd"));
        assertTrue(ValidationUtils.isValidLoginName("ab@cd"));
        assertTrue(ValidationUtils.isValidLoginName("AB\\cd"));
        assertTrue(ValidationUtils.isValidLoginName("AB\\abc@def.com"));
        assertFalse(ValidationUtils.isValidLoginName("ab%cd"));
        assertFalse(ValidationUtils.isValidLoginName("ab:cd"));
        assertFalse(ValidationUtils.isValidLoginName("ab cd"));
    }

    @Test
    public void testCamelDashCase() {
        assertTrue(ValidationUtils.isCamelDashCase("Abcd"));
        assertTrue(ValidationUtils.isCamelDashCase("Abcd-Efgh"));
        assertTrue(ValidationUtils.isCamelDashCase("Abcd-Efgh-Hijk"));
        assertFalse(ValidationUtils.isCamelDashCase("AbcdEfgh"));
        assertFalse(ValidationUtils.isCamelDashCase("Abcd123"));
        assertFalse(ValidationUtils.isCamelDashCase("8bcd"));
        assertFalse(ValidationUtils.isCamelDashCase("Ab-cd"));
        assertFalse(ValidationUtils.isCamelDashCase("Ab cd"));
    }
    
}
