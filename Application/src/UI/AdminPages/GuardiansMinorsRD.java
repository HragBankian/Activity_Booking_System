package UI.AdminPages;

import OOP.Admin;
import OOP.Users.Guardian;
import OOP.Minor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class GuardiansMinorsRD extends JFrame {
    private JTable guardiansTable;
    private DefaultTableModel guardiansTableModel;
    private JTable minorsTable;
    private DefaultTableModel minorsTableModel;
    private JButton deleteGuardianButton;
    private JButton deleteMinorButton;

    public GuardiansMinorsRD() {
        setTitle("Guardians and Minors CRUD");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1));  // Use GridLayout to have two tables

        // Initialize components
        guardiansTableModel = new DefaultTableModel(new Object[]{"ID", "Full Name", "Email", "Phone Number", "Date of Birth"}, 0){
            public boolean isCellEditable(int row, int column) {
                return false;  // Disable editing for all cells
            }
        };
        guardiansTable = new JTable(guardiansTableModel);

        minorsTableModel = new DefaultTableModel(new Object[]{"ID", "Full Name", "Guardian ID"}, 0){
            public boolean isCellEditable(int row, int column) {
                return false;  // Disable editing for all cells
            }
        };
        minorsTable = new JTable(minorsTableModel);

        deleteGuardianButton = new JButton("Delete Guardian");
        deleteMinorButton = new JButton("Delete Minor");

        // Scroll panes for tables
        JScrollPane guardiansTableScrollPane = new JScrollPane(guardiansTable);
        JScrollPane minorsTableScrollPane = new JScrollPane(minorsTable);

        // Panel for buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteGuardianButton);
        buttonPanel.add(deleteMinorButton);

        // Adding components to the frame
        add(guardiansTableScrollPane);
        add(minorsTableScrollPane);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load guardians and minors when the UI starts
        loadGuardians();
        loadMinors();

        // Delete guardian action
        deleteGuardianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteGuardian();
            }
        });

        // Delete minor action
        deleteMinorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMinor();
            }
        });
    }

    private void loadGuardians() {
        // Clear the table before reloading data
        guardiansTableModel.setRowCount(0);

        ArrayList<Guardian> guardians = Admin.getGuardians();

        // Populate the table with guardian data
        for (Guardian guardian : guardians) {
            int id = guardian.getId();
            String fullName = guardian.getFullName();
            String email = guardian.getEmail();
            String phoneNumber = guardian.getPhoneNumber();
            String dateOfBirth = guardian.getDateOfBirth();

            guardiansTableModel.addRow(new Object[]{id, fullName, email, phoneNumber, dateOfBirth});
        }
    }

    private void deleteGuardian() {
        int selectedRow = guardiansTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a guardian to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int guardianId = (int) guardiansTableModel.getValueAt(selectedRow, 0);
        boolean success = Admin.deleteGuardian(guardianId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Guardian deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadGuardians(); // Refresh the guardians table
            loadMinors(); // Refresh the minors table
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting guardian.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMinors() {
        // Clear the table before reloading data
        minorsTableModel.setRowCount(0);

        ArrayList<Minor> minors = Admin.getMinors();

        // Populate the table with minor data
        for (Minor minor : minors) {
            int id = minor.getId();
            String fullName = minor.getFullName();
            int guardianId = minor.getGuardianId();

            minorsTableModel.addRow(new Object[]{id, fullName, guardianId});
        }
    }

    private void deleteMinor() {
        int selectedRow = minorsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a minor to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int minorId = (int) minorsTableModel.getValueAt(selectedRow, 0);
        boolean success = Admin.deleteMinor(minorId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Minor deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadMinors(); // Refresh the minors table
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting minor.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GuardiansMinorsRD().setVisible(true));
    }
}
