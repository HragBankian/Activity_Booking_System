package UI.GuardianPages;

import OOP.Users.Guardian;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GuardianHome extends JFrame {
    private JLabel welcomeLabel;
    private static Guardian guardian;

    public GuardianHome(Guardian guardian) {
        this.guardian = guardian;
        String guardianFullName = guardian.getFullName();

        setTitle("Guardian Home");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 1, 10, 10));

        welcomeLabel = new JLabel("Welcome " + guardianFullName);
        add(welcomeLabel, BorderLayout.NORTH);

        JButton btnMakeBooking = new JButton("Make a Booking");
        JButton btnManageBookings = new JButton("Manage My Bookings");
        JButton btnManageMinors = new JButton("Manage Minors");

        // Add action listeners to buttons
        btnMakeBooking.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GuardianMakeBooking(guardian).setVisible(true);
            }
        });

        btnManageBookings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new GuardianManageBooking(guardian).setVisible(true);
            }
        });

        btnManageMinors.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ManageMinors(guardian).setVisible(true);
            }
        });

        add(btnMakeBooking);
        add(btnManageBookings);
        add(btnManageMinors);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuardianHome(guardian).setVisible(true));
    }
}
