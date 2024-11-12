package UI.ClientPages;

import OOP.*;
import OOP.Users.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ClientMakeBooking extends JFrame {
    private JTable offeringsTable;
    private DefaultTableModel offeringsTableModel;
    private JButton bookButton;
    private static Client client;

    public ClientMakeBooking(Client client) {
        this.client = client;

        setTitle("Client Make Booking");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Only close this window
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        offeringsTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Organization", "City", "Time", "Capacity", "Num Students"}, 0);
        offeringsTable = new JTable(offeringsTableModel);
        bookButton = new JButton("Book Offering");

        // Scroll pane for table
        JScrollPane offeringsScrollPane = new JScrollPane(offeringsTable);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(bookButton);

        // Adding components to the frame
        add(offeringsScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load offerings when the UI starts
        loadAvailableOfferings();

        // Book offering action
        bookButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                bookOffering();
            }
        });
    }

    private void loadAvailableOfferings() {
        // Clear the table before reloading data
        offeringsTableModel.setRowCount(0);

        ArrayList<Offering> availableOfferings = client.getAvailableOfferings();

        // Populate the table with available offerings
        for (Offering offering : availableOfferings) {
            String title = offering.getTitle();
            String organization = offering.getOrganization();
            String city = offering.getCity();
            String time = offering.getTime();
            int capacity = offering.getCapacity();
            int numStudents = offering.getNumStudents();
            int offeringId = offering.getId(); // Get the offering id

            offeringsTableModel.addRow(new Object[]{offeringId, title, organization, city, time, capacity, numStudents});
        }
    }

    private void bookOffering() {
        int selectedRow = offeringsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offering to book.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the offering ID
        Object offeringValue = offeringsTableModel.getValueAt(selectedRow, 0);
        int offeringId = (offeringValue instanceof Integer) ? (Integer) offeringValue : Integer.parseInt((String) offeringValue);

        boolean success = client.bookOffering(offeringId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Booking successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            client.getAvailableOfferings();  // Reload available offerings
        } else {
            JOptionPane.showMessageDialog(this, "Error booking the offering.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientMakeBooking(client).setVisible(true));
    }
}
