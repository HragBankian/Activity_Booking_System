package UI.AdminPages;

import OOP.Admin;
import OOP.Offering;

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
    private JTextField titleField, organizationField, cityField, timeField, capacityField, instructorIdField, locationField;

    public OfferingsCRUD() {
        setTitle("Offerings CRUD");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        tableModel = new DefaultTableModel(new Object[]{"ID", "Title", "Organization", "City", "Time", "Capacity", "Num Students", "Instructor ID", "Location"}, 0);
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
        locationField = new JTextField(15);  // New field for Location

        // Panel for user input
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 2));
        panel.add(new JLabel("Title:"));
        panel.add(titleField);
        panel.add(new JLabel("Organization:"));
        panel.add(organizationField);
        panel.add(new JLabel("City:"));
        panel.add(cityField);
        panel.add(new JLabel("Day/Time (Weekday,StartTime,EndTime):"));
        panel.add(timeField);
        panel.add(new JLabel("Capacity:"));
        panel.add(capacityField);
        panel.add(new JLabel("Location:"));  // Add label for Location
        panel.add(locationField);  // Add text field for Location

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
        String[] parts = time.split(",\\s*");

        if (parts.length != 3) {
            JOptionPane.showMessageDialog(this, "Invalid time format. Use format: DayOfWeek, StartTime, EndTime (e.g., Monday, 09:00, 10:00).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        String dayOfWeek = parts[0].trim();
        String startTimeString = parts[1].trim();
        String endTimeString = parts[2].trim();

        List<String> validDays = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        if (!validDays.contains(dayOfWeek)) {
            JOptionPane.showMessageDialog(this, "Invalid day of week. Use a valid day (e.g., Monday).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        SimpleDateFormat timeFormat24Hour = new SimpleDateFormat("HH:mm");
        timeFormat24Hour.setLenient(false);

        try {
            java.util.Date startTime = timeFormat24Hour.parse(startTimeString);
            java.util.Date endTime = timeFormat24Hour.parse(endTimeString);

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
            String location = locationField.getText();  // New location input

            if (!validateTime(time)) {
                return;
            }

            int capacity = Integer.parseInt(capacityField.getText());
            Integer instructorId = instructorIdField.getText().isEmpty() ? null : Integer.parseInt(instructorIdField.getText());

            Offering newOffering = new Offering(0, title, organization, city, time, capacity, location);
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
        tableModel.setRowCount(0);

        ArrayList<Offering> offerings = Admin.getOfferings();

        for (Offering offering : offerings) {
            int id = offering.getId();
            String title = offering.getTitle();
            String organization = offering.getOrganization();
            String city = offering.getCity();
            String time = offering.getTime();
            int capacity = offering.getCapacity();
            int numStudents = offering.getNumStudents();
            Integer instructorId = offering.getInstructorId();
            String location = offering.getLocation();  // Get location

            tableModel.addRow(new Object[]{id, title, organization, city, time, capacity, numStudents, instructorId, location});
        }
    }

    private void updateOffering() {
        int selectedRow = offeringsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an offering to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            Object offerId = tableModel.getValueAt(selectedRow, 0);
            int offeringId = (offerId instanceof Integer) ? (Integer) offerId : Integer.parseInt((String) offerId);
            if (offeringsTable.isEditing()){
                offeringsTable.getCellEditor().stopCellEditing();
            }
            String title = (String) tableModel.getValueAt(selectedRow, 1);
            String organization = (String) tableModel.getValueAt(selectedRow, 2);
            String city = (String) tableModel.getValueAt(selectedRow, 3);
            String time = (String) tableModel.getValueAt(selectedRow, 4);
            String location = (String) tableModel.getValueAt(selectedRow, 8);  // Get location from table

            if (!validateTime(time)) {
                return;
            }

            Object capValue = tableModel.getValueAt(selectedRow, 5);
            int capacity = (capValue instanceof Integer) ? (Integer) capValue : Integer.parseInt((String) capValue);
            Offering updatedOffering = new Offering(offeringId, title, organization, city, time, capacity, location);  // Pass location

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
            loadOfferings();
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting offering.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OfferingsCRUD().setVisible(true));
    }
}
