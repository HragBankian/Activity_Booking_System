package UI.ClientPages;

import OOP.Users.Client;
import OOP.ClientBooking;
import OOP.Offering;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ClientManageBooking extends JFrame {
    private JTable clientBookingsTable;
    private DefaultTableModel clientBookingsTableModel;
    private JButton cancelBookingButton;
    private static Client client;

    public ClientManageBooking(Client client) {
        this.client = client;

        setTitle("Client Manage Booking");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        clientBookingsTableModel = new DefaultTableModel(new Object[]{"Booking ID", "Title", "Organization", "City", "Time", "Capacity", "Num Students"}, 0);
        clientBookingsTable = new JTable(clientBookingsTableModel);
        cancelBookingButton = new JButton("Cancel Booking");

        // Scroll pane for table
        JScrollPane clientBookingsScrollPane = new JScrollPane(clientBookingsTable);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelBookingButton);

        // Adding components to the frame
        add(clientBookingsScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load client's bookings when the UI starts
        loadClientBookings();

        // Cancel booking action
        cancelBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBooking();
            }
        });
    }

    private void loadClientBookings() {
        // Clear the table before reloading data
        clientBookingsTableModel.setRowCount(0);

        ArrayList<ClientBooking> clientBookings = client.getBookings();

        // Populate the table with the client bookings and corresponding offering details
        for (ClientBooking booking : clientBookings) {
            int offeringId = booking.getOfferingId();
            Offering offering = Offering.getOfferingById(offeringId);

            String title = offering.getTitle();
            String organization = offering.getOrganization();
            String city = offering.getCity();
            String time = offering.getTime();
            int capacity = offering.getCapacity();
            int numStudents = offering.getNumStudents();

            clientBookingsTableModel.addRow(new Object[]{booking.getId(), title, organization, city, time, capacity, numStudents});
        }
    }

    private void cancelBooking() {
        int selectedRow = clientBookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the booking ID from the first column (Booking ID column)
        Object bookingValue = clientBookingsTableModel.getValueAt(selectedRow, 0);
        int bookingId = (bookingValue instanceof Integer) ? (Integer) bookingValue : Integer.parseInt((String) bookingValue);

        // Cancel the booking by removing the ClientBooking record and decrementing the num_students of the offering
        boolean success = client.cancelBooking(bookingId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Booking canceled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadClientBookings();  // Reload the client's bookings
        } else {
            JOptionPane.showMessageDialog(this, "Error canceling the booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientManageBooking(client).setVisible(true)); // Example with clientId = 1
    }
}
