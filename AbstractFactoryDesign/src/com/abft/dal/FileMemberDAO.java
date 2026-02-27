package com.abft.dal;

import java.io.*;

public class FileMemberDAO implements IMemberDAO {
    private String filePath;

    public FileMemberDAO(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void addMember(Member member) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(member.getId() + "," + member.getName());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Member getMemberById(int id) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                int memberId = Integer.parseInt(parts[0]);
                if (memberId == id) {
                    return new Member(memberId, parts[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
