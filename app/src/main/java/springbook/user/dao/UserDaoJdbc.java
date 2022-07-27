package springbook.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDaoJdbc implements UserDao {
    private RowMapper<User> userMapper =
            new RowMapper<User>() {
                @Override
                public User mapRow(ResultSet rs, int rowNum) throws SQLException {
                    User user = new User();
                    user.setId(rs.getString("id"));
                    user.setName(rs.getString("name"));
                    user.setPassword(rs.getString("password"));
                    user.setLevel(Level.valueOf(rs.getInt("level")));
                    user.setLogin(rs.getInt("login"));
                    user.setRecommend(rs.getInt("recommend"));
                    user.setEmail(rs.getString("email"));
                    return user;
                }
            };
    private JdbcTemplate jdbcTemplate;

    public UserDaoJdbc() {}


    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    public void add(final User user) {
        this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommend, email) values(?, ?, ?, ?, ?, ?, ?)",
                user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail()
        );
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject(
                "select * from users where id = ?",
                this.userMapper,
                id
        );
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query(
                "select * from users order by id",
                this.userMapper
        );
    }

    public void deleteAll() {
        jdbcTemplate.update("delete from users");
    }

    public int getCount() {
        // query 메소드를 이용하기 때문에 2개의 콜백을 작성
//        return this.jdbcTemplate.query(
//                new PreparedStatementCreator() {
//                   @Override
//                   public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//                       return con.prepareStatement("select count(*) from users");
//                   }
//                },
//                new ResultSetExtractor<Integer>() {
//                    @Override
//                    public Integer extractData(ResultSet rs) throws SQLException, DataAccessException {
//                        rs.next();
//                        return rs.getInt(1);
//                    }
//                });

        // 하나의 정수 값을 위한 queryForObject 메소드를 사용
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    @Override
    public void update(User user1) {
        this.jdbcTemplate.update(
                "update users set name = ?, password = ?, level = ?, login = ?, recommend = ?, email = ? where id = ?;",
                user1.getName(), user1.getPassword(), user1.getLevel().intValue(), user1.getLogin(), user1.getRecommend(), user1.getEmail(),
                user1.getId()
        );
    }
}
