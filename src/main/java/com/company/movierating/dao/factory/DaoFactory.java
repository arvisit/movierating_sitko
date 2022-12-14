package com.company.movierating.dao.factory;

import java.util.HashMap;
import java.util.Map;

import com.company.movierating.dao.BanDao;
import com.company.movierating.dao.FilmDao;
import com.company.movierating.dao.ReviewDao;
import com.company.movierating.dao.ScoreDao;
import com.company.movierating.dao.UserDao;
import com.company.movierating.dao.connection.DataSource;
import com.company.movierating.dao.impl.BanDaoImpl;
import com.company.movierating.dao.impl.FilmDaoImpl;
import com.company.movierating.dao.impl.ReviewDaoImpl;
import com.company.movierating.dao.impl.ScoreDaoImpl;
import com.company.movierating.dao.impl.UserDaoImpl;

public class DaoFactory {
    private final Map<Class<?>, Object> daos;

    private static class DaoFactoryHolder {
        public static final DaoFactory HOLDER_INSTANCE = new DaoFactory();
    }

    private DaoFactory() {
        daos = new HashMap<>();
        daos.put(UserDao.class, new UserDaoImpl(DataSource.getInstance()));
        daos.put(BanDao.class, new BanDaoImpl(DataSource.getInstance(), getDao(UserDao.class)));
        daos.put(FilmDao.class, new FilmDaoImpl(DataSource.getInstance()));
        daos.put(ScoreDao.class,
                new ScoreDaoImpl(DataSource.getInstance(), getDao(FilmDao.class), getDao(UserDao.class)));
        daos.put(ReviewDao.class,
                new ReviewDaoImpl(DataSource.getInstance(), getDao(FilmDao.class), getDao(UserDao.class)));
    }

    public static DaoFactory getInstance() {
        return DaoFactoryHolder.HOLDER_INSTANCE;
    }

    public <T> T getDao(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T dao = (T) daos.get(clazz);
        if (dao == null) {
            throw new IllegalArgumentException("Attempt to get DaoObject for unsupported class " + clazz);
        }
        return dao;
    }
}
