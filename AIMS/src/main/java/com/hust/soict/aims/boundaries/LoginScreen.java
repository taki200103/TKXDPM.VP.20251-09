package com.hust.soict.aims.boundaries;

import javax.swing.*;
import java.awt.*;
import com.hust.soict.aims.controls.AuthController;
import com.hust.soict.aims.boundaries.manager.ProductManagementScreen;
import com.hust.soict.aims.components.RoundedButton;
import static com.hust.soict.aims.utils.UIConstant.*;

public class LoginScreen extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private BaseScreenHandler parentScreen;

    public LoginScreen(Frame owner, BaseScreenHandler parent) {
        super(owner, "Login", true);
        this.parentScreen = parent;

        setSize(450, 300);
        setLocationRelativeTo(owner);
        setResizable(false);

        setupUI();
        bindEvents();
    }

    private void setupUI() {
        setLayout(new BorderLayout());

        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(PADDING_LARGE);
        mainPanel.setBackground(BACKGROUND_WHITE);

        // Title
        JLabel titleLabel = new JLabel("Welcome to AIMS");
        titleLabel.setFont(FONT_HEADER);
        titleLabel.setForeground(TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(SPACING_LARGE));

        // Email field
        JPanel emailPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        emailPanel.setOpaque(false);
        emailPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(FONT_BODY);
        emailLabel.setPreferredSize(new Dimension(100, 35));

        emailField = new JTextField();
        emailField.setFont(FONT_BODY);
        emailField.setPreferredSize(new Dimension(250, 35));
        emailField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        emailPanel.add(emailLabel);
        emailPanel.add(emailField);
        mainPanel.add(emailPanel);
        mainPanel.add(Box.createVerticalStrut(SPACING_MEDIUM));

        // Password field
        JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        passwordPanel.setOpaque(false);
        passwordPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(FONT_BODY);
        passwordLabel.setPreferredSize(new Dimension(100, 35));

        passwordField = new JPasswordField();
        passwordField.setFont(FONT_BODY);
        passwordField.setPreferredSize(new Dimension(250, 35));
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        passwordPanel.add(passwordLabel);
        passwordPanel.add(passwordField);
        mainPanel.add(passwordPanel);
        mainPanel.add(Box.createVerticalStrut(SPACING_LARGE));

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, SPACING_SMALL, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        RoundedButton loginButton = new RoundedButton("Login", 8);
        loginButton.setFont(FONT_BUTTON);
        loginButton.setBackground(PRIMARY_COLOR);
        loginButton.setForeground(TEXT_ON_PRIMARY);
        loginButton.setCursor(CURSOR_HAND);
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.addActionListener(e -> performLogin());

        RoundedButton cancelButton = new RoundedButton("Cancel", 8);
        cancelButton.setFont(FONT_BUTTON);
        cancelButton.setBackground(BACKGROUND_GRAY);
        cancelButton.setForeground(TEXT_PRIMARY);
        cancelButton.setCursor(CURSOR_HAND);
        cancelButton.setPreferredSize(new Dimension(120, 40));
        cancelButton.addActionListener(e -> setVisible(false));

        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void bindEvents() {
        // Enter key in password field triggers login
        passwordField.addActionListener(e -> performLogin());

        // Enter key in email field moves focus to password
        emailField.addActionListener(e -> passwordField.requestFocus());
    }

    private void performLogin() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both email and password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (AuthController.authenticate(email, password)) {
            // Login successful
            setVisible(false);

            // Navigate to manager main screen
            if (parentScreen != null) {
                com.hust.soict.aims.boundaries.manager.ManagerMainScreen managerScreen = 
                    new com.hust.soict.aims.boundaries.manager.ManagerMainScreen(parentScreen);
                ScreenNavigator.getInstance().navigateTo(managerScreen);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Login successful!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            // Login failed
            JOptionPane.showMessageDialog(this,
                    "Invalid email or password",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
}
