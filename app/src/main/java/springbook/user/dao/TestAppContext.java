package springbook.user.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.test.UserServiceTest;

@Configuration
public class TestAppContext {
    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }

    @Bean
    public UserService testUserService() {
        UserServiceTest.TestUserServiceImpl testService = new UserServiceTest.TestUserServiceImpl();
        return testService;
    }
}
