package springbook.user.dao;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;
import org.junit.Test;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertThrows;

public class UserDaoTest {
    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        GenericApplicationContext context =
                new GenericXmlApplicationContext("applicationContext.xml");
        UserDao dao = context.getBean("setterUserDao", UserDao.class);
        User user = new User();
        user.setId("gyumee");
        user.setName("park");
        user.setPassword("springno1");

        dao.add(user);

        User user2 = dao.get(user.getId());


        assertThat(user2.getName(), is(user.getName()));
        assertThat(user2.getPassword(), is(user.getPassword()));
    }
}
