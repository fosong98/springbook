package springbook.issuetracker.updatable;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import springbook.user.sqlservice.SqlNotFoundException;
import springbook.user.sqlservice.SqlUpdateFailureException;
import springbook.user.sqlservice.UpdatableSqlRegistry;

import javax.sql.DataSource;
import java.util.Map;

public class EmbeddedDbSqlRegistry implements UpdatableSqlRegistry {
    JdbcTemplate jdbc;

    public void setDataSource(DataSource dataSource) {
        jdbc = new JdbcTemplate(dataSource);
    }

    public void registerSql(String key, String sql) {
        jdbc.update("insert into sqlmap(key_, sql_) values (?, ?)", key, sql);
    }

    public String findSql(String key) throws SqlNotFoundException {
        try {
            return jdbc.queryForObject("select sql_ from sqlmap where key_=?", String.class, key);
        } catch (EmptyResultDataAccessException e) {
            throw new SqlNotFoundException(key + " is not found.", e);
        }
    }

    public void updateSql(String key, String sql) throws SqlUpdateFailureException {
        int affected = jdbc.update("update sqlmap set sql_ = ? where key_= ?", sql, key);
        if (affected == 0) {
            throw new SqlUpdateFailureException(key + " is not found.");
        }
    }

    public void updateSql(Map<String, String> sqlmap) throws SqlUpdateFailureException {
        for (Map.Entry<String, String> entry : sqlmap.entrySet()) {
            updateSql(entry.getKey(), entry.getValue());
        }
    }
}