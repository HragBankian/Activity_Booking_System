package UI;

import DB.DatabaseConnection;
import DB.Minor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ManageMinors extends JFrame {
    private JTable minorsTable;
    private DefaultTableModel minorsTableModel;
    private JTextField minorNameField;
    private JButton addMinorButton, deleteMinorButton;
    private int guardianId;

    public ManageMinors(int guardianId) {
        this.guardianId = guardianId;

        setTitle("Manage Minors");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Table setup
        minorsTableModel = new DefaultTableModel(new Object[]{"Id", "Full Name"}, 0);
        minorsTable = new JTable(minorsTableModel);
        minorsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(minorsTable);

        // Minor name input and buttons
        minorNameField = new JTextField(20);
        addMinorButton = new JButton("Add Minor");
        deleteMinorButton = new JButton("Delete Minor");

        JPanel inputPanel = new JPanel();
        inputPanel.add(new JLabel("New Minor's Name:"));
        inputPanel.add(minorNameField);
        inputPanel.add(addMinorButton);
        inputPanel.add(deleteMinorButton);

        // Adding components to frame
        add(tableScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);

        // Load minors for the guardian
        loadMinors();

        // Add minor button action
        addMinorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMinor();
            }
        });

        // Delete minor button action
        deleteMinorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMinor();
            }
        });
    }

    private void loadMinors() {
        // Clear existing data
        minorsTableModel.setRowCount(0);

        // Retrieve and populate minors for this guardian
        ArrayList<Minor> minors = DatabaseConnection.getMinorsObjectForGuardian(guardianId);
        for (Minor minor : minors) {
            minorsTableModel.addRow(new Object[]{minor.getId(), minor.getFullName()});
        }
    }

    private void addMinor() {
        String minorName = minorNameField.getText().trim();
        if (minorName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter the minor's full name.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = DatabaseConnection.addMinor(minorName, guardianId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Minor added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            minorNameField.setText("");  // Clear input field
            loadMinors();  // Reload minors list
        } else {
            JOptionPane.showMessageDialog(this, "Error adding minor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMinor() {
        int selectedRow = minorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a minor to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object minorValue = minorsTableModel.getValueAt(selectedRow, 0);
        int minorId = (minorValue instanceof Integer) ? (Integer) minorValue : Integer.parseInt((String) minorValue);

        boolean success = DatabaseConnection.deleteMinor(minorId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Minor deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMinors();  // Reload minors list
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting minor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageMinors(1).setVisible(true));  // Example with guardianId = 1
    }
}
