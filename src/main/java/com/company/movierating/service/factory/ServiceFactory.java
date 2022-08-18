package com.company.movierating.service.factory;

import java.util.HashMap;
import java.util.Map;

import com.company.movierating.dao.BanDao;
import com.company.movierating.dao.UserDao;
import com.company.movierating.dao.factory.DaoFactory;
import com.company.movierating.service.BanService;
import com.company.movierating.service.UserService;
import com.company.movierating.service.converter.factory.ConverterFactory;
import com.company.movierating.service.converter.impl.BanConverter;
import com.company.movierating.service.converter.impl.UserConverter;
import com.company.movierating.service.impl.BanServiceImpl;
import com.company.movierating.service.impl.UserServiceImpl;
import com.company.movierating.service.util.BanValidator;
import com.company.movierating.service.util.UserValidator;

public class ServiceFactory {
    private final Map<Class<?>, Object> services;

    private static class ServiceFactoryHolder {
        public static final ServiceFactory HOLDER_INSTANCE = new ServiceFactory();
    }

    private ServiceFactory() {
        services = new HashMap<>();
        services.put(UserService.class, new UserServiceImpl(DaoFactory.getInstance().getDao(UserDao.class),
                ConverterFactory.getInstance().getConverter(UserConverter.class), UserValidator.INSTANCE));
        services.put(BanService.class, new BanServiceImpl(DaoFactory.getInstance().getDao(BanDao.class),
                ConverterFactory.getInstance().getConverter(BanConverter.class), BanValidator.INSTANCE));
    }

    public static ServiceFactory getInstance() {
        return ServiceFactoryHolder.HOLDER_INSTANCE;
    }

    public <T> T getService(Class<T> clazz) {
        @SuppressWarnings("unchecked")
        T service = (T) services.get(clazz);
        if (service == null) {
            throw new IllegalArgumentException("Attempt to get ServiceObject for unsupported class " + clazz);
        }
        return service;
    }
}
