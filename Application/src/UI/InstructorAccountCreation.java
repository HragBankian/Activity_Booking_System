package UI;

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
    private JTextField specialtiesField;
    private JTextArea citySelectionArea; // To display selected cities

    private String[] cities = {
            "Montreal", "Quebec City", "Laval", "Gatineau", "Longueuil",
            "Saguenay", "Levis", "Sherbrooke", "Trois-Rivieres",
            "Saint-Jean-sur-Richelieu", "Chicoutimi", "Drummondville",
            "Saint-Jerome", "Granby"
    };

    private ArrayList<String> selectedCities;

    public InstructorAccountCreation() {
        // Set up the frame
        setTitle("Instructor Account Creation");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Set up the main panel with BorderLayout
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Top panel with "Back" and "UI.Home" buttons
        JPanel topPanel = new JPanel(new BorderLayout());

        // Left panel for "Back" button
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                CreateAccount createAccountPage = new CreateAccount();
                createAccountPage.setVisible(true);
            }
        });
        leftPanel.add(backButton);

        // Right panel for "UI.Home" button
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                Home homePage = new Home();
                homePage.setVisible(true);
            }
        });
        rightPanel.add(homeButton);

        // Add left and right panels to the top panel
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        // Center panel for form fields
        JPanel centerPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        JLabel titleLabel = new JLabel("Create Instructor Account", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Form fields
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

        centerPanel.add(new JLabel("Specialties (separate with commas):"));
        specialtiesField = new JTextField();
        centerPanel.add(specialtiesField);

        centerPanel.add(new JLabel("City Availability:"));
        JButton cityButton = new JButton("Select Cities");
        cityButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCitySelectionDialog();
            }
        });
        centerPanel.add(cityButton);

        // Area to display selected cities
        citySelectionArea = new JTextArea();
        citySelectionArea.setEditable(false);
        citySelectionArea.setPreferredSize(new Dimension(100, 50));
        centerPanel.add(citySelectionArea); // Moved this to be directly under the button

        // Create Instructor Account button
        JButton createAccountButton = new JButton("Create Instructor Account");
        createAccountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateFields()) {
                    JOptionPane.showMessageDialog(null, "Instructor account created successfully!");
                    dispose();
                    Home homePage = new Home();
                    homePage.setVisible(true);
                }
            }
        });

        // Add components to the main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(createAccountButton, BorderLayout.SOUTH);

        // Add main panel to the frame
        add(mainPanel);
    }

    private void showCitySelectionDialog() {
        // Dialog for city selection
        JDialog dialog = new JDialog(this, "Select Cities", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(300, 400);
        dialog.setLocationRelativeTo(this);

        JPanel cityPanel = new JPanel(new GridLayout(0, 1));
        selectedCities = new ArrayList<>(); // Reset selected cities

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

    private void updateCitySelection() {
        citySelectionArea.setText(String.join(", ", selectedCities));
    }

    private boolean validateFields() {
        // Validate Full Name
        if (nameField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Full Name is required.");
            return false;
        }

        // Validate Email
        String email = emailField.getText();
        if (!Pattern.matches("^[\\w-\\.]+@[\\w-]+(\\.com|\\.ca)$", email)) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address.");
            return false;
        }

        // Validate Password
        if (passwordField.getPassword().length == 0) {
            JOptionPane.showMessageDialog(this, "Password is required.");
            return false;
        }

        // Validate Phone Number
        if (phoneField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Phone Number is required.");
            return false;
        }

        // Validate Date of Birth
        String dob = dobField.getText();
        if (!isValidDOB(dob)) {
            JOptionPane.showMessageDialog(this, "Date of Birth format should be YYYY-MM-DD and you must be at least 18 years old.");
            return false;
        }

        // Validate Specialties
        String specialties = specialtiesField.getText();
        if (specialties.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Specialties are required.");
            return false;
        }

        // Validate City Availability
        if (selectedCities.isEmpty()) {
            JOptionPane.showMessageDialog(this, "At least one city must be selected.");
            return false;
        }

        return true;
    }

    private boolean isValidDOB(String dob) {
        // Regex to match YYYY-MM-DD format
        if (!Pattern.matches("^\\d{4}-\\d{2}-\\d{2}$", dob)) {
            return false; // Invalid format
        }

        try {
            String[] parts = dob.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);
            int day = Integer.parseInt(parts[2]);

            // Check if the date is valid
            if (month < 1 || month > 12 || day < 1 || day > 31) {
                return false; // Invalid month or day
            }
            // Check for the number of days in each month
            if ((month == 4 || month == 6 || month == 9 || month == 11) && day > 30) {
                return false; // April, June, September, November
            }
            if (month == 2) {
                if (isLeapYear(year)) {
                    if (day > 29) return false; // February in leap year
                } else {
                    if (day > 28) return false; // February in non-leap year
                }
            }

            int currentYear = java.time.LocalDate.now().getYear();
            return currentYear - year >= 18; // Check age
        } catch (Exception e) {
            return false; // In case of parsing error
        }
    }

    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InstructorAccountCreation instructorAccountCreationPage = new InstructorAccountCreation();
            instructorAccountCreationPage.setVisible(true);
        });
    }
}
