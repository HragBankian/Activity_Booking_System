package UI;


import DB.Client;
import DB.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ClientHome extends JFrame {
    private JLabel welcomeLabel;
    private static Client client;

    public ClientHome(Client client) {
        this.client = client;
        String clientFullName = client.getFullName();

        setTitle("Client Home");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 1, 10, 10));

        welcomeLabel = new JLabel("Welcome " + clientFullName);
        //welcomeLabel.setFont(new Font("Serif", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

        JButton btnMakeBooking = new JButton("Make a Booking");
        JButton btnManageBookings = new JButton("Manage My Bookings");

        btnMakeBooking.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientMakeBooking(client).setVisible(true);
            }
        });

        btnManageBookings.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ClientManageBooking(client).setVisible(true);
            }
        });

        add(btnMakeBooking);
        add(btnManageBookings);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientHome(client).setVisible(true));
    }
}
