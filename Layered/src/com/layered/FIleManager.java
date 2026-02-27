package com.layered;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class FIleManager {
	
    public ArrayList<Double> getData(String rollNo) {
        File file = new File("/home/wahab/Downloads/"+rollNo + ".txt");
        if (!file.exists()) {
            System.out.println("No File Found");
            return null;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            ArrayList<Double> courseGpa = new ArrayList<>();
            int count = 0;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                	courseGpa.add(Double.parseDouble(parts[1].trim()));
                    count++;
                }
            }
            if (count > 0) {
            	return courseGpa;
            }

        } catch (Exception ex) {
        	System.out.println("Error reading data for roll number: "+rollNo);
        }
        return null;
    }
}
