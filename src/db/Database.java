package db;

import java.sql.*;

/**
 * SQLite 数据库管理 — 建表 + 默认管理员
 */
public class Database {
    private static final String DB_URL = "jdbc:sqlite:userdb.sqlite";
    private static Connection conn;

    public static Connection getConnection() throws SQLException {
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(DB_URL);
            conn.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    public static void init() {
        try {
            Class.forName("org.sqlite.JDBC");
            Connection c = getConnection();

            String sql = """
                CREATE TABLE IF NOT EXISTS users (
                    id         INTEGER PRIMARY KEY AUTOINCREMENT,
                    username   TEXT    NOT NULL UNIQUE,
                    password   TEXT    NOT NULL,
                    role       TEXT    NOT NULL DEFAULT 'USER',
                    email      TEXT    DEFAULT '',
                    phone      TEXT    DEFAULT '',
                    status     INTEGER NOT NULL DEFAULT 1,
                    created_at TEXT    NOT NULL DEFAULT (datetime('now','localtime'))
                );
                """;
            c.createStatement().execute(sql);

            // 默认管理员
            String check = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
            ResultSet rs = c.createStatement().executeQuery(check);
            if (rs.next() && rs.getInt(1) == 0) {
                String hash = dao.UserDAO.hashPassword("admin123");
                PreparedStatement ps = c.prepareStatement(
                    "INSERT INTO users (username, password, role, email, phone, status) VALUES (?,?,?,?,?,?)");
                ps.setString(1, "admin");
                ps.setString(2, hash);
                ps.setString(3, "ADMIN");
                ps.setString(4, "admin@example.com");
                ps.setString(5, "13800000000");
                ps.setInt(6, 1);
                ps.executeUpdate();
                ps.close();
                System.out.println("[DB] 默认管理员已创建: admin / admin123");
            }
            rs.close();
            System.out.println("[DB] 数据库初始化完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        try { if (conn != null && !conn.isClosed()) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
    }
}
