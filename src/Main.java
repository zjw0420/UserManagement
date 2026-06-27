import db.Database;
import ui.LoginFrame;

import javax.swing.*;

/**
 * 程序入口
 */
public class Main {
    public static void main(String[] args) {
        // 设置系统 LookAndFeel
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        // 暗色全局
        UIManager.put("Panel.background", new java.awt.Color(50, 50, 55));
        UIManager.put("OptionPane.background", new java.awt.Color(50, 50, 55));
        UIManager.put("OptionPane.messageForeground", java.awt.Color.LIGHT_GRAY);

        // 初始化数据库
        Database.init();

        // 启动登录窗口
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}
