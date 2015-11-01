package info.jtrac.lucene;

import info.jtrac.domain.Item;
import org.junit.Test;
import org.junit.Ignore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
// IndexSercher is deprecated in favour of hibernate's search feature
// the class and test will be modified or removed to reflect this.
public class IndexSearcherTest {
    
    private ApplicationContext context;
    
    @Ignore
    public void setUp() {
        File home = new File("target/home");
        if (!home.exists()) {
            home.mkdir();
        }
        File file = new File("target/home/indexes");
        if (!file.exists()) {
            file.mkdir();
        } else {            
            for (File f : file.listFiles()) {
                f.delete();
            }
        }
        System.setProperty("jtrac.home", home.getAbsolutePath());
        context = new FileSystemXmlApplicationContext("src/main/webapp/WEB-INF/applicationContext-lucene.xml");    
    }

    @Ignore
    public void testFindItemIdsBySearchingWithinSummaryAndDetailFields() throws Exception {       
        Item item = new Item();
        item.setId(1L);
        item.setSummary("this is a test summary");
        item.setDetail("the quick brown fox jumped over the lazy dogs");
        Indexer indexer = (Indexer) context.getBean("indexer");
        indexer.index(item);
        IndexSearcher searcher = (IndexSearcher) context.getBean("indexSearcher");
        List list = searcher.findItemIdsContainingText("lazy");
        assertEquals(1, list.size());
        list = searcher.findItemIdsContainingText("foo");
        assertEquals(0, list.size());
        list = searcher.findItemIdsContainingText("summary");
        assertEquals(1, list.size());
    }
    
}
