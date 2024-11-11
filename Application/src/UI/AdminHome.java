package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminHome extends JFrame {

    public AdminHome() {
        setTitle("Admin Home");
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(5, 1, 10, 10)); // Set layout with 5 rows, 1 column

        // Create buttons for each CRUD operation
        JButton btnOfferingsCRUD = new JButton("Offerings");
        JButton btnBookingsCRUD = new JButton("Bookings");
        JButton btnClientsCRUD = new JButton("Clients");
        JButton btnGuardiansCRUD = new JButton("Guardians");
        JButton btnInstructorsCRUD = new JButton("Instructors");

        // Add action listeners for buttons
        btnOfferingsCRUD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new OfferingsCRUD().setVisible(true); // Open Offerings CRUD window
            }
        });

        btnBookingsCRUD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //new BookingsCRUD().setVisible(true); // Open Bookings CRUD window
            }
        });

        btnClientsCRUD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientsCRUD().setVisible(true); // Open Clients CRUD window
            }
        });

        btnGuardiansCRUD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //new GuardiansCRUD().setVisible(true); // Open Guardians CRUD window
            }
        });

        btnInstructorsCRUD.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //new InstructorsCRUD().setVisible(true); // Open Instructors CRUD window
            }
        });

        // Add buttons to the frame
        add(btnOfferingsCRUD);
        add(btnBookingsCRUD);
        add(btnClientsCRUD);
        add(btnGuardiansCRUD);
        add(btnInstructorsCRUD);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminHome().setVisible(true); // Show the Admin Home page
        });
    }
}
