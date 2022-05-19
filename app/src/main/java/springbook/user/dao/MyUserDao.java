package springbook.user.dao;

import springbook.user.domain.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyUserDao extends UserDao {
    @Override
    public Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("org.mariadb.jdbc.Driver");
        Connection c = DriverManager.getConnection(UserDaoPrivateData.address, UserDaoPrivateData.name, UserDaoPrivateData.password);
        return c;
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        UserDao dao = new MyUserDao();

        User user = new User();
        user.setId("Whiteship");
        user.setName("baekison");
        user.setPassword("married");

        dao.add(user);

        System.out.println(user.getId() + "enroll success");

        User user2 = dao.get(user.getId());
        System.out.println(user2.getName());
        System.out.println(user2.getPassword());

        System.out.println(user2.getId() + " search success");
    }
}
