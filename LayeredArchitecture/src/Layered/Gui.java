package Layered;


import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
public class Gui extends JFrame {
    private JTextField rollInput;
    private JTextArea outputArea;
    private JButton searchButton;

    public StudentCGPAApp() {
        setTitle("Student CGPA Calculator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(new JLabel("Roll Number:"));
        rollInput = new JTextField(15);
        topPanel.add(rollInput);
        searchButton = new JButton("Search");
        topPanel.add(searchButton);

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String rollNo = rollInput.getText().trim();
                if (!rollNo.isEmpty()) {
                    displayStudentData(rollNo);
                }
            }
        });
    }

}
