package UI.GeneralPages;

import OOP.*;
import OOP.Users.Client;
import OOP.Users.Guardian;
import OOP.Users.Instructor;
import OOP.Users.User;
import UI.AdminPages.AdminHome;
import UI.ClientPages.ClientHome;
import UI.GuardianPages.GuardianHome;
import UI.InstructorPages.InstructorHome;

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

        // Top panel for the "UI.GeneralPages.Home" button
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

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String accountType = (String) accountTypeComboBox.getSelectedItem();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                User user = User.validateLogin(accountType, email, password);
                if (user != null) {
                    JOptionPane.showMessageDialog(Login.this, "Login successful!", "Login", JOptionPane.INFORMATION_MESSAGE);
                    JFrame nextPage = null;
                    switch (accountType){
                        case "Admin":
                            Admin admin = Admin.getInstance();
                            admin.initialize(email, password);
                            nextPage = new AdminHome();
                            break;
                        case "Client":
                            nextPage = new ClientHome((Client) user);
                            break;
                        case "Guardian":
                            nextPage = new GuardianHome((Guardian) user);
                            break;
                        case "Instructor":
                            nextPage = new InstructorHome((Instructor) user);
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
