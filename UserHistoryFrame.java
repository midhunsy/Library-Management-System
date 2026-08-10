package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
import java.awt.*;

public class UserHistoryFrame extends JFrame {
    private User user;
    private DefaultTableModel model;

    public UserHistoryFrame(User u) {
        this.user = u;
        setTitle("My Borrowing History - " + u.getFullName());
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create top panel with only Back button
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnBack = new JButton("Back");
        topPanel.add(btnBack);
        add(topPanel, BorderLayout.NORTH);

        // Table setup
        model = new DefaultTableModel(new Object[]{"ID", "Book Title", "Issue Date", "Return Date", "Status"}, 0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        // Load user borrowing history
        loadHistory();

        // Action for Back button
        btnBack.addActionListener(e -> {
            new UserDashboard(user).setVisible(true);
            this.dispose();
        });
    }

    private void loadHistory() {
        model.setRowCount(0);
        String sql = "SELECT ih.*, b.title FROM issue_history ih JOIN books b ON ih.book_id = b.id WHERE ih.user_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getTimestamp("issue_date"),
                        rs.getTimestamp("return_date"),
                        rs.getString("status")
                    });
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
