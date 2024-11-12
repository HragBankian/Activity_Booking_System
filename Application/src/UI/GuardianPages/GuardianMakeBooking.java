package UI.GuardianPages;

import OOP.Users.Guardian;
import OOP.Minor;
import OOP.Offering;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;

public class GuardianMakeBooking extends JFrame {
    private JTable offeringsTable;
    private DefaultTableModel offeringsTableModel;
    private JButton bookButton;
    private ButtonGroup minorsButtonGroup;
    private JPanel minorsPanel;
    private static Guardian guardian;

    public GuardianMakeBooking(Guardian guardian) {
        this.guardian = guardian;

        setTitle("Guardian Make Booking");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Only close this window
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components with the new Location column
        offeringsTableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Organization", "City", "Time", "Capacity", "Num Students", "Location"}, 0);
        offeringsTable = new JTable(offeringsTableModel);
        bookButton = new JButton("Book Offering");

        // Panel for minors selection
        minorsPanel = new JPanel();
        minorsPanel.setLayout(new BoxLayout(minorsPanel, BoxLayout.Y_AXIS));
        loadMinors();

        // Scroll pane for table
        JScrollPane offeringsScrollPane = new JScrollPane(offeringsTable);

        // Panel for buttons and minors selection
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(minorsPanel, BorderLayout.NORTH);
        buttonPanel.add(bookButton, BorderLayout.SOUTH);

        // Adding components to the frame
        add(offeringsScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);

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

    private void loadMinors() {
        minorsPanel.removeAll();
        minorsButtonGroup = new ButtonGroup();

        ArrayList<Minor> minors = guardian.getMinors();
        for (Minor minor : minors) {
            JRadioButton minorButton = new JRadioButton(minor.getFullName());
            minorsButtonGroup.add(minorButton);
            minorsPanel.add(minorButton);
        }

        if (minors.isEmpty()) {
            minorsPanel.add(new JLabel("No minors associated with this guardian."));
        }
        minorsPanel.revalidate();
        minorsPanel.repaint();
    }

    private void loadAvailableOfferings() {
        offeringsTableModel.setRowCount(0);

        ArrayList<Offering> availableOfferings = Guardian.getAvailableOfferings();

        for (Offering offering : availableOfferings) {
            offeringsTableModel.addRow(new Object[]{
                    offering.getId(),
                    offering.getTitle(),
                    offering.getOrganization(),
                    offering.getCity(),
                    offering.getTime(),
                    offering.getCapacity(),
                    offering.getNumStudents(),
                    offering.getLocation()  // Add location data
            });
        }
    }

    private void bookOffering() {
        int selectedRow = offeringsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offering to book.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JRadioButton selectedMinorButton = getSelectedMinor();
        if (selectedMinorButton == null) {
            JOptionPane.showMessageDialog(this, "Please select a minor for the booking.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String selectedMinorName = selectedMinorButton.getText();
        Object offeringValue = offeringsTableModel.getValueAt(selectedRow, 0);
        int offeringId = (offeringValue instanceof Integer) ? (Integer) offeringValue : Integer.parseInt((String) offeringValue);

        boolean success = guardian.bookOfferingForMinor(selectedMinorName, offeringId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Booking successful for " + selectedMinorName + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadAvailableOfferings();  // Reload available offerings
        } else {
            JOptionPane.showMessageDialog(this, "Error booking the offering.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JRadioButton getSelectedMinor() {
        Enumeration<AbstractButton> buttons = minorsButtonGroup.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.isSelected()) {
                return button;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuardianMakeBooking(guardian).setVisible(true)); // Example with guardianId = 1
    }
}
