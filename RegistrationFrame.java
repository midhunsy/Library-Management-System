package library;

import javax.swing.*;
import java.awt.*;

public class RegistrationFrame extends JFrame {
    private JTextField txtFull = new JTextField(20);
    private JTextField txtPhone = new JTextField(15);
    private JTextField txtUser = new JTextField(20);
    private JPasswordField txtPass = new JPasswordField(20);
    private JPasswordField txtPass2 = new JPasswordField(20);

    public RegistrationFrame() {
        setTitle("New User Registration");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        // === Gradient Background Panel ===
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color c1 = new Color(0, 123, 255);  // Blue top
                Color c2 = new Color(210, 214, 222); // Light gray bottom
                GradientPaint gp = new GradientPaint(0, 0, c1, 0, getHeight(), c2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        background.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        // === Title Label ===
        JLabel lblTitle = new JLabel("📝 User Registration", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        background.add(lblTitle, c);
        c.gridwidth = 1;

        // === Input Fields ===
        c.gridy = 1;
        c.gridx = 0;
        background.add(new JLabel("Full Name:"), c);
        c.gridx = 1;
        background.add(txtFull, c);

        c.gridy = 2;
        c.gridx = 0;
        background.add(new JLabel("Phone:"), c);
        c.gridx = 1;
        background.add(txtPhone, c);

        c.gridy = 3;
        c.gridx = 0;
        background.add(new JLabel("Username:"), c);
        c.gridx = 1;
        background.add(txtUser, c);

        c.gridy = 4;
        c.gridx = 0;
        background.add(new JLabel("Password:"), c);
        c.gridx = 1;
        background.add(txtPass, c);

        c.gridy = 5;
        c.gridx = 0;
        background.add(new JLabel("Confirm Password:"), c);
        c.gridx = 1;
        background.add(txtPass2, c);

        // === Buttons ===
        JButton btnRegister = new JButton("Register");
        JButton btnBack = new JButton("Back to Login");

        btnRegister.setBackground(new Color(34, 139, 34)); // Green
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnRegister.setFocusPainted(false);

        btnBack.setBackground(new Color(220, 53, 69)); // Red
        btnBack.setForeground(Color.WHITE);
        btnBack.setFont(new Font("Tahoma", Font.BOLD, 13));
        btnBack.setFocusPainted(false);

        c.gridy = 6;
        c.gridx = 0;
        background.add(btnRegister, c);
        c.gridx = 1;
        background.add(btnBack, c);

        add(background);

        // === Button Actions ===
        btnRegister.addActionListener(e -> register());
        btnBack.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            this.dispose();
        });
    }

    private void register() {
        String fn = txtFull.getText().trim();
        String phone = txtPhone.getText().trim();
        String username = txtUser.getText().trim();
        String p1 = new String(txtPass.getPassword());
        String p2 = new String(txtPass2.getPassword());

        if (fn.isEmpty() || username.isEmpty() || p1.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill required fields.");
            return;
        }
        if (!p1.equals(p2)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        User u = new User();
        u.setFullName(fn);
        u.setPhone(phone);
        u.setUsername(username);
        u.setPassword(p1);
        u.setRole("USER");

        if (UserDAO.registerUser(u)) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now login.");
            new LoginFrame().setVisible(true);
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Registration failed. Username may already exist.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationFrame().setVisible(true));
    }
}
