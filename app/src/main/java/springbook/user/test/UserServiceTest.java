package springbook.user.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.MockMailSender;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.UserServiceTx;

import javax.sql.DataSource;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;


import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {
    @Autowired
    UserDao dao;
    @Autowired
    UserService userService;
    @Autowired
    UserServiceImpl userServiceImpl;
    @Autowired
    DataSource testDataSource;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    MailSender mailSender;
    List<User> users;

    class TestUserService extends UserServiceImpl {
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
                new User("abujin", "bum", "p1", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER - 1, 0, "a"),
                new User("bjoytouch", "kkang", "p2", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "b"),
                new User("cjjadoo", "song", "pick6", Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, MIN_RECOMMEND_FOR_GOLD, "c"),
                new User("derwins", "shin", "p3", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD - 1, "d"),
                new User("emadnite1", "lee", "p4", Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "e"),
                new User("fgreen", "oh", "p5", Level.GOLD, Integer.MAX_VALUE, Integer.MAX_VALUE, "f")
        );
    }
    @Test
    public void bean() {
        assertNotNull(this.userService);
    }

    @Test
    @DirtiesContext
    public void upgradeLevels() throws Exception {
        dao.deleteAll();
        for (User user: users) dao.add(user);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        checkLevelUpgraded(users.get(0), 0);
        checkLevelUpgraded(users.get(1), 1);
        checkLevelUpgraded(users.get(2), 2);
        checkLevelUpgraded(users.get(3), 0);
        checkLevelUpgraded(users.get(4), 1);
        checkLevelUpgraded(users.get(5), 0);

        List<String> request = mockMailSender.getRequests();
        System.out.println(request);
        assertEquals(4, request.size());
        assertEquals(users.get(1).getEmail(), request.get(0));
        assertEquals(users.get(2).getEmail(), request.get(1));
        assertEquals(users.get(2).getEmail(), request.get(2));
        assertEquals(users.get(4).getEmail(), request.get(3));
    }

    @Test
    public void add() {
        dao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = dao.get(userWithLevel.getId());
        User userWithoutLevelRead = dao.get(userWithoutLevel.getId());

        assertEquals(userWithLevel.getLevel(), userWithLevelRead.getLevel());
        assertEquals(userWithoutLevel.getLevel(), Level.BASIC);
    }

    @Test
    public void upgradeAllOrNothing() throws Exception {
        TestUserService testUserService = new TestUserService(users.get(4).getId());
        testUserService.setUserDao(this.dao);
        testUserService.setMailSender(mailSender);

        UserServiceTx txUserService = new UserServiceTx();
        txUserService.setTransactionManager(this.transactionManager);
        txUserService.setUserService(testUserService);

        dao.deleteAll();
        for (User user : users) dao.add(user);

        try {
            txUserService.upgradeLevels();
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
