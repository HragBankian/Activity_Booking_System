package UI.AdminPages;

import OOP.Admin;
import OOP.ClientBooking;
import OOP.MinorBooking;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class BookingsRD extends JFrame {
    private JTable clientBookingsTable, minorBookingsTable;
    private DefaultTableModel clientBookingsTableModel, minorBookingsTableModel;
    private JButton deleteClientBookingButton, deleteMinorBookingButton;

    public BookingsRD() {
        setTitle("Bookings CRUD");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Only close this window
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1));  // Use GridLayout for better layout control

        // Initialize components
        clientBookingsTableModel = new DefaultTableModel(new Object[]{"ID", "Offering ID", "Client ID"}, 0){
            public boolean isCellEditable(int row, int column) {
                return false;  // Disable editing for all cells
            }
        };
        clientBookingsTable = new JTable(clientBookingsTableModel);

        minorBookingsTableModel = new DefaultTableModel(new Object[]{"ID", "Offering ID", "Minor ID"}, 0);
        minorBookingsTable = new JTable(minorBookingsTableModel);

        deleteClientBookingButton = new JButton("Delete Client Booking");
        deleteMinorBookingButton = new JButton("Delete Minor Booking");

        // Scroll panes for tables
        JScrollPane clientBookingsScrollPane = new JScrollPane(clientBookingsTable);
        JScrollPane minorBookingsScrollPane = new JScrollPane(minorBookingsTable);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteClientBookingButton);
        buttonPanel.add(deleteMinorBookingButton);

        // Adding components to the frame
        add(clientBookingsScrollPane);
        add(minorBookingsScrollPane);
        add(buttonPanel);

        // Load bookings when the UI starts
        loadClientBookings();
        loadMinorBookings();

        // Delete client booking action
        deleteClientBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteClientBooking();
            }
        });

        // Delete minor booking action
        deleteMinorBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMinorBooking();
            }
        });
    }

    private void loadClientBookings() {
        // Clear the table before reloading data
        clientBookingsTableModel.setRowCount(0);

        ArrayList<ClientBooking> clientBookings = Admin.getClientBookings();

        // Populate the table with client booking data
        for (ClientBooking booking : clientBookings) {
            int id = booking.getId();
            int offeringId = booking.getOfferingId();
            int clientId = booking.getClientId();

            clientBookingsTableModel.addRow(new Object[]{id, offeringId, clientId});
        }
    }

    private void deleteClientBooking() {
        int selectedRow = clientBookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client booking to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingId = (int) clientBookingsTableModel.getValueAt(selectedRow, 0);
        boolean success = Admin.deleteClientBooking(bookingId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Client booking deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadClientBookings(); // Refresh the table
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting client booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMinorBookings() {
        // Clear the table before reloading data
        minorBookingsTableModel.setRowCount(0);

        ArrayList<MinorBooking> minorBookings = Admin.getMinorBookings();

        // Populate the table with minor booking data
        for (MinorBooking booking : minorBookings) {
            int id = booking.getId();
            int offeringId = booking.getOfferingId();
            int minorId = booking.getMinorId();

            minorBookingsTableModel.addRow(new Object[]{id, offeringId, minorId});
        }
    }

    private void deleteMinorBooking() {
        int selectedRow = minorBookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a minor booking to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int bookingId = (int) minorBookingsTableModel.getValueAt(selectedRow, 0);
        boolean success = Admin.deleteMinorBooking(bookingId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Minor booking deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMinorBookings(); // Refresh the table
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting minor booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BookingsRD().setVisible(true));
    }
}
