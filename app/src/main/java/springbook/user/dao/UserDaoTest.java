package springbook.user.dao;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.sql.SQLException;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppContext.class)
@ActiveProfiles("test")
@DirtiesContext
public class UserDaoTest {
    @Autowired
    private DefaultListableBeanFactory bf;

    @Autowired
    private ApplicationContext context;
    @Autowired
    private UserDao userDao;
    private User user1;
    private User user2;
    private User user3;
    @Before
    public void setUp() {
        user1 = new User("gyumee", "park", "springno1", Level.BASIC, 1, 0, "gyumee@pusan.ac.kr");
        user2 = new User("leegw700", "Lee", "springno2", Level.SILVER, 55, 10, "leegw700@test.com");
        user3 = new User("bumjin", "park2", "springno3", Level.GOLD, 100, 40, "bumjin@gmail.com");

    }
    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        userDao.deleteAll();
        assertEquals(userDao.getCount(), 0);

        userDao.add(user1);
        userDao.add(user2);
        assertEquals(userDao.getCount(), 2);

        User userget1 = userDao.get(user1.getId());
        checkSameUser(user1, userget1);

        User userget2 = userDao.get(user2.getId());
        checkSameUser(user2, userget2);
    }

    @Test
    public void count() throws SQLException, ClassNotFoundException {

        userDao.deleteAll();
        assertEquals(userDao.getCount(), 0);

        userDao.add(user1);
        assertEquals(userDao.getCount(), 1);

        userDao.add(user2);
        assertEquals(userDao.getCount(), 2);

        userDao.add(user3);
        assertEquals(userDao.getCount(), 3);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailuer() throws SQLException {
        userDao.deleteAll();
        assertEquals(userDao.getCount(), 0);

        userDao.get("unknown_id");
    }

    @Test
    public void getAll() {
        userDao.deleteAll();

        List<User> user0 = userDao.getAll();
        assertEquals(user0.size(), 0);

        userDao.add(user1);
        List<User> users1 = userDao.getAll();
        assertEquals(users1.size(), 1);
        checkSameUser(user1, users1.get(0));

        userDao.add(user2);
        List<User> users2 = userDao.getAll();
        assertEquals(users2.size(), 2);
        checkSameUser(user1, users2.get(0));
        checkSameUser(user2, users2.get(1));

        userDao.add(user3);
        List<User> users3 = userDao.getAll();
        assertEquals(users3.size(), 3);
        checkSameUser(user3, users3.get(0));
        checkSameUser(user1, users3.get(1));
        checkSameUser(user2, users3.get(2));
    }

    @Test
    public void update() {
        userDao.deleteAll();

        userDao.add(user1);
        userDao.add(user2);

        user1.setName("Ouh");
        user1.setPassword("springno6");
        user1.setLevel(Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        user1.setEmail("Ouh@gmail.com");
        userDao.update(user1);

        User user1update = userDao.get(user1.getId());
        checkSameUser(user1, user1update);
        User user2update = userDao.get(user2.getId());
        checkSameUser(user2, user2update);
    }

    @Test(expected = DuplicateKeyException.class)
    public void duplicateKey() {
        userDao.deleteAll();

        userDao.add(user1);
        userDao.add(user1);
    }

    private void checkSameUser(User user1, User user2) {
        assertEquals(user1.getId(), user2.getId());
        assertEquals(user1.getName(), user2.getName());
        assertEquals(user1.getPassword(), user2.getPassword());
        assertEquals(user1.getLevel(), user2.getLevel());
        assertEquals(user1.getLogin(), user2.getLogin());
        assertEquals(user1.getRecommend(), user2.getRecommend());
        assertEquals(user1.getEmail(), user2.getEmail());
    }

    @Test
    public void beans() {
        for (String n : bf.getBeanDefinitionNames()) {
            System.out.println(n + "\t\t" + bf.getBean(n).getClass().getName());
        }
    }
}
