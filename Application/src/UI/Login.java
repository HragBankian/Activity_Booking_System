package UI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame {
    private JComboBox<String> accountTypeComboBox;
    private JTextField emailField;
    private JPasswordField passwordField;

    public Login() {
        // Set up the frame
        setTitle("Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel for the "UI.Home" button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Home homePage = new Home();
                homePage.setVisible(true);
            }
        });
        topPanel.add(homeButton);

        // Center panel for form fields
        JPanel centerPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        centerPanel.add(new JLabel("Account Type:"));
        String[] accountTypes = {"Client", "Guardian", "Instructor", "Admin"};
        accountTypeComboBox = new JComboBox<>(accountTypes);
        centerPanel.add(accountTypeComboBox);

        centerPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        centerPanel.add(emailField);

        centerPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        centerPanel.add(passwordField);

        // Add components to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Login button at the bottom
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String accountType = (String) accountTypeComboBox.getSelectedItem();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                int id = DB.DatabaseConnection.validateLogin(accountType, email, password);
                if (id != -1) {
                    JOptionPane.showMessageDialog(Login.this, "Login successful!", "Login", JOptionPane.INFORMATION_MESSAGE);
                    JFrame nextPage = null;
                    switch (accountType){
                        case "Admin":
                            nextPage = new AdminHome();
                            break;
                        case "Client":
                            nextPage = new ClientHome(id);
                            break;
                        case "Guardian":
                            //nextPage = new GuardianHome();
                            break;
                        case "Instructor":
                            //nextPage = new InstructorHome();
                            break;
                        default:
                            nextPage = new Home();
                    }
                    if (nextPage != null) {
                        nextPage.setVisible(true);
                        Login.this.dispose();
                    }

                } else {
                    JOptionPane.showMessageDialog(Login.this, "Invalid credentials. Please try again.", "Login", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        mainPanel.add(loginButton, BorderLayout.SOUTH);

        // Add main panel to the frame
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Login loginPage = new Login();
            loginPage.setVisible(true);
        });
    }
}
