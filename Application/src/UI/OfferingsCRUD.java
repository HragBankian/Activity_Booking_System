package UI;

import DB.Offering;
import DB.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class OfferingsCRUD extends JFrame {
    private JTable offeringsTable;
    private DefaultTableModel tableModel;
    private JButton createButton, updateButton, deleteButton, loadButton;
    private JTextField titleField, organizationField, cityField, timeField, capacityField, instructorIdField;

    public OfferingsCRUD() {
        setTitle("Offerings CRUD");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Organization", "City", "Time", "Capacity", "Num Students", "Instructor ID"}, 0);
        offeringsTable = new JTable(tableModel);

        createButton = new JButton("Create Offering");
        updateButton = new JButton("Update Offering");
        deleteButton = new JButton("Delete Offering");
        loadButton = new JButton("Load Offerings");

        titleField = new JTextField(15);
        organizationField = new JTextField(15);
        cityField = new JTextField(15);
        timeField = new JTextField(15);
        capacityField = new JTextField(5);
        instructorIdField = new JTextField(5);

        // Panel for user input
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Organization:"));
        panel.add(organizationField);
        panel.add(new JLabel("City:"));
        panel.add(cityField);
        panel.add(new JLabel("Time:"));
        panel.add(timeField);
        panel.add(new JLabel("Capacity:"));
        panel.add(capacityField);
        panel.add(new JLabel("Instructor ID:"));
        panel.add(instructorIdField);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(createButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(loadButton);

        // Scroll pane for table
        JScrollPane tableScrollPane = new JScrollPane(offeringsTable);

        // Adding components to the frame
        add(panel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(tableScrollPane, BorderLayout.CENTER);

        // Load offerings on button click
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadOfferings();
            }
        });

        // Create offering action
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createOffering();
            }
        });

        // Update offering action
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateOffering();
            }
        });

        // Delete offering action
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteOffering();
            }
        });
    }

    private void loadOfferings() {
        // Clear the table before reloading data
        tableModel.setRowCount(0);

        ArrayList<Offering> offerings = DatabaseConnection.getOfferings();

        // Populate the table with offerings
        for (Offering offering : offerings) {
            int id = offering.getId();
            String title = offering.getTitle();
            String organization = offering.getOrganization();
            String city = offering.getCity();
            String time = offering.getTime();
            int capacity = offering.getCapacity();
            int numStudents = offering.getNumStudents();
            Integer instructorId = offering.getInstructorId();

            tableModel.addRow(new Object[]{id, title, organization, city, time, capacity, numStudents, instructorId});
        }
    }

    private void createOffering() {
        try {
            String title = titleField.getText();
            String organization = organizationField.getText();
            String city = cityField.getText();
            String time = timeField.getText();
            int capacity = Integer.parseInt(capacityField.getText()); // Handle potential parsing error
            Integer instructorId = instructorIdField.getText().isEmpty() ? null : Integer.parseInt(instructorIdField.getText());

            Offering newOffering = new Offering(0, title, organization, city, time, capacity);
            newOffering.setInstructorId(instructorId);

            boolean success = DatabaseConnection.createOffering(newOffering);
            if (success) {
                JOptionPane.showMessageDialog(this, "Offering created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOfferings(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Error creating offering.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Capacity and Instructor ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateOffering() {
        int selectedRow = offeringsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offering to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            int offeringId = (int) tableModel.getValueAt(selectedRow, 0);
            if (offeringsTable.isEditing()){
                offeringsTable.getCellEditor().stopCellEditing();
            }
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            String organization = (String) tableModel.getValueAt(selectedRow, 2);
            String city = (String) tableModel.getValueAt(selectedRow, 3);
            String time = (String) tableModel.getValueAt(selectedRow, 4);
            Object capvalue = tableModel.getValueAt(selectedRow, 5);
            int capacity = (capvalue instanceof Integer) ? (Integer) capvalue : Integer.parseInt((String) capvalue);
            System.out.println(capacity);
            Offering updatedOffering = new Offering(offeringId, title, organization, city, time, capacity);

            boolean success = DatabaseConnection.updateOffering(offeringId, updatedOffering);
            if (success) {
                JOptionPane.showMessageDialog(this, "Offering updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOfferings(); // Refresh the table
            } else {
                JOptionPane.showMessageDialog(this, "Error updating offering.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Capacity and Instructor ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteOffering() {
        int selectedRow = offeringsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offering to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int offeringId = (int) tableModel.getValueAt(selectedRow, 0);
        boolean success = DatabaseConnection.deleteOffering(offeringId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Offering deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadOfferings(); // Refresh the table
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting offering.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OfferingsCRUD().setVisible(true));
    }
}
