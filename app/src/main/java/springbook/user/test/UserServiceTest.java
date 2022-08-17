package springbook.user.test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import springbook.user.dao.MockUserDao;
import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.*;

import javax.sql.DataSource;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

import static org.mockito.Mockito.*;

import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/test-applicationContext.xml")
public class UserServiceTest {
    @Autowired
    ApplicationContext context;
    @Autowired
    UserDao dao;
    @Autowired
    UserService userService;
    @Autowired
    UserService testUserService;
    @Autowired
    DataSource testDataSource;
    @Autowired
    PlatformTransactionManager transactionManager;
    @Autowired
    MailSender mailSender;
    List<User> users;

    static class TestUserServiceImpl extends UserServiceImpl {
        private String id = "emadnite1";

        @Override
        protected void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) {
                throw new TestUserServiceException();
            }
            super.upgradeLevel(user);
        }

        @Override
        public List<User> getAll() {
            for (User user: super.getAll()) {
                super.update(user);
            }
            return null;
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
    public void mockUpgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        UserDao mockUserDao = mock(UserDao.class);
        when(mockUserDao.getAll()).thenReturn(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MailSender mockMailSender = mock(MailSender.class);
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();
        verify(mockUserDao, times(4)).update(any(User.class));
        verify(mockUserDao).update(users.get(1));
        assertEquals(users.get(1).getLevel(), Level.SILVER);
        verify(mockUserDao, times(2)).update(users.get(2));
        assertEquals(users.get(2).getLevel(), Level.GOLD);
        verify(mockUserDao).update(users.get(4));
        assertEquals(users.get(4).getLevel(), Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mockMailSender, times(4)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        assertEquals(users.get(1).getEmail(), mailMessages.get(0).getTo()[0]);
        assertEquals(users.get(2).getEmail(), mailMessages.get(1).getTo()[0]);
        assertEquals(users.get(2).getEmail(), mailMessages.get(2).getTo()[0]);
        assertEquals(users.get(4).getEmail(), mailMessages.get(3).getTo()[0]);
    }

    @Test
    public void upgradeLevels() throws Exception {
        UserServiceImpl userServiceImpl = new UserServiceImpl();

        MockUserDao mockUserDao = new MockUserDao(this.users);
        userServiceImpl.setUserDao(mockUserDao);

        MockMailSender mockMailSender = new MockMailSender();
        userServiceImpl.setMailSender(mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        assertEquals(updated.size(), 4);

        checkUserAndLevel(updated.get(0), "bjoytouch", Level.SILVER);
        checkUserAndLevel(updated.get(2), "cjjadoo", Level.GOLD);
        checkUserAndLevel(updated.get(3), "emadnite1", Level.GOLD);



        List<String> request = mockMailSender.getRequests();
        System.out.println(request);
        assertEquals(4, request.size());
        assertEquals(users.get(1).getEmail(), request.get(0));
        assertEquals(users.get(2).getEmail(), request.get(1));
        assertEquals(users.get(2).getEmail(), request.get(2));
        assertEquals(users.get(4).getEmail(), request.get(3));
    }

    @Test(expected = TransientDataAccessResourceException.class)
    public void readOnlyTransactionAttribute() {
        testUserService.deleteAll();
        for (User user : users) {
            testUserService.add(user);
        }
        testUserService.getAll();
    }

    @Test
    public void transactionSync() {
        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);

        userService.deleteAll();

        userService.add(users.get(0));
        userService.add(users.get(1));

        transactionManager.commit(txStatus);
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
    @DirtiesContext
    public void upgradeAllOrNothing() throws Exception {
        dao.deleteAll();
        for (User user: users) dao.add(user);

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

    private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
        assertEquals(expectedId, updated.getId());
        assertEquals(expectedLevel, updated.getLevel());
    }
}
