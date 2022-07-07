package springbook.user.dao;

import org.junit.Before;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import springbook.user.domain.User;

import java.sql.SQLException;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UserDaoTest {
    private UserDao dao;
    private User user1;
    private User user2;
    private User user3;
    @Before
    public void setUp() {
        GenericApplicationContext context =
                new GenericXmlApplicationContext("applicationContext.xml");
        dao = context.getBean("setterUserDao", UserDao.class);

        user1 = new User("gyumee", "park", "springno1");
        user2 = new User("leegw700", "Lee", "springno2");
        user3 = new User("bumjin", "park2", "springno3");
    }
    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        dao.add(user1);
        dao.add(user2);
        assertEquals(dao.getCount(), 2);

        User userget1 = dao.get(user1.getId());
        assertEquals(userget1.getName(), user1.getName());
        assertEquals(userget1.getPassword(), user1.getPassword());

        User userget2 = dao.get(user2.getId());
        assertEquals(userget2.getName(), user2.getName());
        assertEquals(userget2.getPassword(), user2.getPassword());
    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {

        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        dao.add(user1);
        assertEquals(dao.getCount(), 1);

        dao.add(user2);
        assertEquals(dao.getCount(), 2);

        dao.add(user3);
        assertEquals(dao.getCount(), 3);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailuer() throws SQLException {
        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        dao.get("unknown_id");
    }
}
