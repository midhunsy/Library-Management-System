package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserDashboard extends JFrame {
    private User user;
    private JTable bookTable;
    private DefaultTableModel model;

    public UserDashboard(User u) {
        this.user = u;
        setTitle("User Dashboard - " + u.getFullName());
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // === Gradient background ===
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color c1 = new Color(72, 201, 176);  // teal
                Color c2 = new Color(240, 243, 244); // light grey
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        background.setLayout(new BorderLayout());
        add(background);

        // === Top Panel (Buttons + Title) ===
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        topPanel.setOpaque(false);

        JLabel lblTitle = new JLabel("📚 Welcome, " + u.getFullName() + " (User)");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTitle.setForeground(Color.DARK_GRAY);

        JButton btnRequest = new JButton("Request to Borrow");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnHistory = new JButton("View My History");
        JButton btnBack = new JButton("Back");
        JButton btnLogout = new JButton("Logout");

        // === Style Buttons ===
        JButton[] buttons = {btnRequest, btnRefresh, btnHistory, btnBack, btnLogout};
        for (JButton b : buttons) {
            b.setFocusPainted(false);
            b.setFont(new Font("Tahoma", Font.BOLD, 12));
            b.setForeground(Color.WHITE);
        }
        btnRequest.setBackground(new Color(46, 134, 193)); // blue
        btnRefresh.setBackground(new Color(40, 180, 99));  // green
        btnHistory.setBackground(new Color(155, 89, 182)); // purple
        btnBack.setBackground(new Color(243, 156, 18));    // orange
        btnLogout.setBackground(new Color(231, 76, 60));   // red

        topPanel.add(lblTitle);
        background.add(topPanel, BorderLayout.NORTH);

        // === Table ===
        model = new DefaultTableModel(new Object[]{"ID", "Title", "Author", "ISBN", "Quantity"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        bookTable = new JTable(model);
        refreshBooks();

        JScrollPane scrollPane = new JScrollPane(bookTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Available Books"));
        background.add(scrollPane, BorderLayout.CENTER);

        // === Bottom Button Bar ===
        JPanel buttonBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonBar.setOpaque(false);
        buttonBar.add(btnRequest);
        buttonBar.add(btnRefresh);
        buttonBar.add(btnHistory);
        buttonBar.add(btnBack);
        buttonBar.add(btnLogout);
        background.add(buttonBar, BorderLayout.SOUTH);

        // === Actions ===
        btnRefresh.addActionListener(e -> refreshBooks());
        btnRequest.addActionListener(e -> requestBorrow());
        btnHistory.addActionListener(e -> new UserHistoryFrame(user).setVisible(true));

        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                dispose();
            }
        });
    }

    private void refreshBooks() {
        model.setRowCount(0);
        List<Book> books = BookDAO.getAllBooks();
        for (Book b : books) {
            model.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.getQuantity()});
        }
    }

    private void requestBorrow() {
        int row = bookTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a book first.");
            return;
        }
        int bookId = (int) model.getValueAt(row, 0);
        boolean ok = BorrowRequestDAO.createRequest(user.getId(), bookId);
        JOptionPane.showMessageDialog(this, ok ? "Request submitted successfully." : "Failed to submit request.");
    }

    public static void main(String[] args) {
        // For quick UI testing (you can remove later)
        User dummy = new User();
        dummy.setFullName("John Doe");
        SwingUtilities.invokeLater(() -> new UserDashboard(dummy).setVisible(true));
    }
}
