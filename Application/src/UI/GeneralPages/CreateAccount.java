package UI.GeneralPages;

import UI.ClientPages.ClientAccountCreation;
import UI.GuardianPages.GuardianAccountCreation;
import UI.InstructorPages.InstructorAccountCreation;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CreateAccount extends JFrame {
    public CreateAccount() {
        // Set up the frame
        setTitle("Create Account");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel with "UI.GeneralPages.Home" button
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

        // Center panel for account type selection
        JPanel centerPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        JLabel titleLabel = new JLabel("Select Account Type", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        centerPanel.add(titleLabel);

        // Account type buttons
        JButton clientButton = new JButton("Client");
        clientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                ClientAccountCreation clientAccountCreationPage = new ClientAccountCreation();
                clientAccountCreationPage.setVisible(true);
            }
        });
        centerPanel.add(clientButton);

        JButton guardianButton = new JButton("Guardian");
        guardianButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                GuardianAccountCreation guardianAccountCreationPage = new GuardianAccountCreation();
                guardianAccountCreationPage.setVisible(true);
            }
        });
        centerPanel.add(guardianButton);

        JButton instructorButton = new JButton("Instructor");
        instructorButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                InstructorAccountCreation instructorAccountCreationPage = new InstructorAccountCreation();
                instructorAccountCreationPage.setVisible(true);
            }
        });
        centerPanel.add(instructorButton);

        // Add components to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // Add main panel to the frame
        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CreateAccount createAccountPage = new CreateAccount();
            createAccountPage.setVisible(true);
        });
    }
}
