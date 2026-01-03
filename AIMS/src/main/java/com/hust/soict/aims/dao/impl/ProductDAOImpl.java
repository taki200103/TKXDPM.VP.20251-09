package com.hust.soict.aims.dao.impl;

import com.hust.soict.aims.dao.BaseDAO;
import com.hust.soict.aims.dao.ProductDAO;
import com.hust.soict.aims.entities.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Implementation for Product operations
 * Contains SQL queries and database access logic
 */
public class ProductDAOImpl extends BaseDAO implements ProductDAO {
    
    @Override
    public List<Product> getProducts(int offset, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, " +
                     "m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, " +
                     "m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
                     "FROM Media m " +
                     "WHERE m.status = 'active' " +
                     "ORDER BY m.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = mapMediaToProduct(conn, rs);
                    if (p != null) list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public Product getProductById(long productId) {
        String sql = "SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, " +
                     "m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, " +
                     "m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
                     "FROM Media m WHERE m.media_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapMediaToProduct(conn, rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    @Override
    public int getStock(long productId) {
        String sql = "SELECT quantity FROM Media WHERE media_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("quantity");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public boolean reduceStock(long productId, int amount) {
        String sql = "UPDATE Media SET quantity = quantity - ? WHERE media_id = ? AND quantity >= ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, amount);
            ps.setLong(2, productId);
            ps.setInt(3, amount);
            int affected = ps.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public int countProducts() {
        String sql = "SELECT COUNT(*) FROM Media WHERE status = 'active'";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public List<Product> searchProducts(String searchTerm, int offset, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, " +
                     "m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, " +
                     "m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
                     "FROM Media m " +
                     "WHERE m.status = 'active' AND LOWER(m.title) LIKE ? " +
                     "ORDER BY m.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + searchTerm.toLowerCase() + "%");
            ps.setInt(2, limit);
            ps.setInt(3, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = mapMediaToProduct(conn, rs);
                    if (p != null) list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public int countSearchResults(String searchTerm) {
        String sql = "SELECT COUNT(*) FROM Media WHERE status = 'active' AND LOWER(title) LIKE ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + searchTerm.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public List<Product> searchProductsWithFilters(String searchTerm, String category, 
                                                   Double minPrice, Double maxPrice, 
                                                   int offset, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, " +
                     "m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, " +
                     "m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
                     "FROM Media m WHERE m.status = 'active'");
        List<Object> params = new ArrayList<>();
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND LOWER(m.title) LIKE ?");
            params.add("%" + searchTerm.toLowerCase() + "%");
        }
        
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            sql.append(" AND LOWER(m.category) = ?");
            params.add(category.toLowerCase());
        }
        
        if (minPrice != null) {
            sql.append(" AND m.price >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append(" AND m.price <= ?");
            params.add(maxPrice);
        }
        
        sql.append(" ORDER BY m.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = mapMediaToProduct(conn, rs);
                    if (p != null) list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public int countFilteredResults(String searchTerm, String category, 
                                    Double minPrice, Double maxPrice) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM Media WHERE status = 'active'");
        List<Object> params = new ArrayList<>();
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND LOWER(title) LIKE ?");
            params.add("%" + searchTerm.toLowerCase() + "%");
        }
        
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            sql.append(" AND LOWER(category) = ?");
            params.add(category.toLowerCase());
        }
        
        if (minPrice != null) {
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public List<Product> getAllProductsForManagement(int offset, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, " +
                     "m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, " +
                     "m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
                     "FROM Media m " +
                     "ORDER BY m.created_at DESC LIMIT ? OFFSET ?";
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = mapMediaToProduct(conn, rs);
                    if (p != null) list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<Product> searchProductsWithFiltersForManagement(String searchTerm, String category,
                                                                Double minPrice, Double maxPrice,
                                                                int offset, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, " +
                     "m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, " +
                     "m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
                     "FROM Media m WHERE 1=1");
        List<Object> params = new ArrayList<>();
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sql.append(" AND LOWER(m.title) LIKE ?");
            params.add("%" + searchTerm.toLowerCase() + "%");
        }
        
        if (category != null && !category.isEmpty() && !category.equalsIgnoreCase("all")) {
            sql.append(" AND LOWER(m.category) = ?");
            params.add(category.toLowerCase());
        }
        
        if (minPrice != null) {
            sql.append(" AND m.price >= ?");
            params.add(minPrice);
        }
        
        if (maxPrice != null) {
            sql.append(" AND m.price <= ?");
            params.add(maxPrice);
        }
        
        sql.append(" ORDER BY m.created_at DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                if (param instanceof String) {
                    ps.setString(i + 1, (String) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                } else if (param instanceof Integer) {
                    ps.setInt(i + 1, (Integer) param);
                }
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = mapMediaToProduct(conn, rs);
                    if (p != null) list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public int countAllProductsForManagement() {
        String sql = "SELECT COUNT(*) FROM Media";
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    @Override
    public long addProduct(Product product) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Get manager user_id
                int managerUserId = 1;
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT user_id FROM Users WHERE username = 'manager' LIMIT 1")) {
                    if (rs.next()) {
                        managerUserId = rs.getInt(1);
                    }
                }
                
                // Parse dimension
                Double width = null, height = null, length = null;
                if (product.getDimension() != null && !product.getDimension().isEmpty()) {
                    String[] parts = product.getDimension().replaceAll("[^0-9xX.]", "").split("[xX]");
                    if (parts.length >= 2) {
                        try {
                            width = Double.parseDouble(parts[0]);
                            height = Double.parseDouble(parts[1]);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                
                // Insert into Media
                String sql = "INSERT INTO Media (category, barcode, title, description, price, value, quantity, weight, width, height, length, condition, image_url, created_by, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                long mediaId;
                try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, product.getType() != null ? product.getType().toLowerCase() : null);
                    ps.setString(2, product.getBarcode() != null ? product.getBarcode() : "PROD" + System.currentTimeMillis());
                    ps.setString(3, product.getTitle());
                    ps.setString(4, product.getDescription());
                    ps.setDouble(5, product.getCurrentPrice());
                    ps.setDouble(6, product.getOriginalValue());
                    ps.setInt(7, product.getQuantity());
                    ps.setDouble(8, product.getWeight());
                    if (width != null) ps.setDouble(9, width);
                    else ps.setNull(9, Types.REAL);
                    if (height != null) ps.setDouble(10, height);
                    else ps.setNull(10, Types.REAL);
                    if (length != null) ps.setDouble(11, length);
                    else ps.setNull(11, Types.REAL);
                    ps.setString(12, "new");
                    String imagePath = product.getImagePath() != null ? product.getImagePath() : 
                        com.hust.soict.aims.utils.ImageUtils.getProductImagePathAlways(0);
                    ps.setString(13, imagePath);
                    ps.setInt(14, managerUserId);
                    ps.setInt(15, managerUserId);
                    ps.executeUpdate();
                    
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            mediaId = rs.getLong(1);
                            // Update imagePath with actual ID
                            if (product.getImagePath() == null || product.getImagePath().isEmpty()) {
                                String newImagePath = com.hust.soict.aims.utils.ImageUtils.getProductImagePathAlways(mediaId);
                                updateMediaImagePath(conn, mediaId, newImagePath);
                            }
                            
                            // Insert into type-specific table
                            insertProductTypeDetails(conn, mediaId, product);
                            
                            conn.commit();
                            return mediaId;
                        }
                    }
                }
                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    @Override
    public boolean updateProduct(Product product) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Get manager user_id
                int managerUserId = 1;
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT user_id FROM Users WHERE username = 'manager' LIMIT 1")) {
                    if (rs.next()) {
                        managerUserId = rs.getInt(1);
                    }
                }
                
                // Parse dimension
                Double width = null, height = null, length = null;
                if (product.getDimension() != null && !product.getDimension().isEmpty()) {
                    String[] parts = product.getDimension().replaceAll("[^0-9xX.]", "").split("[xX]");
                    if (parts.length >= 2) {
                        try {
                            width = Double.parseDouble(parts[0]);
                            height = Double.parseDouble(parts[1]);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                
                // Update Media
                String sql = "UPDATE Media SET category=?, barcode=?, title=?, description=?, price=?, value=?, quantity=?, weight=?, width=?, height=?, length=?, condition=?, status=?, image_url=?, updated_by=?, updated_at=CURRENT_TIMESTAMP WHERE media_id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, product.getType() != null ? product.getType().toLowerCase() : null);
                    ps.setString(2, product.getBarcode() != null ? product.getBarcode() : String.valueOf(product.getId()));
                    ps.setString(3, product.getTitle());
                    ps.setString(4, product.getDescription());
                    ps.setDouble(5, product.getCurrentPrice());
                    ps.setDouble(6, product.getOriginalValue());
                    ps.setInt(7, product.getQuantity());
                    ps.setDouble(8, product.getWeight());
                    if (width != null) ps.setDouble(9, width);
                    else ps.setNull(9, Types.REAL);
                    if (height != null) ps.setDouble(10, height);
                    else ps.setNull(10, Types.REAL);
                    if (length != null) ps.setDouble(11, length);
                    else ps.setNull(11, Types.REAL);
                    ps.setString(12, product.getCondition() != null ? product.getCondition() : "new");
                    ps.setString(13, product.getStatus() != null ? product.getStatus() : "active");
                    String imagePath = product.getImagePath() != null ? product.getImagePath() : 
                        com.hust.soict.aims.utils.ImageUtils.getProductImagePathAlways(product.getId());
                    ps.setString(14, imagePath);
                    ps.setInt(15, managerUserId);
                    ps.setLong(16, product.getId());
                    
                    int affected = ps.executeUpdate();
                    if (affected > 0) {
                        // Update type-specific table
                        updateProductTypeDetails(conn, product.getId(), product);
                        conn.commit();
                        return true;
                    }
                }
                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean deleteProduct(long productId) {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Check stock first
                int stock = getStock(productId);
                
                if (stock > 0) {
                    // If stock > 0, only deactivate the product
                    if (updateProductStatus(productId, "deactivated")) {
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
                
                // If stock = 0, proceed with deletion
                // Delete from type-specific tables first
                String[] typeTables = {"Book", "Newspaper", "CD", "DVD"};
                for (String table : typeTables) {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE media_id = ?")) {
                        ps.setLong(1, productId);
                        ps.executeUpdate();
                    }
                }
                
                // Delete from Media
                String sql = "DELETE FROM Media WHERE media_id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setLong(1, productId);
                    int affected = ps.executeUpdate();
                    if (affected > 0) {
                        conn.commit();
                        return true;
                    }
                }
                conn.rollback();
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean updateProductStatus(long productId, String status) {
        try (Connection conn = getConnection()) {
            String sql = "UPDATE Media SET status=?, updated_at=CURRENT_TIMESTAMP WHERE media_id=?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, status);
                ps.setLong(2, productId);
                int affected = ps.executeUpdate();
                return affected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Map Media ResultSet to Product object
     */
    private Product mapMediaToProduct(Connection conn, ResultSet rs) throws SQLException {
        return com.hust.soict.aims.utils.ProductMapper.mapMediaToProduct(conn, rs);
    }
    
    // Helper methods for product type details
    private void insertProductTypeDetails(Connection conn, long mediaId, Product product) throws SQLException {
        if (product instanceof com.hust.soict.aims.entities.Book) {
            com.hust.soict.aims.entities.Book b = (com.hust.soict.aims.entities.Book) product;
            insertBook(conn, mediaId, b);
        } else if (product instanceof com.hust.soict.aims.entities.Newspaper) {
            com.hust.soict.aims.entities.Newspaper n = (com.hust.soict.aims.entities.Newspaper) product;
            insertNewspaper(conn, mediaId, n);
        } else if (product instanceof com.hust.soict.aims.entities.CD) {
            com.hust.soict.aims.entities.CD c = (com.hust.soict.aims.entities.CD) product;
            insertCD(conn, mediaId, c);
        } else if (product instanceof com.hust.soict.aims.entities.DVD) {
            com.hust.soict.aims.entities.DVD d = (com.hust.soict.aims.entities.DVD) product;
            insertDVD(conn, mediaId, d);
        }
    }
    
    private void updateProductTypeDetails(Connection conn, long mediaId, Product product) throws SQLException {
        // Delete old type-specific records
        String[] typeTables = {"Book", "Newspaper", "CD", "DVD"};
        for (String table : typeTables) {
            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE media_id = ?")) {
                ps.setLong(1, mediaId);
                ps.executeUpdate();
            }
        }
        
        // Insert new type-specific details
        insertProductTypeDetails(conn, mediaId, product);
    }
    
    private void updateMediaImagePath(Connection conn, long mediaId, String imagePath) throws SQLException {
        String sql = "UPDATE Media SET image_url=? WHERE media_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, imagePath);
            ps.setLong(2, mediaId);
            ps.executeUpdate();
        }
    }
    
    private void insertBook(Connection conn, long mediaId, com.hust.soict.aims.entities.Book book) throws SQLException {
        String sql = "INSERT INTO Book (media_id, author, cover_type, publisher, publish_date, number_of_page, language, book_category, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getCoverType());
            ps.setString(4, book.getPublisher());
            ps.setString(5, book.getPublicationDate());
            if (book.getNumberOfPages() != null) {
                ps.setInt(6, book.getNumberOfPages());
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setString(7, book.getLanguage());
            ps.setString(8, book.getBookCategory());
            ps.setString(9, book.getGenre());
            ps.executeUpdate();
        }
    }
    
    private void insertNewspaper(Connection conn, long mediaId, com.hust.soict.aims.entities.Newspaper newspaper) throws SQLException {
        String sql = "INSERT INTO Newspaper (media_id, editor_in_chief, publisher, publish_date, issue_number, publication_frequency, issn, language, sections) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, newspaper.getEditorInChief());
            ps.setString(3, newspaper.getPublisher());
            ps.setString(4, newspaper.getPublicationDate());
            ps.setString(5, newspaper.getIssueNumber());
            ps.setString(6, newspaper.getPublicationFrequency());
            ps.setString(7, newspaper.getIssn());
            ps.setString(8, newspaper.getLanguage());
            ps.setString(9, newspaper.getSections());
            ps.executeUpdate();
        }
    }
    
    private void insertCD(Connection conn, long mediaId, com.hust.soict.aims.entities.CD cd) throws SQLException {
        String sql = "INSERT INTO CD (media_id, artist, record_label, music_type, release_date, genre) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, cd.getArtist());
            ps.setString(3, cd.getRecordLabel());
            ps.setString(4, cd.getAlbum());
            ps.setString(5, cd.getReleaseDate());
            ps.setString(6, cd.getGenre());
            ps.executeUpdate();
        }
    }
    
    private void insertDVD(Connection conn, long mediaId, com.hust.soict.aims.entities.DVD dvd) throws SQLException {
        String sql = "INSERT INTO DVD (media_id, disc_type, director, runtime, studio, language, subtitle, release_date, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, dvd.getDiscType());
            ps.setString(3, dvd.getDirector());
            if (dvd.getRuntime() != null) {
                String runtime = String.valueOf(dvd.getRuntime());
                runtime = runtime.replaceAll("[^0-9]", "");
                if (!runtime.isEmpty()) {
                    try {
                        ps.setInt(4, Integer.parseInt(runtime));
                    } catch (NumberFormatException e) {
                        ps.setNull(4, Types.INTEGER);
                    }
                } else {
                    ps.setNull(4, Types.INTEGER);
                }
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.setString(5, dvd.getStudio());
            ps.setString(6, dvd.getLanguage());
            ps.setString(7, dvd.getSubtitles());
            ps.setString(8, dvd.getReleaseDate());
            ps.setString(9, dvd.getGenre());
            ps.executeUpdate();
        }
    }
}

