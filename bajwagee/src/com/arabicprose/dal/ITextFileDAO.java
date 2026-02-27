package com.arabicprose.dal;

import java.io.IOException;


public interface ITextFileDAO {
    
   
    String readFileContent(String filePath) throws IOException;
   
    java.util.List<String> readFileLines(String filePath) throws IOException;
    
 
    boolean isValidFile(String filePath);
}