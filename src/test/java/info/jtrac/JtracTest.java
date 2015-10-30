package info.jtrac;

import info.jtrac.domain.*;
import info.jtrac.util.ItemUtils;
import org.hibernate.SessionFactory;
import org.junit.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.transaction.TestTransaction;

import java.util.*;

import static org.junit.Assert.*;

/**
 * JUnit test cases for the business implementation as well as the DAO
 * Tests assume that a database is available, and with HSQLDB around this is not
 * an issue.
 */
public class JtracTest extends JtracTestBase {
    
    private Space getSpace() {
        Space space = new Space();
        space.setPrefixCode("TEST");
        space.setName("Test Space");
        return space;
    }
    
    private Metadata getMetadata() {
        Metadata metadata = new Metadata();
        String xmlString = "<metadata><fields>"
                + "<field name='cusInt01' label='Test Label'/>"
                + "<field name='cusInt02' label='Test Label 2'/>"
                + "</fields></metadata>";
        metadata.setXmlString(xmlString);
        return metadata;
    }
    
    //==========================================================================
    @Test
    public void testGeneratedPasswordIsAlwaysDifferent() {
        String p1 = jtrac.generatePassword();
        String p2 = jtrac.generatePassword();
        //assertTrue(!p1.equals(p2));
        assertNotSame(p1, p2);
    }
    @Test
    public void testEncodeClearTextPassword() {
        assertEquals("21232f297a57a5a743894a0e4a801fc3", jtrac.encodeClearText("admin"));
    }
    @Test
    public void testMetadataInsertAndLoad() {
        Metadata m1 = getMetadata();
        jtrac.storeMetadata(m1);
        assertTrue(m1.getId() > 0);
        Metadata m2 = jtrac.loadMetadata(m1.getId());
        assertTrue(m2 != null);
        Map<Field.Name, Field> fields = m2.getFields();
        assertTrue(fields.size() == 2);
    }
    @Test
    public void testUserInsertAndLoad() {
        User user = new User();
        user.setLoginName("test");
        user.setEmail("test@jtrac.com");
        user = jtrac.storeUser(user);
        User user1 = jtrac.loadUser("test");
        assertTrue(user1.getEmail().equals("test@jtrac.com"));
        User user2 = dao.findUsersByEmail("test@jtrac.com").get(0);
        assertTrue(user2.getLoginName().equals("test"));
    }
    @Test
    public void testUserSpaceRolesInsert() {
        Space space = getSpace();
        Metadata metadata = getMetadata();
        
        space.setMetadata(metadata);
        jtrac.storeSpace(space);
        
        User user = new User();
        user.setLoginName("test");
        
        user.addSpaceWithRole(space, "ROLE_TEST");
        jtrac.storeUser(user);
        
        User u1 = jtrac.loadUser("test");
        
        Collection<? extends GrantedAuthority> gas1 = u1.getAuthorities();
        GrantedAuthority[] gas = new GrantedAuthority[gas1.size()];
        gas1.toArray(gas);
        assertEquals(2, gas.length);
        assertEquals("ROLE_USER", gas[0].getAuthority());
        assertEquals("ROLE_TEST_TEST", gas[1].getAuthority());
        
        List<UserSpaceRole> userSpaceRoles = jtrac.findUserRolesForSpace(space.getId());
        assertEquals(1, userSpaceRoles.size());
        UserSpaceRole usr = userSpaceRoles.get(0);
        assertEquals("test", usr.getUser().getLoginName());
        assertEquals("ROLE_TEST", usr.getRoleKey());
        
        List<User> users = jtrac.findUsersForUser(u1);
        assertEquals(1, users.size());
        
        List<User> users2 = jtrac.findUsersForSpace(space.getId());
        assertEquals(1, users2.size());
        
    }
    @Test
    public void testConfigStoreAndLoad() {
        Config config = new Config("testParam", "testValue");
        jtrac.storeConfig(config);
        String value = jtrac.loadConfig("testParam");
        assertEquals("testValue", value);
    }
    @Test
    public void testStoreAndLoadUserWithAdminRole() {
        User user = new User();
        user.setLoginName("test");
        user.addSpaceWithRole(null, "ROLE_ADMIN");
        jtrac.storeUser(user);
        
        UserDetails ud = jtrac.loadUserByUsername("test");
        
        Set<String> set = new HashSet<String>();
        for (GrantedAuthority ga : ud.getAuthorities()) {
            set.add(ga.getAuthority());
        }
        
        assertEquals(2, set.size());
        assertTrue(set.contains("ROLE_USER"));
        assertTrue(set.contains("ROLE_ADMIN"));
        
    }
    @Test
    public void testDefaultAdminUserHasAdminRole() {
        UserDetails ud = jtrac.loadUserByUsername("admin");
        Set<String> set = new HashSet<String>();
        for (GrantedAuthority ga : ud.getAuthorities()) {
            set.add(ga.getAuthority());
        }
        assertEquals(2, set.size());
        assertTrue(set.contains("ROLE_USER"));
        assertTrue(set.contains("ROLE_ADMIN"));
    }
    @Test
    public void testItemInsertAndCounts() {
        Space s = getSpace();
        jtrac.storeSpace(s);
        User u = new User();
        u.setLoginName("test");
        u.addSpaceWithRole(s, "DEFAULT");
        jtrac.storeUser(u);
        Item i = new Item();
        i.setSpace(s);
        i.setAssignedTo(u);
        i.setLoggedBy(u);
        i.setStatus(State.CLOSED);
        jtrac.storeItem(i, null);
        assertEquals(1, i.getSequenceNum());
        
        CountsHolder ch = jtrac.loadCountsForUser(u);
        assertEquals(1, ch.getTotalAssignedToMe());
        assertEquals(1, ch.getTotalLoggedByMe());
        assertEquals(1, ch.getTotalTotal());
        
        Counts c = ch.getCounts().get(s.getId());
        assertEquals(1, c.getLoggedByMe());
        assertEquals(1, c.getAssignedToMe());
        assertEquals(1, c.getTotal());
    }
    @Test
    public void testRemoveSpaceRoleDoesNotOrphanDatabaseRecord() {
        Space space = getSpace();
        jtrac.storeSpace(space);
        long spaceId = space.getId();
        User user = new User();
        user.setLoginName("test");
        user.addSpaceWithRole(space, "ROLE_ADMIN");
        jtrac.storeUser(user);
        long id = jdbcTemplate.queryForObject("select id from user_space_roles where space_id = " + spaceId, Long.class);
        UserSpaceRole usr = jtrac.loadUserSpaceRole(id);
        assertEquals(spaceId, usr.getSpace().getId().longValue());
        jtrac.removeUserSpaceRole(usr);
        //FH Might not be allowed to have the following in the updated TransactionalSpringJUnit as only one transaction allowed(?)
        //endTransaction();
        //Integer result = jdbcTemplate.queryForObject("select count(0) from user_space_roles where space_id = " + spaceId, Integer.class);
        //assertEquals(0,result.intValue());
       // TestTransaction.flagForCommit();
        //TestTransaction.end();
        //assertFalse(TestTransaction.isActive());
        ////TestTransaction.start();
        sessionFactory.getCurrentSession().flush();
        Integer result = jdbcTemplate.queryForObject("select count(0) from user_space_roles where space_id = " + spaceId, Integer.class);
        assertEquals(0,result.intValue());



    }
    @Test
    public void testFindSpacesWhereGuestAllowed() {
        Space space = getSpace();
        space.setGuestAllowed(true);
        jtrac.storeSpace(space);
        assertEquals(1, jtrac.findSpacesWhereGuestAllowed().size());
    }
    @Test
    public void testRenameSpaceRole() {
        Space space = getSpace();
        jtrac.storeSpace(space);
        User u = new User();
        u.setLoginName("test");
        u.addSpaceWithRole(space, "DEFAULT");
        jtrac.storeUser(u);
       assertEquals(1, jdbcTemplate.queryForObject("select count(0) from user_space_roles where role_key = 'DEFAULT'", Integer.class).intValue());
        jtrac.bulkUpdateRenameSpaceRole(space, "DEFAULT", "NEWDEFAULT");
        assertEquals(0, jdbcTemplate.queryForObject("select count(0) from user_space_roles where role_key = 'DEFAULT'", Integer.class).intValue());
        assertEquals(1, jdbcTemplate.queryForObject("select count(0) from user_space_roles where role_key = 'NEWDEFAULT'", Integer.class).intValue());
    }
    @Test
    public void testGetItemAsHtmlDoesNotThrowException() {
        Config config = new Config("mail.server.host", "dummyhost");
        jtrac.storeConfig(config);
        // now email sending is switched on
        Space s = getSpace();
        jtrac.storeSpace(s);
        User u = new User();
        u.setLoginName("test");
        u.setName("Test User");
        u.setEmail("test");
        u.addSpaceWithRole(s, "DEFAULT");
        jtrac.storeUser(u);
        Item i = new Item();
        i.setSpace(s);
        i.setAssignedTo(u);
        i.setLoggedBy(u);
        i.setStatus(State.CLOSED);
        // next step will internally try to render item as Html for sending e-mail
        jtrac.storeItem(i, null);
        System.out.println(ItemUtils.getAsXml(i).asXML());
    }
    //FH Following test not allowed for abstract class Transactional...(?)
    @Test
    public void testDeleteItemThatHasRelatedItems() {
        Space s = getSpace();
        jtrac.storeSpace(s);
        User u = new User();
        u.setLoginName("test");
        u.setEmail("dummy");
        u.addSpaceWithRole(s, "DEFAULT");
        jtrac.storeUser(u);
        //========================
        Item i0 = new Item();
        i0.setSpace(s);
        i0.setAssignedTo(u);
        i0.setLoggedBy(u);
        i0.setStatus(State.CLOSED);
        jtrac.storeItem(i0, null);
        //=======================
        sessionFactory.getCurrentSession().flush();
        Item i1 = new Item();
        i1.setSpace(s);
        i1.setAssignedTo(u);
        i1.setLoggedBy(u);
        i1.setStatus(State.CLOSED);
        i1.addRelated(i0, ItemItem.DEPENDS_ON);
        jtrac.storeItem(i1, null);
        //========================
        Item i2 = new Item();
        i2.setSpace(s);
        i2.setAssignedTo(u);
        i2.setLoggedBy(u);
        i2.setStatus(State.CLOSED);
        i2.addRelated(i1, ItemItem.DUPLICATE_OF);
        jtrac.storeItem(i2, null);
        assertEquals(3, jtrac.loadCountOfHistoryInvolvingUser(u));
        // can we remove i1?
        Item temp = jtrac.loadItem(i1.getId());
        jtrac.removeItem(temp);
    }
    
}
