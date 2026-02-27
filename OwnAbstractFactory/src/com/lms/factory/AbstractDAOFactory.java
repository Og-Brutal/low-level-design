package com.lms.factory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

public abstract class AbstractDAOFactory implements IDAOFactory{
	private static IDAOFactory instance=null;
	public static final IDAOFactory getInstance() throws FileNotFoundException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		if(instance==null) {
			String factoryClassName=null;
			
			try(FileInputStream input = new FileInputStream("config.properties")){
				Properties prop=new Properties();
				
				
				prop.load(input);
				
				factoryClassName=prop.getProperty("dal.factory");
				
				Class<?> clazz=Class.forName(factoryClassName);
				
				instance = (IDAOFactory) clazz.getDeclaredConstructor().newInstance();
			}
			
		}
		return instance;
	}
}
