import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;

public class DreamCurveApp extends JFrame {
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JRadioButton studentRadioButton, mentorRadioButton;
    private boolean isLogin = false; // To track whether we're in Login or Sign Up mode

    public DreamCurveApp() {
        setTitle("DreamCurve");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null); // Center the window on the screen

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createFrontPage(), "FrontPage");
        mainPanel.add(createLoginSignUpPage(true), "LoginPage");
        mainPanel.add(createLoginSignUpPage(false), "SignUpPage");

        add(mainPanel);
        setVisible(true);
    }

    // Method to create the Front Page with "Login" and "Sign Up" options
    private JPanel createFrontPage() {
        JPanel frontPagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw the background image
                g.drawImage(new ImageIcon("path/to/your/background.jpg").getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
        frontPagePanel.setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("DreamCurve");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 20));
        loginButton.addActionListener(e -> {
            isLogin = true;
            cardLayout.show(mainPanel, "LoginPage");
        });

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 20));
        signUpButton.addActionListener(e -> {
            isLogin = false;
            cardLayout.show(mainPanel, "SignUpPage");
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.add(loginButton);
        buttonPanel.add(signUpButton);

        frontPagePanel.add(titleLabel);
        frontPagePanel.add(Box.createVerticalStrut(30));
        frontPagePanel.add(buttonPanel);

        return frontPagePanel;
    }

    // Method to create either the Login or Sign Up Page based on isLogin flag
    private JPanel createLoginSignUpPage(boolean isLoginPage) {
        JPanel pagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Apply a gradient background
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(0, 0, Color.ORANGE, getWidth(), getHeight(), Color.RED);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        pagePanel.setLayout(new GridBagLayout());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(isLoginPage ? "Account Login" : "Account Sign Up");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        usernameField = new JTextField(15);
        usernameField.setMaximumSize(new Dimension(300, 30));
        usernameField.setFont(new Font("Arial", Font.PLAIN, 16));

        passwordField = new JPasswordField(15);
        passwordField.setMaximumSize(new Dimension(300, 30));
        passwordField.setFont(new Font("Arial", Font.PLAIN, 16));

        studentRadioButton = new JRadioButton("Student");
        mentorRadioButton = new JRadioButton("Mentor");
        ButtonGroup group = new ButtonGroup();
        group.add(studentRadioButton);
        group.add(mentorRadioButton);

        studentRadioButton.setOpaque(false);
        mentorRadioButton.setOpaque(false);
        studentRadioButton.setForeground(Color.WHITE);
        mentorRadioButton.setForeground(Color.WHITE);

        JButton submitButton = new JButton(isLoginPage ? "Login" : "Sign Up");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.setFont(new Font("Arial", Font.BOLD, 16));
        submitButton.setBackground(new Color(128, 0, 0));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(isLoginPage ? new LoginButtonListener() : new SignUpButtonListener());

        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(new JLabel("Username", JLabel.CENTER));
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(new JLabel("Password", JLabel.CENTER));
        mainPanel.add(passwordField);
        mainPanel.add(studentRadioButton);
        mainPanel.add(mentorRadioButton);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(submitButton);

        pagePanel.add(mainPanel);
        return pagePanel;
    }

    // ActionListener for Sign Up button
    private class SignUpButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String userType = studentRadioButton.isSelected() ? "Student" : "Mentor";

            try {
                String hashedPassword = hashPassword(password);
                insertSignUpData(username, hashedPassword, userType);
                JOptionPane.showMessageDialog(DreamCurveApp.this, "Sign Up Successful!");
                cardLayout.show(mainPanel, "FrontPage");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(DreamCurveApp.this, "Sign Up failed. Please try again.");
                cardLayout.show(mainPanel, "FrontPage"); // Redirect to front page on failure
            }
        }
    }

    // ActionListener for Login button
    private class LoginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String userType = studentRadioButton.isSelected() ? "Student" : "Mentor";

            try {
                String hashedPassword = hashPassword(password);
                if (validateLogin(username, hashedPassword, userType)) {
                    JOptionPane.showMessageDialog(DreamCurveApp.this, "Login Successful!");
                    cardLayout.show(mainPanel, "FrontPage");
                } else {
                    JOptionPane.showMessageDialog(DreamCurveApp.this, "Login failed. Please check your credentials and try again.");
                    cardLayout.show(mainPanel, "FrontPage"); // Redirect to front page on failure
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(DreamCurveApp.this, "Login failed. Please try again.");
                cardLayout.show(mainPanel, "FrontPage"); // Redirect to front page on failure
            }
        }
    }

    // Method to insert sign-up data into the database
    private void insertSignUpData(String username, String hashedPassword, String userType) throws SQLException {
        String dbUrl = "jdbc:postgresql://localhost:5432/DreamCurve";
        String dbUsername = "postgres";
        String dbPassword = "rishi@123";

        String tableName = userType.equals("Student") ? "signup_student" : "signup_mentor";
        String insertQuery = "INSERT INTO " + tableName + " (username, password) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.executeUpdate();
        }
    }

    // Method to validate login credentials
    private boolean validateLogin(String username, String hashedPassword, String userType) throws SQLException {
        String dbUrl = "jdbc:postgresql://localhost:5432/DreamCurve";
        String dbUsername = "postgres";
        String dbPassword = "rishi@123";

        // Use the correct table based on user type (Student or Mentor)
        String tableName = userType.equals("Student") ? "signup_student" : "signup_mentor";
        String query = "SELECT * FROM " + tableName + " WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            ResultSet rs = stmt.executeQuery();

            // Check if any matching record exists for the provided username and hashed password
            if (rs.next()) {
                return true;  // Login successful
            } else {
                return false; // Login failed: no match
            }
        }
    }

    // Method to hash passwords
    private String hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();

        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DreamCurveApp::new);
    }
}
