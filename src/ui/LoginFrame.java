package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * 登录窗口
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private UserDAO userDAO = new UserDAO();

    public LoginFrame() {
        setTitle("用户管理系统 - 登录");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 300);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(45, 45, 48));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel title = new JLabel("用户管理系统", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 22));
        title.setForeground(new Color(0, 200, 255));
        panel.add(title, gbc);

        // 用户名
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        JLabel ul = new JLabel("用户名:");
        ul.setForeground(Color.LIGHT_GRAY);
        ul.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        panel.add(ul, gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(15);
        styleField(usernameField);
        panel.add(usernameField, gbc);

        // 密码
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel pl = new JLabel("密  码:");
        pl.setForeground(Color.LIGHT_GRAY);
        pl.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        panel.add(pl, gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(15);
        styleField(passwordField);
        panel.add(passwordField, gbc);

        // 登录按钮
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        loginButton = new JButton("登  录");
        loginButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        loginButton.setBackground(new Color(0, 150, 200));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.add(loginButton, gbc);

        // 提示
        gbc.gridy = 4;
        JLabel hint = new JLabel("默认: admin / admin123", SwingConstants.CENTER);
        hint.setForeground(new Color(120, 120, 130));
        hint.setFont(new Font("Microsoft YaHei", Font.PLAIN, 11));
        panel.add(hint, gbc);

        add(panel);

        // 事件
        loginButton.addActionListener(e -> login());
        passwordField.addActionListener(e -> login());
        getRootPane().setDefaultButton(loginButton);
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        field.setBackground(new Color(60, 60, 65));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 85)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名和密码", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = userDAO.authenticate(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this, "用户名或密码错误，或账号已被禁用", "登录失败", JOptionPane.ERROR_MESSAGE);
            return;
        }

        new MainFrame(user).setVisible(true);
        this.dispose();
    }
}
