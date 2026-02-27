package com.dani;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class StudentDataReader {

    public ArrayList<Double> readGpaData(String rollNumber) {
        File file = new File("/home/wahab/Downloads/" + rollNumber + ".txt");
        if (!file.exists()) {
            System.out.println("No file found for roll number: " + rollNumber);
            return null;
        }
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
            String line;
            ArrayList<Double> gpaList = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    gpaList.add(Double.parseDouble(parts[1].trim()));
                }
            }
            if (!gpaList.isEmpty()) {
                return gpaList;
            }

        } catch (Exception ex) {
            System.out.println("Error reading GPA data for roll number: " + rollNumber);
        }
        return null;
    }
}
