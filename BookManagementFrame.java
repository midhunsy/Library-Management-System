package library;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class BookManagementFrame extends JFrame {
    private DefaultTableModel model;
    private JTable table;
    public BookManagementFrame() {
        setTitle("Book Management");
        setSize(700,450);
        setLocationRelativeTo(null);

        model = new DefaultTableModel(new Object[]{"ID","Title","Author","ISBN","Qty"},0) {
            public boolean isCellEditable(int r,int c){return false;}
        };
        table = new JTable(model);
        refreshBooks();

        JButton btnAdd = new JButton("Add Book");
        JButton btnDelete = new JButton("Delete Book");
        JButton btnRefresh = new JButton("Refresh");

        JPanel top = new JPanel();
        top.add(btnAdd); top.add(btnDelete); top.add(btnRefresh);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        btnAdd.addActionListener(e -> addBook());
        btnDelete.addActionListener(e -> deleteSelectedBook());
        btnRefresh.addActionListener(e -> refreshBooks());
    }

    private void refreshBooks() {
        model.setRowCount(0);
        List<Book> books = BookDAO.getAllBooks();
        for (Book b : books) model.addRow(new Object[]{b.getId(), b.getTitle(), b.getAuthor(), b.getIsbn(), b.getQuantity()});
    }

    private void addBook() {
        JTextField tTitle = new JTextField();
        JTextField tAuthor = new JTextField();
        JTextField tISBN = new JTextField();
        JTextField tQty = new JTextField("1");
        Object[] fields = {
            "Title:", tTitle,
            "Author:", tAuthor,
            "ISBN:", tISBN,
            "Quantity:", tQty
        };
        int ok = JOptionPane.showConfirmDialog(this, fields, "Add Book", JOptionPane.OK_CANCEL_OPTION);
        if (ok == JOptionPane.OK_OPTION) {
            Book b = new Book();
            b.setTitle(tTitle.getText().trim());
            b.setAuthor(tAuthor.getText().trim());
            b.setIsbn(tISBN.getText().trim());
            try { b.setQuantity(Integer.parseInt(tQty.getText().trim())); } catch (NumberFormatException e) { b.setQuantity(1); }
            boolean res = BookDAO.addBook(b);
            JOptionPane.showMessageDialog(this, res ? "Book added." : "Failed to add.");
            refreshBooks();
        }
    }

    private void deleteSelectedBook() {
        int row = table.getSelectedRow();
        if (row == -1) { JOptionPane.showMessageDialog(this,"Select book."); return; }
        int id = (int) model.getValueAt(row, 0);
        if (JOptionPane.showConfirmDialog(this, "Delete this book?") == JOptionPane.YES_OPTION) {
            boolean res = BookDAO.deleteBook(id);
            JOptionPane.showMessageDialog(this, res ? "Deleted." : "Failed.");
            refreshBooks();
        }
    }
}
