package springbook.user.service;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.List;

public class UserService {
    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        for (User user : users) {
            while (canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        }
    }

    public void add(User user) {
        if (user.getLevel() == null) user.setLevel(Level.BASIC);
        userDao.add(user);
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        return switch (currentLevel) {
            case BASIC -> (user.getLogin() >= 50);
            case SILVER -> (user.getRecommend() >= 30);
            case GOLD -> false;
            default -> throw new IllegalArgumentException("Unknown Level: "+ currentLevel);
        };
    }

    private void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }
}