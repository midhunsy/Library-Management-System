package library;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {
    private User admin;

    public AdminDashboard(User u) {
        this.admin = u;
        setTitle("Admin Dashboard - " + u.getFullName());
        setSize(600, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title
        JLabel lblTitle = new JLabel("Welcome, " + u.getFullName(), SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        add(lblTitle, BorderLayout.NORTH);

        // Center Panel - Main Buttons
        JButton btnBooks = new JButton("Manage Books");
        JButton btnViewUsers = new JButton("View All Users");
        JButton btnViewRecords = new JButton("View System Records");

        JPanel centerPanel = new JPanel();
        centerPanel.add(btnBooks);
        centerPanel.add(btnViewUsers);
        centerPanel.add(btnViewRecords);
        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel - Back and Logout Buttons
        JButton btnBack = new JButton("⬅ Back");
        JButton btnLogout = new JButton("Logout");

        // Add color styling
        btnLogout.setBackground(Color.RED);
        btnLogout.setForeground(Color.WHITE);
        btnBack.setBackground(Color.LIGHT_GRAY);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        bottomPanel.add(btnBack);
        bottomPanel.add(btnLogout);
        add(bottomPanel, BorderLayout.SOUTH);

        // --- Button Actions ---
        btnBooks.addActionListener(e -> new BookManagementFrame().setVisible(true));
        btnViewUsers.addActionListener(e -> new AdminViewUsersFrame().setVisible(true));
        btnViewRecords.addActionListener(e -> new AdminViewRecordsFrame().setVisible(true));

        // Back button → just go to login or previous frame
        btnBack.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true); // Back to main login
        });

        // Logout button → confirmation + redirect
        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });

        setVisible(true);
    }
}
