package springbook.user.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.UserService;
import javax.sql.DataSource;
import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECOMMEND_FOR_GOLD;


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
    @Autowired
    DataSource testDataSource;
    @Autowired
    PlatformTransactionManager transactionManager;
    List<User> users;

    class TestUserService extends UserService {
        private String id;

        private TestUserService(String id) {
            this.id = id;
        }

        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            super.upgradeLevel(user);
        }
    }

    static class TestUserServiceException extends RuntimeException {
    }

    @Before
    public void setUp() {
        users = Arrays.asList(
                new User("bujin", "bum", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "a"),
                new User("joytouch", "kkang", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "b"),
                new User("jjadoo", "song", "pick6", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, MIN_RECOMMEND_FOR_GOLD, "c"),
                new User("erwins", "shin", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1, "d"),
                new User("madnite1", "lee", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "e"),
                new User("green", "oh", "p5", Level.GOLD, Integer.MAX_VALUE, Integer.MAX_VALUE, "f")
        );
    }
    @Test
    public void bean() {
        assertNotNull(this.service);
    }

    @Test
    public void upgradeLevels() throws Exception {
        dao.deleteAll();
        for (User user: users) dao.add(user);

        service.upgradeLevels();

        checkLevelUpgraded(users.get(0), 0);
        checkLevelUpgraded(users.get(1), 1);
        checkLevelUpgraded(users.get(2), 2);
        checkLevelUpgraded(users.get(3), 0);
        checkLevelUpgraded(users.get(4), 1);
        checkLevelUpgraded(users.get(5), 0);
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

    @Test
    public void upgradeAllOrNothing() throws Exception {
        UserService testUserService = new TestUserService(users.get(4).getId());
        testUserService.setUserDao(this.dao);
        testUserService.setTransactionManager(transactionManager);

        dao.deleteAll();
        for (User user : users) dao.add(user);

        try {
            testUserService.upgradeLevels();
            fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {
        }

        checkLevelUpgraded(users.get(1), 0);
    }

    private void checkLevelUpgraded(User user, int difference) {
        User userUpdate = dao.get(user.getId());
        assertEquals(Level.subtract(userUpdate.getLevel(), user.getLevel()), difference);
    }
}
