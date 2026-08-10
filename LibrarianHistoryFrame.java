package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.List;

public class LibrarianHistoryFrame extends JFrame {
    private DefaultTableModel model;
    private JTable historyTable;

    public LibrarianHistoryFrame() {
        setTitle("Users Borrowing History");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Top panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnRefresh = new JButton("Refresh");
        JButton btnAddReturn = new JButton("Add Return Date");
        JButton btnBack = new JButton("Back");

        topPanel.add(btnAddReturn);
        topPanel.add(btnRefresh);
        topPanel.add(btnBack);
        add(topPanel, BorderLayout.NORTH);

        // Table
        model = new DefaultTableModel(new Object[]{"History ID", "User", "Book", "Issue Date", "Return Date", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        historyTable = new JTable(model);
        add(new JScrollPane(historyTable), BorderLayout.CENTER);

        // Load data initially
        loadHistory();

        // Button Actions
        btnRefresh.addActionListener(e -> loadHistory());

        btnBack.addActionListener(e -> dispose());

        btnAddReturn.addActionListener(e -> updateReturnDate());
    }

    // Load all issue history with user and book details
    private void loadHistory() {
        model.setRowCount(0);
        String sql = "SELECT ih.id, u.username, b.title, ih.issue_date, ih.return_date, ih.status " +
                     "FROM issue_history ih " +
                     "JOIN users u ON ih.user_id = u.id " +
                     "JOIN books b ON ih.book_id = b.id " +
                     "ORDER BY ih.issue_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("title"),
                        rs.getTimestamp("issue_date"),
                        rs.getTimestamp("return_date"),
                        rs.getString("status")
                });
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Allow librarian to set a return date for selected record
    private void updateReturnDate() {
        int row = historyTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a record first.");
            return;
        }

        int issueId = (int) model.getValueAt(row, 0);
        String currentStatus = (String) model.getValueAt(row, 5);

        if ("RETURNED".equalsIgnoreCase(currentStatus)) {
            JOptionPane.showMessageDialog(this, "This book has already been returned.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Mark this book as returned and update the return date?",
                "Confirm Return", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            String sql = "UPDATE issue_history SET return_date = CURRENT_TIMESTAMP, status = 'RETURNED' WHERE id = ?";
            String sqlBook = "UPDATE books SET quantity = quantity + 1 WHERE id = (SELECT book_id FROM issue_history WHERE id = ?)";

            try (Connection conn = DBConnection.getConnection()) {
                // Update return date and status
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, issueId);
                    ps.executeUpdate();
                }

                // Increment book quantity
                try (PreparedStatement ps2 = conn.prepareStatement(sqlBook)) {
                    ps2.setInt(1, issueId);
                    ps2.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Return date updated successfully!");
                loadHistory();

            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error updating return date.");
            }
        }
    }
}
