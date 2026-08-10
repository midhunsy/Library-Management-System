package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AdminViewUsersFrame extends JFrame {
    public AdminViewUsersFrame() {
        setTitle("All Users");
        setSize(600,400);
        setLocationRelativeTo(null);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID","Name","Phone","Username","Role"},0);
        JTable table = new JTable(model);
        add(new JScrollPane(table), BorderLayout.CENTER);

        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, full_name, phone, username, role FROM users")) {
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("full_name"), rs.getString("phone"), rs.getString("username"), rs.getString("role")});
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
}
