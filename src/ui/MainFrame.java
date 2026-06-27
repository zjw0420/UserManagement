package ui;

import dao.UserDAO;
import model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * 用户管理主界面
 */
public class MainFrame extends JFrame {
    private User currentUser;
    private UserDAO userDAO = new UserDAO();
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JLabel infoLabel;

    public MainFrame(User user) {
        this.currentUser = user;
        setTitle("用户管理系统 — " + user.getUsername() + " (" + user.getRoleText() + ")");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(960, 640);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));

        // 整体布局
        setLayout(new BorderLayout());

        // === 顶部工具栏 ===
        add(buildToolbar(), BorderLayout.NORTH);

        // === 中央表格 ===
        add(buildTablePanel(), BorderLayout.CENTER);

        // === 底部状态栏 ===
        add(buildStatusBar(), BorderLayout.SOUTH);

        refreshTable();
    }

    // ==================== 工具栏 ====================

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(55, 55, 60));
        bar.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

        // 左侧按钮组
        JPanel btnGroup = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnGroup.setBackground(new Color(55, 55, 60));

        btnGroup.add(makeButton("➕ 新增用户", new Color(0, 160, 80), e -> addUser()));
        btnGroup.add(makeButton("✏ 编辑", new Color(0, 140, 200), e -> editUser()));
        btnGroup.add(makeButton("🗑 删除", new Color(200, 60, 60), e -> deleteUser()));
        btnGroup.add(makeButton("🔑 重置密码", new Color(200, 150, 0), e -> resetPassword()));
        btnGroup.add(makeButton("🔄 刷新", new Color(100, 100, 110), e -> refreshTable()));

        bar.add(btnGroup, BorderLayout.WEST);

        // 右侧搜索
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        searchPanel.setBackground(new Color(55, 55, 60));

        searchField = new JTextField(15);
        searchField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        searchField.setBackground(new Color(70, 70, 75));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 95)),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        searchField.putClientProperty("JTextField.placeholderText", "搜索用户名/邮箱/手机...");
        searchPanel.add(searchField);

        JButton searchBtn = makeButton("🔍 搜索", new Color(80, 80, 180), e -> search());
        searchPanel.add(searchBtn);

        bar.add(searchPanel, BorderLayout.EAST);
        return bar;
    }

    // ==================== 表格 ====================

    private JScrollPane buildTablePanel() {
        String[] cols = {"ID", "用户名", "角色", "邮箱", "手机", "状态", "创建时间"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setRowHeight(32);
        table.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        table.setForeground(new Color(220, 220, 225));
        table.setBackground(new Color(40, 40, 45));
        table.setGridColor(new Color(65, 65, 70));
        table.setSelectionBackground(new Color(0, 120, 200));
        table.getTableHeader().setFont(new Font("Microsoft YaHei", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(55, 55, 60));
        table.getTableHeader().setForeground(new Color(180, 180, 190));
        table.getTableHeader().setReorderingAllowed(false);

        // 列宽
        table.getColumnModel().getColumn(0).setPreferredWidth(40);
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(180);
        table.getColumnModel().getColumn(4).setPreferredWidth(130);
        table.getColumnModel().getColumn(5).setPreferredWidth(60);
        table.getColumnModel().getColumn(6).setPreferredWidth(150);

        // 居中对齐
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(2).setCellRenderer(center);
        table.getColumnModel().getColumn(5).setCellRenderer(center);

        // 双击编辑
        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) editUser();
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(new Color(40, 40, 45));
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        return scrollPane;
    }

    // ==================== 状态栏 ====================

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(new Color(55, 55, 60));
        bar.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        infoLabel = new JLabel(" ");
        infoLabel.setForeground(new Color(150, 150, 160));
        infoLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        bar.add(infoLabel, BorderLayout.WEST);

        JLabel logout = new JLabel("退出登录");
        logout.setForeground(new Color(200, 120, 120));
        logout.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        logout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logout.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) { logout(); }
        });
        bar.add(logout, BorderLayout.EAST);
        return bar;
    }

    // ==================== 操作 ====================

    private void refreshTable() {
        List<User> list = userDAO.getAll();
        populateTable(list);
        infoLabel.setText("共 " + list.size() + " 个用户");
    }

    private void search() {
        String kw = searchField.getText().trim();
        if (kw.isEmpty()) { refreshTable(); return; }
        List<User> list = userDAO.search(kw);
        populateTable(list);
        infoLabel.setText("搜索 '" + kw + "': 找到 " + list.size() + " 条");
    }

    private void populateTable(List<User> list) {
        tableModel.setRowCount(0);
        for (User u : list) {
            tableModel.addRow(new Object[]{
                u.getId(), u.getUsername(), u.getRoleText(),
                u.getEmail(), u.getPhone(), u.getStatusText(),
                u.getCreatedAt() != null ? u.getCreatedAt().toString().replace("T", " ") : ""
            });
        }
    }

    private void addUser() {
        UserDialog dialog = new UserDialog(this, "新增用户", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            User u = dialog.getUser();
            if (userDAO.usernameExists(u.getUsername(), 0)) {
                JOptionPane.showMessageDialog(this, "用户名已存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (userDAO.insert(u)) {
                JOptionPane.showMessageDialog(this, "新增成功");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "新增失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editUser() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "请先选择一个用户", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int id = (int) tableModel.getValueAt(row, 0);
        User old = userDAO.getById(id);
        if (old == null) { JOptionPane.showMessageDialog(this, "用户不存在"); return; }

        if (!currentUser.getRole().equals("ADMIN") && currentUser.getId() != id) {
            JOptionPane.showMessageDialog(this, "无权编辑其他用户", "权限不足", JOptionPane.ERROR_MESSAGE);
            return;
        }

        UserDialog dialog = new UserDialog(this, "编辑用户", old);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            User u = dialog.getUser();
            u.setId(id);
            if (userDAO.usernameExists(u.getUsername(), id)) {
                JOptionPane.showMessageDialog(this, "用户名已存在", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (userDAO.update(u)) {
                JOptionPane.showMessageDialog(this, "更新成功");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "更新失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteUser() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "请先选择一个用户"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);
        if (id == currentUser.getId()) {
            JOptionPane.showMessageDialog(this, "不能删除自己", "提示", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!currentUser.getRole().equals("ADMIN")) {
            JOptionPane.showMessageDialog(this, "无权删除用户", "权限不足", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int r = JOptionPane.showConfirmDialog(this, "确认删除用户 \"" + name + "\" ?", "确认删除", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            if (userDAO.delete(id)) {
                JOptionPane.showMessageDialog(this, "删除成功");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "删除失败", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetPassword() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "请先选择一个用户"); return; }
        int id = (int) tableModel.getValueAt(row, 0);
        String name = (String) tableModel.getValueAt(row, 1);

        JPasswordField pf1 = new JPasswordField(15);
        JPasswordField pf2 = new JPasswordField(15);
        JPanel p = new JPanel(new GridLayout(2, 2, 8, 8));
        p.add(new JLabel("新密码:")); p.add(pf1);
        p.add(new JLabel("确认密码:")); p.add(pf2);

        int r = JOptionPane.showConfirmDialog(this, p, "重置密码 - " + name, JOptionPane.OK_CANCEL_OPTION);
        if (r != JOptionPane.OK_OPTION) return;

        String pw1 = new String(pf1.getPassword());
        String pw2 = new String(pf2.getPassword());
        if (pw1.isEmpty()) { JOptionPane.showMessageDialog(this, "密码不能为空"); return; }
        if (!pw1.equals(pw2)) { JOptionPane.showMessageDialog(this, "两次密码不一致"); return; }

        if (userDAO.resetPassword(id, pw1)) {
            JOptionPane.showMessageDialog(this, "密码重置成功");
        } else {
            JOptionPane.showMessageDialog(this, "密码重置失败", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void logout() {
        int r = JOptionPane.showConfirmDialog(this, "确认退出登录？", "退出", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            new LoginFrame().setVisible(true);
            this.dispose();
        }
    }

    // ==================== 工具 ====================

    private JButton makeButton(String text, Color bg, java.awt.event.ActionListener action) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Microsoft YaHei", Font.PLAIN, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        btn.addActionListener(action);
        return btn;
    }
}
