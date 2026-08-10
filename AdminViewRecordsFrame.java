package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminViewRecordsFrame extends JFrame {
    public AdminViewRecordsFrame() {
        setTitle("Issue History Records");
        setSize(700,450);
        setLocationRelativeTo(null);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","User","Book","Issue Date","Return Date","Status"},0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        String sql = "SELECT ih.id, u.username, b.title, ih.issue_date, ih.return_date, ih.status " +
                     "FROM issue_history ih JOIN users u ON ih.user_id = u.id JOIN books b ON ih.book_id=b.id";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt(1), rs.getString(2), rs.getString(3), rs.getTimestamp(4), rs.getTimestamp(5), rs.getString(6)
                });
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
