package UI.AdminPages;

import OOP.Admin;
import OOP.Users.Client;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ClientsRD extends JFrame {
    private JTable clientsTable;
    private DefaultTableModel tableModel;
    private JButton deleteButton;

    public ClientsRD() {
        setTitle("Clients CRUD");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Only close this window
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Initialize components
        tableModel = new DefaultTableModel(new Object[]{"ID", "Full Name", "Email", "Phone Number", "Date of Birth"}, 0){
            public boolean isCellEditable(int row, int column) {
                return false;  // Disable editing for all cells
            }
        };
        clientsTable = new JTable(tableModel);

        deleteButton = new JButton("Delete Client");

        // Scroll pane for table
        JScrollPane tableScrollPane = new JScrollPane(clientsTable);

        // Panel for button
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(deleteButton);

        // Adding components to the frame
        add(tableScrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Load clients when the UI starts
        loadClients();

        // Delete client action
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteClient();
            }
        });
    }

    private void loadClients() {
        // Clear the table before reloading data
        tableModel.setRowCount(0);

        ArrayList<Client> clients = Admin.getClients();

        // Populate the table with client data
        for (Client client : clients) {
            int id = client.getId();
            String fullName = client.getFullName();
            String email = client.getEmail();
            String phoneNumber = client.getPhoneNumber();
            String dateOfBirth = client.getDateOfBirth();

            tableModel.addRow(new Object[]{id, fullName, email, phoneNumber, dateOfBirth});
        }
    }

    private void deleteClient() {
        int selectedRow = clientsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a client to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int clientId = (int) tableModel.getValueAt(selectedRow, 0);
        boolean success = Admin.deleteClient(clientId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Client deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadClients(); // Refresh the table
        } else {
            JOptionPane.showMessageDialog(this, "Error deleting client.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientsRD().setVisible(true));
    }
}
