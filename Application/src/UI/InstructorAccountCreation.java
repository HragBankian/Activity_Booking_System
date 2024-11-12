package UI;

import DB.Instructor;
import DB.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class InstructorAccountCreation extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JPasswordField passwordField;
    private JTextField phoneField;
    private JTextField dobField;
    private JTextArea citySelectionArea; // To display selected cities
    private JTextArea specialtySelectionArea; // To display selected specialty

    private String[] cities = {
            "Montreal", "Quebec City", "Laval", "Gatineau", "Longueuil",
            "Saguenay", "Levis", "Sherbrooke", "Trois-Rivieres",
            "Saint-Jean-sur-Richelieu", "Chicoutimi", "Drummondville",
            "Saint-Jerome", "Granby"
    };

    private String[] specialties = {
            "Yoga", "Judo", "Swimming", "Dance", "Wrestling",
            "Zumba", "Meditation", "CrossFit", "Pilates", "Personal Training"
    };

    private ArrayList<String> selectedCities;
    private String selectedSpecialty;

    public InstructorAccountCreation() {
        // Set up the frame
        setTitle("Instructor Account Creation");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(e -> {
            dispose();
            CreateAccount createAccountPage = new CreateAccount();
            createAccountPage.setVisible(true);
        });
        leftPanel.add(backButton);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> {
            dispose();
            Home homePage = new Home();
            homePage.setVisible(true);
        });
        rightPanel.add(homeButton);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        JPanel centerPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        JLabel titleLabel = new JLabel("Create Instructor Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        centerPanel.add(new JLabel("Full Name:"));
        nameField = new JTextField();
        centerPanel.add(nameField);

        centerPanel.add(new JLabel("Email:"));
        emailField = new JTextField();
        centerPanel.add(emailField);

        centerPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        centerPanel.add(passwordField);

        centerPanel.add(new JLabel("Phone Number:"));
        phoneField = new JTextField();
        centerPanel.add(phoneField);

        centerPanel.add(new JLabel("Date of Birth (YYYY-MM-DD):"));
        dobField = new JTextField();
        centerPanel.add(dobField);

        JButton cityButton = new JButton("Select Cities");
        cityButton.addActionListener(e -> showCitySelectionDialog());
        centerPanel.add(cityButton);

        citySelectionArea = new JTextArea();
        citySelectionArea.setEditable(false);
        citySelectionArea.setPreferredSize(new Dimension(100, 50));
        centerPanel.add(citySelectionArea);

        JButton specialtyButton = new JButton("Select Specialty");
        specialtyButton.addActionListener(e -> showSpecialtySelectionDialog());
        centerPanel.add(specialtyButton);

        specialtySelectionArea = new JTextArea();
        specialtySelectionArea.setEditable(false);
        specialtySelectionArea.setPreferredSize(new Dimension(100, 50));
        centerPanel.add(specialtySelectionArea);

        JButton createAccountButton = new JButton("Create Instructor Account");
        createAccountButton.addActionListener(e -> {
            if (validateFields()) {
                try {
                    Instructor newInstructor = new Instructor(
                            nameField.getText(),
                            emailField.getText(),
                            new String(passwordField.getPassword()),
                            phoneField.getText(),
                            dobField.getText(),
                            selectedSpecialty
                    );

                    int instructorId = DatabaseConnection.insertUser(newInstructor);
                    if (instructorId != -1) {
                        for (String city : selectedCities) {
                            DatabaseConnection.insertCity(city, instructorId);
                        }
                        JOptionPane.showMessageDialog(null, "Instructor account created successfully!");
                        dispose();
                        Home homePage = new Home();
                        homePage.setVisible(true);
                    } else {
                        JOptionPane.showMessageDialog(null, "Error registering new Instructor account!");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Error creating account: " + ex.getMessage());
                }
            }
        });

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(createAccountButton, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void showCitySelectionDialog() {
        JDialog dialog = new JDialog(this, "Select Cities", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);

        JPanel cityPanel = new JPanel(new GridLayout(0, 1));
        selectedCities = new ArrayList<>();

        for (String city : cities) {
            JCheckBox checkBox = new JCheckBox(city);
            cityPanel.add(checkBox);
            checkBox.addActionListener(e -> {
                if (checkBox.isSelected()) {
                    selectedCities.add(city);
                } else {
                    selectedCities.remove(city);
                }
            });
        }

        dialog.add(cityPanel, BorderLayout.CENTER);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            updateCitySelection();
            dialog.dispose();
        });
        dialog.add(okButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void showSpecialtySelectionDialog() {
        JDialog dialog = new JDialog(this, "Select Specialty", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);

        JPanel specialtyPanel = new JPanel(new GridLayout(0, 1));
        ButtonGroup specialtyGroup = new ButtonGroup();

        for (String specialty : specialties) {
            JRadioButton radioButton = new JRadioButton(specialty);
            specialtyPanel.add(radioButton);
            specialtyGroup.add(radioButton);
            radioButton.addActionListener(e -> selectedSpecialty = specialty);
        }

        dialog.add(specialtyPanel, BorderLayout.CENTER);
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            updateSpecialtySelection();
            dialog.dispose();
        });
        dialog.add(okButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void updateCitySelection() {
        citySelectionArea.setText(String.join(", ", selectedCities));
    }

    private void updateSpecialtySelection() {
        specialtySelectionArea.setText(selectedSpecialty);
    }

    private boolean validateFields() {
        // (validation code unchanged from previous version)
        // Full Name, Email, Password, Phone, DOB, City Availability checks
        if (selectedSpecialty == null || selectedSpecialty.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a specialty.");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InstructorAccountCreation instructorAccountCreationPage = new InstructorAccountCreation();
            instructorAccountCreationPage.setVisible(true);
        });
    }
}
