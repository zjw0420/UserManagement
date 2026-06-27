package ui;

import model.User;

import javax.swing.*;
import java.awt.*;

/**
 * 新增 / 编辑用户弹窗
 */
public class UserDialog extends JDialog {
    private boolean confirmed = false;
    private JTextField usernameField, emailField, phoneField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo, statusCombo;

    public UserDialog(JFrame parent, String title, User existing) {
        super(parent, title, true);
        setSize(420, existing == null ? 380 : 310);
        setLocationRelativeTo(parent);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(50, 50, 55));
        panel.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        // 用户名
        addRow(panel, gbc, row++, "用户名:");
        usernameField = new JTextField(existing != null ? existing.getUsername() : "", 18);
        styleField(usernameField);
        panel.add(usernameField, gbc);

        // 密码 (仅新增时显示)
        if (existing == null) {
            addRow(panel, gbc, row++, "密  码:");
            passwordField = new JPasswordField(18);
            styleField(passwordField);
            panel.add(passwordField, gbc);
        }

        // 角色
        addRow(panel, gbc, row++, "角  色:");
        roleCombo = new JComboBox<>(new String[]{"USER", "ADMIN"});
        if (existing != null && "ADMIN".equals(existing.getRole())) roleCombo.setSelectedItem("ADMIN");
        styleCombo(roleCombo);
        panel.add(roleCombo, gbc);

        // 邮箱
        addRow(panel, gbc, row++, "邮  箱:");
        emailField = new JTextField(existing != null ? existing.getEmail() : "", 18);
        styleField(emailField);
        panel.add(emailField, gbc);

        // 手机
        addRow(panel, gbc, row++, "手  机:");
        phoneField = new JTextField(existing != null ? existing.getPhone() : "", 18);
        styleField(phoneField);
        panel.add(phoneField, gbc);

        // 状态
        addRow(panel, gbc, row++, "状  态:");
        statusCombo = new JComboBox<>(new String[]{"正常", "禁用"});
        if (existing != null && existing.getStatus() == 0) statusCombo.setSelectedItem("禁用");
        styleCombo(statusCombo);
        panel.add(statusCombo, gbc);

        // 按钮
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 2;
        gbc.insets = new Insets(16, 8, 0, 8);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnPanel.setBackground(new Color(50, 50, 55));

        JButton ok = new JButton("确  定");
        ok.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        ok.setBackground(new Color(0, 150, 200));
        ok.setForeground(Color.WHITE);
        ok.setFocusPainted(false);
        ok.setCursor(new Cursor(Cursor.HAND_CURSOR));
        ok.addActionListener(e -> onConfirm(existing));

        JButton cancel = new JButton("取  消");
        cancel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        cancel.setBackground(new Color(100, 100, 110));
        cancel.setForeground(Color.WHITE);
        cancel.setFocusPainted(false);
        cancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancel.addActionListener(e -> { confirmed = false; dispose(); });

        btnPanel.add(ok);
        btnPanel.add(cancel);
        panel.add(btnPanel, gbc);

        add(panel);
        getRootPane().setDefaultButton(ok);
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String label) {
        gbc.gridx = 0; gbc.gridy = row; gbc.gridwidth = 1;
        JLabel l = new JLabel(label);
        l.setForeground(Color.LIGHT_GRAY);
        l.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        panel.add(l, gbc);
        gbc.gridx = 1;
    }

    private void onConfirm(User existing) {
        String username = usernameField.getText().trim();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "用户名不能为空"); return;
        }
        if (existing == null) {
            String pw = new String(passwordField.getPassword());
            if (pw.isEmpty()) {
                JOptionPane.showMessageDialog(this, "密码不能为空"); return;
            }
        }
        confirmed = true;
        dispose();
    }

    public boolean isConfirmed() { return confirmed; }

    public User getUser() {
        User u = new User();
        u.setUsername(usernameField.getText().trim());
        if (passwordField != null) {
            u.setPassword(new String(passwordField.getPassword()));
        }
        u.setRole((String) roleCombo.getSelectedItem());
        u.setEmail(emailField.getText().trim());
        u.setPhone(phoneField.getText().trim());
        u.setStatus("正常".equals(statusCombo.getSelectedItem()) ? 1 : 0);
        return u;
    }

    private void styleField(JTextField field) {
        field.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        field.setBackground(new Color(65, 65, 70));
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(85, 85, 90)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    private void styleCombo(JComboBox<String> combo) {
        combo.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        combo.setBackground(new Color(65, 65, 70));
        combo.setForeground(Color.WHITE);
    }
}
