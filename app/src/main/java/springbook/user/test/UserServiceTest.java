package springbook.user.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.UserService;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {
    @Autowired
    UserDao dao;
    @Autowired
    UserService service;
    List<User> users;

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("bujin", "bum", "p1", Level.BASIC, 49, 0),
                new User("joytouch", "kkang", "p2", Level.BASIC, 50, 0),
                new User("jjadoo", "song", "pick6", Level.BASIC, 50, 30),
                new User("erwins", "shin", "p3", Level.SILVER, 60, 29),
                new User("madnite1", "lee", "p4", Level.SILVER, 60, 30),
                new User("green", "oh", "p5", Level.GOLD, 100, 100)
        );
    }
    @Test
    public void bean() {
        assertNotNull(this.service);
    }

    @Test
    public void upgradeLevels() {
        dao.deleteAll();
        for (User user: users) dao.add(user);

        service.upgradeLevels();

        checkLevel(users.get(0), Level.BASIC);
        checkLevel(users.get(1), Level.SILVER);
        checkLevel(users.get(2), Level.GOLD);
        checkLevel(users.get(3), Level.SILVER);
        checkLevel(users.get(4), Level.GOLD);
        checkLevel(users.get(5), Level.GOLD);
    }

    @Test
    public void add() {
        dao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        service.add(userWithLevel);
        service.add(userWithoutLevel);

        User userWithLevelRead = dao.get(userWithLevel.getId());
        User userWithoutLevelRead = dao.get(userWithoutLevel.getId());

        assertEquals(userWithLevel.getLevel(), userWithLevelRead.getLevel());
        assertEquals(userWithoutLevel.getLevel(), Level.BASIC);
    }

    private void checkLevel(User user, Level expectedLevel) {
        User userUpdate = dao.get(user.getId());
        assertEquals(userUpdate.getLevel(), expectedLevel);
    }
}
