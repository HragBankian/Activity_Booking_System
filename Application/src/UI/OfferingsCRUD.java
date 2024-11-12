package UI;

import DB.Admin;
import DB.Offering;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Arrays;

public class OfferingsCRUD extends JFrame {
    private JTable offeringsTable;
    private DefaultTableModel tableModel;
    private JButton createButton, updateButton, deleteButton, loadButton;
    private JTextField titleField, organizationField, cityField, timeField, capacityField, instructorIdField;

    public OfferingsCRUD() {
        setTitle("Offerings CRUD");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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
        panel.add(new JLabel("Time (DayOfWeek,StartTime,EndTime):"));
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

    private boolean validateTime(String time) {
        // Split the input string by commas to separate day, start time, and end time
        String[] parts = time.split(",\\s*");  // Split on comma and optional spaces

        // Ensure we have exactly three parts: day, start time, and end time
        if (parts.length != 3) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Use format: DayOfWeek, StartTime, EndTime (e.g., Monday, 09:00, 10:00).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String dayOfWeek = parts[0].trim();
        String startTimeString = parts[1].trim();
        String endTimeString = parts[2].trim();

        // Check that the day of the week is valid
        List<String> validDays = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        if (!validDays.contains(dayOfWeek)) {
            JOptionPane.showMessageDialog(this, "Invalid day of week. Use a valid day (e.g., Monday).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validate start and end times with SimpleDateFormat using 24-hour format
        SimpleDateFormat timeFormat24Hour = new SimpleDateFormat("HH:mm");  // 24-hour format
        timeFormat24Hour.setLenient(false); // Ensure strict parsing

        try {
            java.util.Date startTime = timeFormat24Hour.parse(startTimeString);
            java.util.Date endTime = timeFormat24Hour.parse(endTimeString);

            // Check that the end time is after the start time
            if (!endTime.after(startTime)) {
                JOptionPane.showMessageDialog(this, "End time must be after start time.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Ensure times are in the format HH:mm (24-hour).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void createOffering() {
        try {
            String title = titleField.getText();
            String organization = organizationField.getText();
            String city = cityField.getText();
            String time = timeField.getText();

            if (!validateTime(time)) {
                return; // Stop if time is invalid
            }

            int capacity = Integer.parseInt(capacityField.getText());
            Integer instructorId = instructorIdField.getText().isEmpty() ? null : Integer.parseInt(instructorIdField.getText());

            Offering newOffering = new Offering(0, title, organization, city, time, capacity);
            newOffering.setInstructorId(instructorId);

            boolean success = Admin.createOffering(newOffering);
            if (success) {
                JOptionPane.showMessageDialog(this, "Offering created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOfferings();
            } else {
                JOptionPane.showMessageDialog(this, "Error creating offering.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Capacity and Instructor ID.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadOfferings() {
        // Clear the table before reloading data
        tableModel.setRowCount(0);

        ArrayList<Offering> offerings = Admin.getOfferings();

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

            if (!validateTime(time)) {
                return; // Stop if time is invalid
            }

            Object capvalue = tableModel.getValueAt(selectedRow, 5);
            int capacity = (capvalue instanceof Integer) ? (Integer) capvalue : Integer.parseInt((String) capvalue);
            Offering updatedOffering = new Offering(offeringId, title, organization, city, time, capacity);

            boolean success = Admin.updateOffering(offeringId, updatedOffering);
            if (success) {
                JOptionPane.showMessageDialog(this, "Offering updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOfferings();
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
        boolean success = Admin.deleteOffering(offeringId);
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
