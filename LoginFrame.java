package library;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginFrame extends JFrame {
    private JTextField txtUsername = new JTextField(20);
    private JPasswordField txtPassword = new JPasswordField(20);
    private JComboBox<String> cbRole = new JComboBox<>(new String[]{"USER", "LIBRARIAN", "ADMIN"});

    public LoginFrame() {
        setTitle("Library System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        // === Gradient Background Panel ===
        JPanel background = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                Color c1 = new Color(60, 141, 188); // blue shade
                Color c2 = new Color(210, 214, 222); // light grey shade
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
        JLabel lblTitle = new JLabel("📚 Library Management System", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitle.setForeground(Color.WHITE);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        background.add(lblTitle, c);
        c.gridwidth = 1;

        // === Username Field ===
        c.gridy = 1;
        c.gridx = 0;
        background.add(new JLabel("Username:"), c);
        c.gridx = 1;
        background.add(txtUsername, c);

        // === Password Field ===
        c.gridy = 2;
        c.gridx = 0;
        background.add(new JLabel("Password:"), c);
        c.gridx = 1;
        background.add(txtPassword, c);

        // === Role ComboBox ===
        c.gridy = 3;
        c.gridx = 0;
        background.add(new JLabel("Login as:"), c);
        c.gridx = 1;
        background.add(cbRole, c);

        // === Buttons ===
        JButton btnLogin = new JButton("Login");
        JButton btnRegister = new JButton("New User Registration");

        btnLogin.setBackground(new Color(34, 139, 34));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(new Font("Tahoma", Font.BOLD, 13));

        btnRegister.setBackground(new Color(0, 123, 255));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.setFocusPainted(false);
        btnRegister.setFont(new Font("Tahoma", Font.BOLD, 13));

        c.gridy = 4;
        c.gridx = 0;
        background.add(btnLogin, c);
        c.gridx = 1;
        background.add(btnRegister, c);

        add(background);

        // === Button Actions ===
        btnLogin.addActionListener(e -> doLogin());
        btnRegister.addActionListener(e -> {
            new RegistrationFrame().setVisible(true);
            this.dispose();
        });
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword()).trim();
        String role = (String) cbRole.getSelectedItem();

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter username & password");
            return;
        }

        User user = UserDAO.authenticate(username, password, role);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Invalid credentials or role.");
            return;
        }

        JOptionPane.showMessageDialog(this, "Welcome, " + user.getFullName());

        switch (role) {
            case "USER":
                new UserDashboard(user).setVisible(true);
                break;
            case "LIBRARIAN":
                new LibrarianDashboard(user).setVisible(true);
                break;
            case "ADMIN":
                new AdminDashboard(user).setVisible(true);
                break;
        }
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
