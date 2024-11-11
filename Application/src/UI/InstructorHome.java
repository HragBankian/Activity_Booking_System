package UI;

import DB.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InstructorHome extends JFrame {
    private JLabel welcomeLabel;
    private static int instructorId;

    public InstructorHome(int instructorId) {
        this.instructorId = instructorId;
        String name = DatabaseConnection.getUserNameById(instructorId, "Instructor");

        setTitle("Instructor Home");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 10, 10));

        welcomeLabel = new JLabel("Welcome " + name);
        add(welcomeLabel, BorderLayout.NORTH);

        JButton btnSelectOffering = new JButton("Select An Offering");
        JButton btnManageLessons = new JButton("Manage My Lessons");

        // Add action listeners to buttons
        btnSelectOffering.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SelectOffering(instructorId).setVisible(true);
            }
        });

        btnManageLessons.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageLessons(instructorId).setVisible(true);
            }
        });

        add(btnSelectOffering);
        add(btnManageLessons);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InstructorHome(instructorId).setVisible(true));
    }
}
