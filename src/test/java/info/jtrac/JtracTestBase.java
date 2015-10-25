package info.jtrac;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

/**
 * base class for tests that can test either the service layer or dao or both
 * using the Spring JUnit helper class with the long name, ensures that
 * the applicationContext is only built once
 */
@ContextConfiguration("file:src/main/webapp/WEB-INF/applicationContext.xml")
public abstract class JtracTestBase extends AbstractTransactionalJUnit4SpringContextTests {
    
    protected Jtrac jtrac;
    protected JtracDao dao;
    
    public JtracTestBase() {
        System.setProperty("jtrac.home", "target/home");
    }
    
    // magically autowired by Spring JUnit support
    @Autowired
    public void setDao(JtracDao dao) {
        this.dao = dao;
    }
    
    //  magically autowired by Spring JUnit support
    @Autowired
    public void setJtrac(Jtrac jtrac) {
        this.jtrac = jtrac;
    }
    
    protected String[] getConfigLocations() {

        return new String[] {
            "file:src/main/webapp/WEB-INF/applicationContext.xml",
//            "file:src/main/webapp/WEB-INF/applicationContext-lucene.xml"
        };
    }


    
}
