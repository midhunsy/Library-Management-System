package library;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BorrowRequestDAO {

    // Create a new borrow request
    public static boolean createRequest(int userId, int bookId) {
        String sql = "INSERT INTO borrow_requests (user_id, book_id) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, bookId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Fixed method: get all borrow requests (for Librarian History view)
    public static List<Request> getAllRequests() {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT br.*, u.username, b.title " +
                     "FROM borrow_requests br " +
                     "JOIN users u ON br.user_id = u.id " +
                     "JOIN books b ON br.book_id = b.id " +
                     "ORDER BY br.request_date DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Request r = new Request();
                r.id = rs.getInt("id");
                r.userId = rs.getInt("user_id");
                r.bookId = rs.getInt("book_id");
                r.status = rs.getString("status");
                r.requestDate = rs.getString("request_date");
                r.username = rs.getString("username");
                r.bookTitle = rs.getString("title");
                r.returnDate = null;
                list.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Request inner class
    public static class Request {
        public int id, userId, bookId;
        public String status, requestDate, username, bookTitle;
        public Object returnDate;
    }

    // Get only pending requests
    public static List<Request> getPendingRequests() {
        List<Request> list = new ArrayList<>();
        String sql = "SELECT br.*, u.username, b.title FROM borrow_requests br " +
                     "JOIN users u ON br.user_id = u.id " +
                     "JOIN books b ON br.book_id = b.id WHERE br.status = 'PENDING'";
        try (Connection conn = DBConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Request r = new Request();
                r.id = rs.getInt("id");
                r.userId = rs.getInt("user_id");
                r.bookId = rs.getInt("book_id");
                r.status = rs.getString("status");
                r.requestDate = rs.getString("request_date");
                r.username = rs.getString("username");
                r.bookTitle = rs.getString("title");
                list.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Update request status
    public static boolean updateRequestStatus(int requestId, String newStatus, String comment) {
        String sql = "UPDATE borrow_requests SET status = ?, librarian_comment = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newStatus);
            ps.setString(2, comment);
            ps.setInt(3, requestId);
            boolean updated = ps.executeUpdate() > 0;

            if (updated && "ACCEPTED".equalsIgnoreCase(newStatus)) {
                // Create issue_history entry and decrease quantity
                try (PreparedStatement ps2 = conn.prepareStatement(
                        "INSERT INTO issue_history (user_id, book_id, status) " +
                        "SELECT user_id, book_id, 'ISSUED' FROM borrow_requests WHERE id = ?")) {
                    ps2.setInt(1, requestId);
                    ps2.executeUpdate();
                }

                try (PreparedStatement ps3 = conn.prepareStatement(
                        "UPDATE books SET quantity = quantity - 1 " +
                        "WHERE id = (SELECT book_id FROM borrow_requests WHERE id = ?)")) {
                    ps3.setInt(1, requestId);
                    ps3.executeUpdate();
                }
            }

            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
