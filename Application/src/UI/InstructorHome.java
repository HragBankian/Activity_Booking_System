package UI;

import DB.DatabaseConnection;
import DB.Instructor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InstructorHome extends JFrame {
    private JLabel welcomeLabel;
    private static Instructor instructor;

    public InstructorHome(Instructor instructor) {
        this.instructor = instructor;
        String instructorFullName = instructor.getFullName();

        setTitle("Instructor Home");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 10, 10));

        welcomeLabel = new JLabel("Welcome " + instructorFullName);
        add(welcomeLabel, BorderLayout.NORTH);

        JButton btnSelectOffering = new JButton("Select An Offering");
        JButton btnManageLessons = new JButton("Manage My Lessons");

        // Add action listeners to buttons
        btnSelectOffering.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new SelectOffering(instructor).setVisible(true);
            }
        });

        btnManageLessons.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageLessons(instructor).setVisible(true);
            }
        });

        add(btnSelectOffering);
        add(btnManageLessons);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InstructorHome(instructor).setVisible(true));
    }
}
