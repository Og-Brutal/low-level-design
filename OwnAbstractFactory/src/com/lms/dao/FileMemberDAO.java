package com.lms.dao;

import com.lms.dto.MemberDTO;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileMemberDAO implements IMemberDAO {

    // File beside the src folder
    private static final String FILE_PATH = "Member";

    @Override
    public MemberDTO getMemeber() {
        // Return the first member for simplicity
        List<MemberDTO> members = readAllMembers();
        return members.isEmpty() ? null : members.get(0);
    }

    // Optional: Reads all members from the file
    public List<MemberDTO> readAllMembers() {
        List<MemberDTO> members = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            String name = null;
            String bio = null;

            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue; // skip blank lines

                if (line.startsWith("name:")) {
                    name = line.substring("name:".length()).trim();
                } else if (line.startsWith("bio:")) {
                    bio = line.substring("bio:".length()).trim();
                }

                // When both fields found, create MemberDTO and reset
                if (name != null && bio != null) {
                    members.add(new MemberDTO(name, bio));
                    name = null;
                    bio = null;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return members;
    }
}
