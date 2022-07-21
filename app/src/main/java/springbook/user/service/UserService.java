package springbook.user.service;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;

import java.util.List;

public class UserService {
    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;
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
            case BASIC -> (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER -> (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD -> false;
            default -> throw new IllegalArgumentException("Unknown Level: "+ currentLevel);
        };
    }

    private void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }
}
