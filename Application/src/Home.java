import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Home extends JFrame {
    public Home() {
        // Set up the frame
        setTitle("Home");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Title label
        JLabel titleLabel = new JLabel("Lesson Booking System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // "Create an Account" button
        JButton createAccountButton = new JButton("Create an Account");
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                CreateAccount createAccountPage = new CreateAccount(); // Open CreateAccount page
                createAccountPage.setVisible(true);
            }
        });

        // "Login" button
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                Login loginPage = new Login(); // Open Login page
                loginPage.setVisible(true);
            }
        });

        // Add buttons to the panel
        buttonPanel.add(createAccountButton);
        buttonPanel.add(loginButton);

        // Add components to the main panel
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add main panel to the frame
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Home homePage = new Home();
            homePage.setVisible(true);
        });
    }
}
