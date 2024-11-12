package UI.InstructorPages;

import OOP.Users.Instructor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ManageLessons extends JFrame {
    private JTable lessonsTable;
    private DefaultTableModel tableModel;
    private static Instructor instructor;

    public ManageLessons(Instructor instructor) {
        this.instructor = instructor;

        setTitle("Manage Lessons");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        tableModel = new DefaultTableModel(new String[] {"ID", "Title", "Organization", "City", "Time", "Capacity"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        lessonsTable = new JTable(tableModel);
        loadLessons();

        JButton cancelLessonButton = new JButton("Cancel Lesson");
        cancelLessonButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelSelectedLesson();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(cancelLessonButton);

        add(new JScrollPane(lessonsTable), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadLessons() {
        tableModel.setRowCount(0); // Clear existing rows
        List<Object[]> lessons = instructor.getLessons();
        for (Object[] lesson : lessons) {
            tableModel.addRow(lesson);
        }
    }

    private void cancelSelectedLesson() {
        int selectedRow = lessonsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a lesson to cancel.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int offeringId = (int) tableModel.getValueAt(selectedRow, 0);
        boolean success = Instructor.cancelLesson(offeringId);
        if (success) {
            JOptionPane.showMessageDialog(this, "Lesson canceled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadLessons(); // Refresh the table after cancellation
        } else {
            JOptionPane.showMessageDialog(this, "Error canceling lesson.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageLessons(instructor).setVisible(true)); // Example instructorId = 1
    }
}
