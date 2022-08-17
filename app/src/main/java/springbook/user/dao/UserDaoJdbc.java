package springbook.user.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDaoJdbc implements UserDao {
    private String sqlAdd;
    private String sqlGet;
    private String sqlGetAll;
    private String sqlDeleteAll;
    private String sqlGetCount;
    private String sqlUpdate;

    public void setSqlAdd(String sqlAdd) {
        this.sqlAdd = sqlAdd;
    }

    public void setSqlGet(String sqlGet) {
        this.sqlGet = sqlGet;
    }

    public void setSqlGetAll(String sqlGetAll) {
        this.sqlGetAll = sqlGetAll;
    }

    public void setSqlDeleteAll(String sqlDeleteAll) {
        this.sqlDeleteAll = sqlDeleteAll;
    }

    public void setSqlGetCount(String sqlGetCount) {
        this.sqlGetCount = sqlGetCount;
    }

    public void setSqlUpdate(String sqlUpdate) {
        this.sqlUpdate = sqlUpdate;
    }

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
        this.jdbcTemplate.update(this.sqlAdd,
                user.getId(), user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail()
        );
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject(
                this.sqlGet,
                this.userMapper,
                id
        );
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query(
                this.sqlGetAll,
                this.userMapper
        );
    }

    public void deleteAll() {
        jdbcTemplate.update(this.sqlDeleteAll);
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
        return this.jdbcTemplate.queryForObject(this.sqlGetCount, Integer.class);
    }

    @Override
    public void update(User user1) {
        this.jdbcTemplate.update(
                this.sqlUpdate,
                user1.getName(), user1.getPassword(), user1.getLevel().intValue(), user1.getLogin(), user1.getRecommend(), user1.getEmail(),
                user1.getId()
        );
    }
}
