package UI;

import DB.DatabaseConnection;
import DB.MinorBooking;
import DB.Offering;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GuardianManageBooking extends JFrame {
    private JTable minorBookingsTable;
    private DefaultTableModel minorBookingsTableModel;
    private JButton cancelBookingButton;
    private static int guardianId;

    public GuardianManageBooking(int guardianId) {
        this.guardianId = guardianId;

        setTitle("Guardian Manage Booking");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        minorBookingsTableModel = new DefaultTableModel(new Object[]{"Booking Id", "Offering Id", "Minor Name", "Title", "Organization", "City", "Time", "Capacity", "Num Students"}, 0);
        minorBookingsTable = new JTable(minorBookingsTableModel);
        cancelBookingButton = new JButton("Cancel Booking");

        // Scroll pane for table
        JScrollPane minorBookingsScrollPane = new JScrollPane(minorBookingsTable);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelBookingButton);

        // Adding components to the frame
        add(minorBookingsScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load the guardian's minors' bookings when the UI starts
        loadGuardianBookings();

        // Cancel booking action
        cancelBookingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBooking();
            }
        });
    }

    private void loadGuardianBookings() {
        // Clear the table before reloading data
        minorBookingsTableModel.setRowCount(0);

        // Get all minors for the guardian
        ArrayList<Integer> minorIds = DatabaseConnection.getMinorIdsForGuardian(guardianId);

        for (int minorId : minorIds) {
            // Get the minor's full name based on minorId
            String minorFullName = DatabaseConnection.getMinorFullName(minorId);

            // Get bookings for each minor
            ArrayList<MinorBooking> minorBookings = DatabaseConnection.getMinorBookingsForGuardian(minorId);

            // Populate the table with minor bookings and corresponding offering details
            for (MinorBooking booking : minorBookings) {
                int offeringId = booking.getOfferingId();
                Offering offering = DatabaseConnection.getOfferingById(offeringId);

                String title = offering.getTitle();
                String organization = offering.getOrganization();
                String city = offering.getCity();
                String time = offering.getTime();
                int capacity = offering.getCapacity();
                int numStudents = offering.getNumStudents();

                // Add the booking details and minor full name to the table model
                minorBookingsTableModel.addRow(new Object[]{
                        booking.getId(), offeringId, minorFullName, title, organization, city, time, capacity, numStudents
                });
            }
        }
    }


    private void cancelBooking() {
        int selectedRow = minorBookingsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a booking to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the booking ID from the first column (Booking ID column)
        Object bookingValue = minorBookingsTableModel.getValueAt(selectedRow, 0);
        int bookingId = (bookingValue instanceof Integer) ? (Integer) bookingValue : Integer.parseInt((String) bookingValue);
        Object offeringValue = minorBookingsTableModel.getValueAt(selectedRow, 1);
        int offeringId = (offeringValue instanceof Integer) ? (Integer) offeringValue : Integer.parseInt((String) offeringValue);

        // Cancel the booking by removing the MinorBooking record and decrementing the num_students of the offering
        boolean success = DatabaseConnection.cancelMinorBooking(bookingId, offeringId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Booking canceled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadGuardianBookings();  // Reload the guardian's bookings
        } else {
            JOptionPane.showMessageDialog(this, "Error canceling the booking.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuardianManageBooking(guardianId).setVisible(true)); // Example with guardianId = 1
    }
}
