package UI;

import DB.DatabaseConnection;
import DB.Instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class SelectOffering extends JFrame {
    private JTable offeringsTable;
    private DefaultTableModel tableModel;
    private static Instructor instructor;

    public SelectOffering(Instructor instructor) {
        this.instructor = instructor;

        setTitle("Select Offering");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tableModel = new DefaultTableModel(new Object[]{"Offering ID", "Title", "Organization", "City", "Time", "Capacity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Disable editing for all cells
            }
        };
        offeringsTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(offeringsTable);
        add(scrollPane, BorderLayout.CENTER);

        loadOfferings();

        // Button to select an offering
        JButton selectOfferingButton = new JButton("Select Offering");
        selectOfferingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectOffering();
            }
        });
        add(selectOfferingButton, BorderLayout.SOUTH);
    }

    private void loadOfferings() {
        // Fetch offerings where instructor_id is null using DatabaseConnection class
        List<Object[]> offerings = instructor.getAvailableOfferings();

        // Clear the existing table data
        tableModel.setRowCount(0);

        // Add new rows to the table model
        for (Object[] offering : offerings) {
            tableModel.addRow(offering);
        }
    }

    private void selectOffering() {
        int selectedRow = offeringsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offering.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the offering ID from the selected row
        int offeringId = (int) tableModel.getValueAt(selectedRow, 0);

        // Update the offering with the instructor's ID using DatabaseConnection class
        boolean success = instructor.selectOffering(offeringId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Offering selected successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // Optionally, update the table and show feedback
            loadOfferings();
        } else {
            JOptionPane.showMessageDialog(this, "Error selecting offering.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Pass a sample instructor ID (replace with actual ID when needed)
        int instructorId = 1;  // For example
        SwingUtilities.invokeLater(() -> new SelectOffering(instructor).setVisible(true));
    }
}
