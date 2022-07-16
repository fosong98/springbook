package springbook.user.dao;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;

public class UserDao {
    public DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    public UserDao() {}

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.dataSource = dataSource;
    }


    public void add(final User user) throws SQLException {
        this.jdbcTemplate.update("insert into users(id, name, password) values(?, ?, ?)",
                user.getId(), user.getName(), user.getPassword()
        );
    }

    public User get(String id) throws SQLException {
        Connection c = dataSource.getConnection();

        PreparedStatement ps = c.prepareStatement(
                "SELECT * FROM users WHERE id = ?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        User user = null;

        if (rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }

        rs.close();
        ps.close();
        c.close();

        if (user == null) throw new EmptyResultDataAccessException(1);
        return user;
    }

    public void deleteAll() throws SQLException {
        jdbcTemplate.update("delete from users");
    }

    public int getCount() throws SQLException {
        try (
                Connection c = dataSource.getConnection();
                PreparedStatement ps = c.prepareStatement("select count(*) from users");
                ResultSet rs = ps.executeQuery();
        ) {
            rs.next();
            int count = rs.getInt(1);
            return count;
        }
    }
}
