package UI;

import DB.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuardianHome extends JFrame {
    private JLabel welcomeLabel;
    private static int guardianId;

    public GuardianHome(int guardianId) {
        this.guardianId = guardianId;
        String name = DatabaseConnection.getUserNameById(guardianId, "Guardian");

        setTitle("Guardian Home");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1, 10, 10));

        welcomeLabel = new JLabel("Welcome " + name);
        add(welcomeLabel, BorderLayout.NORTH);

        JButton btnMakeBooking = new JButton("Make a Booking");
        JButton btnManageBookings = new JButton("Manage My Bookings");
        JButton btnManageMinors = new JButton("Manage Minors");

        // Add action listeners to buttons
        btnMakeBooking.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GuardianMakeBooking(guardianId).setVisible(true);
            }
        });

        btnManageBookings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GuardianManageBooking(guardianId).setVisible(true);
            }
        });

        btnManageMinors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageMinors(guardianId).setVisible(true);
            }
        });

        add(btnMakeBooking);
        add(btnManageBookings);
        add(btnManageMinors);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuardianHome(guardianId).setVisible(true));
    }
}
