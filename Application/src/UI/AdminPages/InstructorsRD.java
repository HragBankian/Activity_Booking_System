package UI.AdminPages;

import OOP.Admin;
import OOP.Users.Instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class InstructorsRD extends JFrame {
    private JTable instructorsTable;
    private DefaultTableModel instructorsTableModel;
    private JButton deleteInstructorButton;

    public InstructorsRD() {
        setTitle("Instructors RD");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        instructorsTableModel = new DefaultTableModel(new Object[]{"ID", "Full Name", "Email", "Phone Number", "Date of Birth", "Specialty"}, 0){
            public boolean isCellEditable(int row, int column) {
                return false;  // Disable editing for all cells
            }
        };
        instructorsTable = new JTable(instructorsTableModel);

        deleteInstructorButton = new JButton("Delete Instructor");

        // Scroll pane for the instructors table
        JScrollPane instructorsTableScrollPane = new JScrollPane(instructorsTable);

        // Panel for the button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteInstructorButton);

        // Add components to the frame
        add(instructorsTableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load instructors when the UI starts
        loadInstructors();

        // Delete instructor action
        deleteInstructorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteInstructor();
            }
        });
    }

    private void loadInstructors() {
        // Clear the table before reloading data
        instructorsTableModel.setRowCount(0);

        // Fetch instructors from the database
        ArrayList<Instructor> instructors = Admin.getInstructors();

        // Populate the table with instructor data
        for (Instructor instructor : instructors) {
            int id = instructor.getId();
            String fullName = instructor.getFullName();
            String email = instructor.getEmail();
            String phoneNumber = instructor.getPhoneNumber();
            String dateOfBirth = instructor.getDateOfBirth();
            String specialty = instructor.getSpecialty();

            instructorsTableModel.addRow(new Object[]{id, fullName, email, phoneNumber, dateOfBirth, specialty});
        }
    }

    private void deleteInstructor() {
        int selectedRow = instructorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an instructor to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int instructorId = (int) instructorsTableModel.getValueAt(selectedRow, 0);
        boolean success = Admin.deleteInstructor(instructorId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Instructor deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadInstructors(); // Refresh the instructors table
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting instructor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InstructorsRD().setVisible(true));
    }
}
