package springbook.user.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import springbook.user.service.DummyMailSender;
import springbook.user.service.UserService;
import springbook.user.sqlservice.SqlServiceContext;
import springbook.user.test.UserServiceTest;

import javax.sql.DataSource;
import java.sql.Driver;


@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "springbook.user")
@Import(SqlServiceContext.class)
@PropertySource("classpath:database.properties")
public class AppContext {
    @Value("${db.driverClass}") Class<? extends Driver> driverClass;
    @Value("${db.url}") String url;
    @Value("${db.username}")
    String username;
    @Value("${db.password}")
    String password;
    @Autowired
    Environment env;

    @Autowired
    UserDao userDao;

    @Bean
    public static PropertySourcesPlaceholderConfigurer placeholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }





    @Bean
    public DataSource dataSource() {
        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();

        dataSource.setUrl(url);
        dataSource.setDriverClass(driverClass);
        dataSource.setUsername(username);
        dataSource.setPassword(password);

        return dataSource;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        DataSourceTransactionManager tm = new DataSourceTransactionManager();
        tm.setDataSource(dataSource());
        return tm;
    }

    @Configuration
    @Profile("production")
    public static class ProductionAppContext {
        @Bean
        public MailSender mailSender() {
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("localhost");
            return mailSender;
        }
    }

    @Configuration
    @Profile("test")
    public static class TestAppContext {
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
}
