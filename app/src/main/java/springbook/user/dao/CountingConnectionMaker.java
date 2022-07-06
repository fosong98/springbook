package springbook.user.dao;

import java.sql.Connection;
import java.sql.SQLException;

public class CountingConnectionMaker implements ConnectionMaker {
    int count = 0;
    private ConnectionMaker realConnectionMaker;

    public CountingConnectionMaker(ConnectionMaker realConnectionMaker) {
        this.realConnectionMaker = realConnectionMaker;
    }
    @Override
    public Connection makeNewConnection() throws ClassNotFoundException, SQLException {
        this.count++;
        return realConnectionMaker.makeNewConnection();
    }

    public int getCount() {
        return this.count;
    }
}
