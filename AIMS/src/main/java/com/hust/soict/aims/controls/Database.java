package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.*;

import java.sql.*;
import java.util.*;

public class Database {
    private static final String DB_FILE = "aims.db";
    private static final String URL = "jdbc:sqlite:" + DB_FILE;
    
    /**
     * Functional interface for setting PreparedStatement parameters
     */
    @FunctionalInterface
    private interface PreparedStatementSetter {
        void set(PreparedStatement ps) throws SQLException;
    }

    public static void initDatabase() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            try (Statement st = conn.createStatement()) {
                st.execute("CREATE TABLE IF NOT EXISTS products (id INTEGER PRIMARY KEY AUTOINCREMENT, type TEXT, title TEXT, originalValue REAL, currentPrice REAL, weight REAL, dimension TEXT, description TEXT, extra TEXT)");
                // ensure stock column exists
                if (!hasColumn(conn, "products", "stock")) {
                    try (Statement st2 = conn.createStatement()) {
                        st2.execute("ALTER TABLE products ADD COLUMN stock INTEGER DEFAULT 10");
                    } catch (SQLException ex) {
                        // ignore
                    }
                }
            }

            // seed some data if empty
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count == 0) seed(conn);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean hasColumn(Connection conn, String table, String column) throws SQLException {
        String q = "PRAGMA table_info(" + table + ")";
        try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(q)) {
            while (rs.next()) {
                String name = rs.getString("name");
                if (column.equalsIgnoreCase(name)) return true;
            }
        }
        return false;
    }

    private static void seed(Connection conn) throws SQLException {
        String insert = "INSERT INTO products(type,title,originalValue,currentPrice,weight,dimension,description,extra) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(insert)) {
            // add 15 books
            for (int i = 1; i <= 15; i++) {
                ps.setString(1, "book");
                ps.setString(2, "Book title " + i);
                ps.setDouble(3, 100 + i);
                ps.setDouble(4, 80 + i);
                ps.setDouble(5, 0.5 + i * 0.01);
                ps.setString(6, "15x20cm");
                ps.setString(7, "A great book.");
                String extra = "author=Author " + i + ";;coverType=paperback;;publisher=Pub " + i + ";;publicationDate=2020-01-0" + ((i%9)+1) + "";
                ps.setString(8, extra);
                ps.executeUpdate();
            }

            // add 10 newspapers
            for (int i = 1; i <= 10; i++) {
                ps.setString(1, "newspaper");
                ps.setString(2, "Newspaper " + i);
                ps.setDouble(3, 5.0 + i);
                ps.setDouble(4, 3.0 + i);
                ps.setDouble(5, 0.2);
                ps.setString(6, "30x40cm");
                ps.setString(7, "Daily news.");
                String extra = "editorInChief=Editor " + i + ";;publisher=NewsPub;;publicationDate=2025-10-0" + ((i%9)+1) + ";;issueNumber=" + i + ";;publicationFrequency=daily;;issn=1234-" + i + ";;language=Vietnamese";
                ps.setString(8, extra);
                ps.executeUpdate();
            }

            // add 12 CDs
            for (int i = 1; i <= 12; i++) {
                ps.setString(1, "cd");
                ps.setString(2, "Album " + i);
                ps.setDouble(3, 15.0 + i);
                ps.setDouble(4, 12.0 + i);
                ps.setDouble(5, 0.1);
                ps.setString(6, "12cm");
                ps.setString(7, "Music album.");
                String extra = "album=Album " + i + ";;artist=Artist " + i + ";;recordLabel=Label " + i + ";;genre=Pop;;releaseDate=2019-05-0" + ((i%9)+1);
                ps.setString(8, extra);
                ps.executeUpdate();
            }

            // add 13 DVDs
            for (int i = 1; i <= 13; i++) {
                ps.setString(1, "dvd");
                ps.setString(2, "Movie " + i);
                ps.setDouble(3, 25.0 + i);
                ps.setDouble(4, 20.0 + i);
                ps.setDouble(5, 0.2);
                ps.setString(6, "14x19cm");
                ps.setString(7, "A movie.");
                String extra = "discType=Blu-ray;;director=Director " + i + ";;runtime=120min;;studio=Studio " + i + ";;language=English;;subtitles=Vietnamese;;releaseDate=2018-0" + ((i%9)+1);
                ps.setString(8, extra);
                ps.executeUpdate();
            }
        }
    }

    public static int countProducts() {
        try (Connection conn = DriverManager.getConnection(URL); Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static int getStock(long productId) {
        String q = "SELECT stock FROM products WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("stock");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public static boolean reduceStock(long productId, int amount) {
        // check current
        int current = getStock(productId);
        if (current < amount) return false;
        String u = "UPDATE products SET stock = stock - ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement ps = conn.prepareStatement(u)) {
            ps.setInt(1, amount);
            ps.setLong(2, productId);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) { e.printStackTrace(); }
        return false;
    }

    public static void setStock(long productId, int stock) {
        String u = "UPDATE products SET stock = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement ps = conn.prepareStatement(u)) {
            ps.setInt(1, stock);
            ps.setLong(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public static List<Product> getProducts(int offset, int limit) {
        return queryProducts("SELECT id,type,title,originalValue,currentPrice,weight,dimension,description,extra FROM products ORDER BY id LIMIT ? OFFSET ?", 
                            stmt -> {
                                stmt.setInt(1, limit);
                                stmt.setInt(2, offset);
                            });
    }
    
    /**
     * Search products by title (case-insensitive)
     * @param searchTerm Search term to match against product title
     * @param offset Starting offset
     * @param limit Maximum number of results
     * @return List of matching products
     */
    public static List<Product> searchProducts(String searchTerm, int offset, int limit) {
        return queryProducts("SELECT id,type,title,originalValue,currentPrice,weight,dimension,description,extra FROM products WHERE LOWER(title) LIKE ? ORDER BY id LIMIT ? OFFSET ?",
                            stmt -> {
                                stmt.setString(1, "%" + searchTerm.toLowerCase() + "%");
                                stmt.setInt(2, limit);
                                stmt.setInt(3, offset);
                            });
    }
    
    /**
     * Count products matching search term
     * @param searchTerm Search term
     * @return Count of matching products
     */
    public static int countSearchResults(String searchTerm) {
        String q = "SELECT COUNT(*) FROM products WHERE LOWER(title) LIKE ?";
        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setString(1, "%" + searchTerm.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }
    
    /**
     * Helper method to execute product query with custom PreparedStatement setup
     */
    private static List<Product> queryProducts(String sql, PreparedStatementSetter setter) {
        List<Product> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(URL); PreparedStatement ps = conn.prepareStatement(sql)) {
            setter.set(ps);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String type = rs.getString("type");
                    String title = rs.getString("title");
                    double original = rs.getDouble("originalValue");
                    double current = rs.getDouble("currentPrice");
                    double weight = rs.getDouble("weight");
                    String dimension = rs.getString("dimension");
                    String desc = rs.getString("description");
                    String extra = rs.getString("extra");

                    Map<String,String> m = parseExtra(extra);

                    Product p = null;
                    switch (type) {
                        case "book": {
                            Book b = new Book(id, title, original, current, weight, dimension, desc,
                                    m.getOrDefault("author",""), m.getOrDefault("coverType",""), m.getOrDefault("publisher",""), m.getOrDefault("publicationDate",""));
                            if (m.containsKey("numberOfPages")) try { b.setNumberOfPages(Integer.parseInt(m.get("numberOfPages"))); } catch (Exception ignored) {}
                            b.setLanguage(m.getOrDefault("language", ""));
                            b.setGenre(m.getOrDefault("genre",""));
                            p = b; break;
                        }
                        case "newspaper": {
                            Newspaper n = new Newspaper(id, title, original, current, weight, dimension, desc,
                                    m.getOrDefault("editorInChief",""), m.getOrDefault("publisher",""), m.getOrDefault("publicationDate",""));
                            n.setIssueNumber(m.getOrDefault("issueNumber",""));
                            n.setPublicationFrequency(m.getOrDefault("publicationFrequency",""));
                            n.setIssn(m.getOrDefault("issn",""));
                            n.setLanguage(m.getOrDefault("language",""));
                            n.setSections(m.getOrDefault("sections",""));
                            p = n; break;
                        }
                        case "cd": {
                            CD c = new CD(id, title, original, current, weight, dimension, desc,
                                    m.getOrDefault("album",""), m.getOrDefault("artist",""), m.getOrDefault("recordLabel",""));
                            c.setGenre(m.getOrDefault("genre",""));
                            c.setReleaseDate(m.getOrDefault("releaseDate",""));
                            if (m.containsKey("trackList")) c.setTrackList(Arrays.asList(m.get("trackList").split("\\|")));
                            p = c; break;
                        }
                        case "dvd": {
                            DVD d = new DVD(id, title, original, current, weight, dimension, desc,
                                    m.getOrDefault("discType",""), m.getOrDefault("director",""));
                            d.setRuntime(m.getOrDefault("runtime",""));
                            d.setStudio(m.getOrDefault("studio",""));
                            d.setLanguage(m.getOrDefault("language",""));
                            d.setSubtitles(m.getOrDefault("subtitles",""));
                            d.setReleaseDate(m.getOrDefault("releaseDate",""));
                            d.setGenre(m.getOrDefault("genre",""));
                            p = d; break;
                        }
                        default: {
                            Product prod = new Product(id, title, original, current, weight, dimension, desc);
                            p = prod; break;
                        }
                    }
                    list.add(p);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    private static Map<String,String> parseExtra(String extra) {
        Map<String,String> m = new HashMap<>();
        if (extra == null) return m;
        String[] parts = extra.split(";;");
        for (String p: parts) {
            int idx = p.indexOf('=');
            if (idx>0) {
                String k = p.substring(0, idx);
                String v = p.substring(idx+1);
                m.put(k, v);
            }
        }
        return m;
    }
}
