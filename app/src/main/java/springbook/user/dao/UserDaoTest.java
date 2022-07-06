package springbook.user.dao;

import org.apache.catalina.core.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import springbook.user.domain.User;

import java.sql.SQLException;

public class UserDaoTest {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        GenericApplicationContext context =
                new AnnotationConfigApplicationContext(DaoFactory.class);
        UserDao dao = context.getBean("userDao", UserDao.class);
        User user = new User();
        user.setId("Whiteship");
        user.setName("baekison");
        user.setPassword("married");

        //dao.add(user);

        System.out.println(user.getId() + " enroll success");

        User user2 = dao.get(user.getId());

        if (!user.getName().equals(user2.getName())) {
            System.out.println("테스트 실패 (name)");
        } else if (!user.getPassword().equals(user2.getPassword())) {
            System.out.println("테스트 실패 (password)");
        } else {
            System.out.println("search success");
        }
    }
}
