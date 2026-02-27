package com.lms.dal;

import java.io.FileInputStream;
import java.util.Properties;

public abstract class AbstractDAOFactory implements IDAOFactory {

    private static IDAOFactory instance = null;

    public static IDAOFactory getInstance() {
        
        return instance;
    }
}
