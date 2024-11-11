package UI;

import DB.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientHome extends JFrame {
    private JLabel welcomeLabel;
    private static int clientId;

    public ClientHome(int clientId) {
        this.clientId = clientId;
        String name = DatabaseConnection.getUserNameById(clientId, "Client");

        setTitle("Client Home");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 10, 10));

        welcomeLabel = new JLabel("Welcome " + name);
        //welcomeLabel.setFont(new Font("Serif", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

        JButton btnMakeBooking = new JButton("Make a Booking");
        JButton btnManageBookings = new JButton("Manage My Bookings");

        btnMakeBooking.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientMakeBooking(clientId).setVisible(true);
            }
        });

        btnManageBookings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientManageBooking(clientId).setVisible(true);
            }
        });

        add(btnMakeBooking);
        add(btnManageBookings);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientHome(clientId).setVisible(true));
    }
}