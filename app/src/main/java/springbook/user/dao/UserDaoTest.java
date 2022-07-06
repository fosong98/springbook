package springbook.user.dao;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class UserDaoTest {
    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        GenericApplicationContext context =
                new GenericXmlApplicationContext("applicationContext.xml");
        UserDao dao = context.getBean("setterUserDao", UserDao.class);

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        User user = new User();
        user.setId("gyumee");
        user.setName("park");
        user.setPassword("springno1");

        dao.add(user);
        assertThat(dao.getCount(), is(1));

        User user2 = dao.get(user.getId());


        assertThat(user2.getName(), is(user.getName()));
        assertThat(user2.getPassword(), is(user.getPassword()));
    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {
        ApplicationContext context = new GenericXmlApplicationContext(
                "applicationContext.xml");

        UserDao dao = context.getBean("userDao", UserDao.class);
        User user1 = new User("gyumee", "park", "springno1");
        User user2 = new User("leegw700", "Lee", "springno2");
        User user3 = new User("bumjin", "park2", "springno3");

        dao.deleteAll();
        assertEquals(dao.getCount(), 0);

        dao.add(user1);
        assertEquals(dao.getCount(), 1);

        dao.add(user2);
        assertEquals(dao.getCount(), 2);

        dao.add(user3);
        assertEquals(dao.getCount(), 3);
    }
}
