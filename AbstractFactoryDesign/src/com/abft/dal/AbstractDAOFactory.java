package com.abft.dal;

import java.io.FileInputStream;
import java.util.Properties;

public abstract class AbstractDAOFactory implements IDAOFactory {

    private static IDAOFactory instance = null;

    public static IDAOFactory getInstance() {
        if (instance == null) {
            try (FileInputStream input = new FileInputStream("config.properties")) {
                Properties prop = new Properties();
                prop.load(input);
                String className = prop.getProperty("dal.factory");
                Class<?> clazz = Class.forName(className);
                instance = (IDAOFactory) clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return instance;
    }
}
