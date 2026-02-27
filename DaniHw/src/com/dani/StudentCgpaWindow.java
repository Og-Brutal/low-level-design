package com.dani;

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

public class StudentCgpaWindow extends JFrame {
    private JTextField rollNumberField;
    private JTextArea resultArea;
    private JButton calculateButton;
    private CgpaCalculator cgpaCalculator;

    public StudentCgpaWindow(CgpaCalculator cgpaCalculator) {
        this.cgpaCalculator = cgpaCalculator;
        setTitle("Student CGPA Calculator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.add(new JLabel("Roll Number:"));
        rollNumberField = new JTextField(15);
        inputPanel.add(rollNumberField);
        calculateButton = new JButton("Calculate");
        inputPanel.add(calculateButton);

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(inputPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String rollNumber = rollNumberField.getText().trim();
                if (!rollNumber.isEmpty()) {
                    resultArea.setText(cgpaCalculator.calculateCgpaForStudent(rollNumber));
                }
            }
        });
    }
}
