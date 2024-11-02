package UI;

import DB.DatabaseConnection;
import DB.Offering;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class ViewOfferings extends JFrame {
    private JTable offeringsTable;
    private DefaultTableModel tableModel;

    public ViewOfferings() {
        // Set up the frame
        setTitle("View Lesson Offerings");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create the table model with column names
        String[] columnNames = {"Title", "Organization", "Instructor", "Location", "Time", "Capacity"};
        tableModel = new DefaultTableModel(columnNames, 0);
        offeringsTable = new JTable(tableModel);

        // Add a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(offeringsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Load the offerings into the table
        loadOfferings();

        // Back button
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            // Go back to the previous page (implement your navigation logic)
            Home homePage = new Home();
            homePage.setVisible(true);
        });
        add(backButton, BorderLayout.SOUTH);
    }

    private void loadOfferings() {
        ArrayList<Offering> offerings = fetchOfferingsFromDatabase();
        for (Offering offering : offerings) {
            tableModel.addRow(new Object[]{
                    offering.getTitle(),
                    offering.getOrganization(),
                    offering.getInstructor(),
                    offering.getLocation(),
                    offering.getTime(),
                    offering.getCapacity()
            });
        }
    }

    private ArrayList<Offering> fetchOfferingsFromDatabase() {
        ArrayList<Offering> offerings = new ArrayList<>();
        String query = "SELECT title, organization_name, instructor, location, time, capacity FROM offerings"; // Adjust your table name and fields as needed

        try (Connection connection = DatabaseConnection.connect();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                Offering offering = new Offering(
                        resultSet.getString("title"),
                        resultSet.getString("organization_name"),
                        resultSet.getString("instructor"),
                        resultSet.getString("location"),
                        resultSet.getString("time"),
                        resultSet.getInt("capacity")
                );
                offerings.add(offering);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return offerings;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ViewOfferings viewOfferingsPage = new ViewOfferings();
            viewOfferingsPage.setVisible(true);
        });
    }
}
