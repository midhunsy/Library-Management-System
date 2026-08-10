package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class LibrarianDashboard extends JFrame {
    private User librarian;
    private DefaultTableModel reqModel;

    public LibrarianDashboard(User u) {
        this.librarian = u;
        setTitle("Librarian Dashboard - " + u.getFullName());
        setSize(950, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Top panel with buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton btnAccept = new JButton("Accept");
        JButton btnReject = new JButton("Reject");
        JButton btnRefresh = new JButton("Refresh");
        JButton btnManageBooks = new JButton("Book Management");
        JButton btnViewHistory = new JButton("View History");
        JButton btnLogout = new JButton("Logout");

        topPanel.add(btnAccept);
        topPanel.add(btnReject);
        topPanel.add(btnRefresh);
        topPanel.add(btnManageBooks);
        topPanel.add(btnViewHistory);
        topPanel.add(btnLogout);

        add(topPanel, BorderLayout.NORTH);

        // Request table
        reqModel = new DefaultTableModel(new Object[]{"Req ID", "User", "Book", "Date", "Status"}, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable reqTable = new JTable(reqModel);
        add(new JScrollPane(reqTable), BorderLayout.CENTER);

        // Load requests
        refreshRequests();

        // Button actions
        btnRefresh.addActionListener(e -> refreshRequests());
        btnAccept.addActionListener(e -> handleRequest(reqTable, "ACCEPTED"));
        btnReject.addActionListener(e -> handleRequest(reqTable, "REJECTED"));
        btnManageBooks.addActionListener(e -> new BookManagementFrame().setVisible(true));
        btnViewHistory.addActionListener(e -> new LibrarianHistoryFrame().setVisible(true));

        btnLogout.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                new LoginFrame().setVisible(true);
                this.dispose();
            }
        });
    }

    private void refreshRequests() {
        reqModel.setRowCount(0);
        List<BorrowRequestDAO.Request> list = BorrowRequestDAO.getPendingRequests();
        for (BorrowRequestDAO.Request r : list) {
            reqModel.addRow(new Object[]{r.id, r.username, r.bookTitle, r.requestDate, r.status});
        }
    }

    private void handleRequest(JTable table, String action) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a request.");
            return;
        }
        int reqId = (int) table.getValueAt(row, 0);
        String comment = JOptionPane.showInputDialog(this, "Optional comment:");
        boolean ok = BorrowRequestDAO.updateRequestStatus(reqId, action, comment);
        JOptionPane.showMessageDialog(this, ok ? action + " done." : "Failed.");
        refreshRequests();
    }
}
