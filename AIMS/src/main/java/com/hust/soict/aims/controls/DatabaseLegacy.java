package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.*;
import com.hust.soict.aims.utils.ImageUtils;
import com.hust.soict.aims.utils.ProductMapper;

import java.sql.*;
import java.util.*;

/**
 * Các phương thức cơ sở dữ liệu dạng cũ (Legacy)
 *
 * Lớp này chứa các phương thức tĩnh được giữ lại từ lớp Database gốc.
 * Các phương thức này được giữ lại để đảm bảo khả năng tương thích với mã nguồn cũ.
 *
 * Mã mới nên sử dụng các DAO interface thay thế.
 *
 * @deprecated Hãy dùng các DAO interface thay cho lớp này
 */
@Deprecated
public class DatabaseLegacy {
    private static final String URL = Database.URL;
    
    /**
     * Functional interface dùng để gán tham số cho PreparedStatement
     */
    @FunctionalInterface
    private interface PreparedStatementSetter {
        void set(PreparedStatement ps) throws SQLException;
    }
    
    // =======================
    // Các phương thức tìm kiếm sản phẩm (Legacy)
    // =======================
    
    public static List<Product> searchProducts(String searchTerm, int offset, int limit) {
        // Thử truy vấn bảng Media trước
        List<Product> results = searchMediaProducts(searchTerm, null, null, null, offset, limit);
        // Không còn dùng bảng legacy products, chỉ trả về kết quả từ Media
        return results;
    }
    
    public static int countSearchResults(String searchTerm) {
        // Thử đếm trong bảng Media trước
        String q = "SELECT COUNT(*) FROM Media WHERE status = 'active' AND LOWER(title) LIKE ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setString(1, "%" + searchTerm.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    if (count > 0) return count;
                }
            }
        } catch (SQLException e) {}
        
        // Không còn dùng bảng legacy products, nếu Media không có thì trả về 0
        return 0;
    }
    
    public static List<Product> searchProductsWithFilters(String searchTerm, String category, 
                                                         Double minPrice, Double maxPrice, 
                                                         int offset, int limit) {
        // Thử truy vấn bảng Media trước
        List<Product> results = searchMediaProducts(searchTerm, category, minPrice, maxPrice, offset, limit);
        // Không còn dùng bảng legacy products, chỉ trả về kết quả từ Media
        return results;
    }
    
    public static int countFilteredResults(String searchTerm, String category, 
                                          Double minPrice, Double maxPrice) {
        // Thử đếm trong bảng Media trước
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
        
        try (Connection conn = DriverManager.getConnection(URL); 
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
                    int count = rs.getInt(1);
                    if (count > 0 || hasMediaData()) return count;
                }
            }
        } catch (SQLException e) {}
        
        // Không còn dùng bảng legacy products, nếu Media không có thì trả về 0
        return 0;
    }
    
    public static List<Product> getAllProductsForManagement(int offset, int limit) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
                     "FROM Media m " +
                     "ORDER BY m.created_at DESC LIMIT ? OFFSET ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            ps.setInt(2, offset);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Product p = ProductMapper.mapMediaToProduct(conn, rs);
                    if (p != null) list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static List<Product> searchProductsWithFiltersForManagement(String searchTerm, String category,
                                                                       Double minPrice, Double maxPrice,
                                                                       int offset, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
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
        
        try (Connection conn = DriverManager.getConnection(URL); 
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
                    Product p = ProductMapper.mapMediaToProduct(conn, rs);
                    if (p != null) list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public static int countAllProductsForManagement() {
        String q = "SELECT COUNT(*) FROM Media";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(q)) {
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
    
    // =======================
    // Các phương thức CRUD sản phẩm (Legacy)
    // =======================
    
    public static long addProduct(Product product) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                // Lấy user_id của tài khoản manager
                int managerUserId = 1;
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT user_id FROM Users WHERE username = 'manager' LIMIT 1")) {
                    if (rs.next()) {
                        managerUserId = rs.getInt(1);
                    }
                }
                
                // Phân tích chuỗi kích thước (dimension)
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
                
                // Thêm bản ghi vào bảng Media
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
                    String imagePath = product.getImagePath() != null ? product.getImagePath() : ImageUtils.getProductImagePathAlways(0);
                    ps.setString(13, imagePath);
                    ps.setInt(14, managerUserId);
                    ps.setInt(15, managerUserId);
                    ps.executeUpdate();
                    
                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (rs.next()) {
                            mediaId = rs.getLong(1);
                            // Cập nhật lại imagePath theo ID thật sự
                            if (product.getImagePath() == null || product.getImagePath().isEmpty()) {
                                String newImagePath = ImageUtils.getProductImagePathAlways(mediaId);
                                updateMediaImagePath(conn, mediaId, newImagePath);
                            }
                            
                            // Thêm bản ghi chi tiết vào bảng tương ứng với từng loại sản phẩm
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
    
    public static boolean updateProduct(Product product) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                // Lấy user_id của tài khoản manager
                int managerUserId = 1;
                try (Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT user_id FROM Users WHERE username = 'manager' LIMIT 1")) {
                    if (rs.next()) {
                        managerUserId = rs.getInt(1);
                    }
                }
                
                // Phân tích chuỗi kích thước (dimension)
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
                
                // Cập nhật bản ghi trong bảng Media
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
                    String imagePath = product.getImagePath() != null ? product.getImagePath() : ImageUtils.getProductImagePathAlways(product.getId());
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
    
    public static boolean deleteProduct(long productId) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                // Kiểm tra tồn kho trước
                int stock = getStock(productId);
                
                if (stock > 0) {
                    // Nếu còn tồn kho (> 0) thì chỉ hủy kích hoạt sản phẩm (deactivate)
                    if (updateProductStatus(productId, "deactivated")) {
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
                
                // Nếu tồn kho = 0 thì tiến hành xóa hoàn toàn
                // Xóa trước ở các bảng chi tiết theo loại sản phẩm
                String[] typeTables = {"Book", "Newspaper", "CD", "DVD"};
                for (String table : typeTables) {
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM " + table + " WHERE media_id = ?")) {
                        ps.setLong(1, productId);
                        ps.executeUpdate();
                    }
                }
                
                // Sau đó xóa trong bảng Media
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
    
    public static boolean updateProductStatus(long productId, String status) {
        try (Connection conn = DriverManager.getConnection(URL)) {
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
    
    public static int getStock(long productId) {
        // Thử lấy tồn kho từ bảng Media trước
        String q = "SELECT quantity FROM Media WHERE media_id = ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(q)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt("quantity");
            }
        } catch (SQLException e) {}
        
        // Không còn dùng bảng legacy products, nếu Media không có thì coi như hết hàng
        return 0;
    }
    
    public static boolean reduceStock(long productId, int amount) {
        // Thử trừ tồn kho trên bảng Media trước
        int current = getStock(productId);
        if (current < amount) return false;
        
        String u = "UPDATE Media SET quantity = quantity - ? WHERE media_id = ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(u)) {
            ps.setInt(1, amount);
            ps.setLong(2, productId);
            int affected = ps.executeUpdate();
            if (affected > 0) return true;
        } catch (SQLException e) {}
        
        // Không còn dùng bảng legacy products, nếu không trừ được trên Media thì trả về false
        return false;
    }
    
    public static int countProducts() {
        // Thử đếm số sản phẩm trong bảng Media trước, nếu không có thì dự phòng sang bảng products
        try (Connection conn = DriverManager.getConnection(URL); 
             Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Media")) {
            if (rs.next()) {
                int count = rs.getInt(1);
                if (count > 0) return count;
            }
        } catch (SQLException e) {}
        
        // Không còn dùng bảng legacy products, nếu Media không có thì trả về 0
        return 0;
    }
    
    public static Product getProductById(long productId) {
        // Thử lấy sản phẩm từ bảng Media trước (không lọc theo trạng thái để có thể sửa cả sản phẩm đã bị deactivate)
        String sql = "SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
                     "FROM Media m WHERE m.media_id = ?";
        try (Connection conn = DriverManager.getConnection(URL); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return ProductMapper.mapMediaToProduct(conn, rs);
                }
            }
        } catch (SQLException e) {}
        
        // Không còn dùng bảng legacy products, nếu không tìm thấy thì trả về null
        return null;
    }
    
    // =======================
    // Track Operations
    // =======================
    
    public static List<Track> loadTracks(long mediaId) {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT track_id, title, length, track_number " +
                     "FROM Track WHERE media_id = ? ORDER BY track_number ASC";
        
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Track track = new Track();
                    track.setTrackId(rs.getLong("track_id"));
                    track.setMediaId(mediaId);
                    track.setTitle(rs.getString("title"));
                    track.setLength(rs.getObject("length") != null ? rs.getInt("length") : null);
                    track.setTrackNumber(rs.getObject("track_number") != null ? 
                                        rs.getInt("track_number") : null);
                    tracks.add(track);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return tracks;
    }
    
    public static boolean saveTracks(long mediaId, List<Track> tracks) {
        try (Connection conn = DriverManager.getConnection(URL)) {
            conn.setAutoCommit(false);
            try {
                // Xóa toàn bộ track cũ của CD này
                String deleteSql = "DELETE FROM Track WHERE media_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                    ps.setLong(1, mediaId);
                    ps.executeUpdate();
                }
                
                // Thêm lại danh sách track mới
                if (tracks != null && !tracks.isEmpty()) {
                    String insertSql = "INSERT INTO Track (media_id, title, length, track_number) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                        for (Track track : tracks) {
                            ps.setLong(1, mediaId);
                            ps.setString(2, track.getTitle());
                            if (track.getLength() != null) {
                                ps.setInt(3, track.getLength());
                            } else {
                                ps.setNull(3, java.sql.Types.INTEGER);
                            }
                            if (track.getTrackNumber() != null) {
                                ps.setInt(4, track.getTrackNumber());
                            } else {
                                ps.setNull(4, java.sql.Types.INTEGER);
                            }
                            ps.addBatch();
                        }
                        ps.executeBatch();
                    }
                }
                
                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // =======================
    // Order Operations
    // =======================
    
    public static long insertOrder(Order order) throws SQLException {
        String sql = "INSERT INTO Orders (status, created_at) VALUES (?, CURRENT_TIMESTAMP)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, order.getStatus() != null ? order.getStatus() : "pending");
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long orderId = rs.getLong(1);
                    order.setOrderId(orderId);
                    
                    // Truy vấn lại để lấy thời gian created_at từ cơ sở dữ liệu
                    String selectSql = "SELECT created_at FROM Orders WHERE order_id = ?";
                    try (PreparedStatement selectPs = conn.prepareStatement(selectSql)) {
                        selectPs.setLong(1, orderId);
                        try (ResultSet selectRs = selectPs.executeQuery()) {
                            if (selectRs.next()) {
                                Timestamp createdAt = selectRs.getTimestamp("created_at");
                                order.setCreatedAt(createdAt);
                            }
                        }
                    }
                    
                    return orderId;
                }
            }
        }
        throw new SQLException("Failed to insert order");
    }
    
    public static void insertDeliveryInfo(DeliveryInfo deliveryInfo) throws SQLException {
        String sql = "INSERT INTO DeliveryInfo (order_id, recipient_name, phone_number, email, delivery_address, city, instructions) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, deliveryInfo.getOrderId());
            ps.setString(2, deliveryInfo.getRecipientName());
            ps.setString(3, deliveryInfo.getPhoneNumber());
            ps.setString(4, deliveryInfo.getEmail());
            ps.setString(5, deliveryInfo.getDeliveryAddress());
            ps.setString(6, deliveryInfo.getCity());
            ps.setString(7, deliveryInfo.getInstructions());
            ps.executeUpdate();
        }
    }
    
    public static void insertOrderMedia(OrderMedia orderMedia) throws SQLException {
        String sql = "INSERT INTO OrderMedia (order_id, media_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, orderMedia.getOrderId());
            ps.setLong(2, orderMedia.getMediaId());
            ps.setInt(3, orderMedia.getQuantity());
            ps.setDouble(4, orderMedia.getPrice());
            ps.executeUpdate();
        }
    }
    
    public static void insertOrderMediaBatch(List<OrderMedia> orderMediaList) throws SQLException {
        String sql = "INSERT INTO OrderMedia (order_id, media_id, quantity, price) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql)) {
            for (OrderMedia orderMedia : orderMediaList) {
                ps.setLong(1, orderMedia.getOrderId());
                ps.setLong(2, orderMedia.getMediaId());
                ps.setInt(3, orderMedia.getQuantity());
                ps.setDouble(4, orderMedia.getPrice());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
    
    public static long insertPaymentTransaction(PaymentTransaction paymentTransaction) throws SQLException {
        String sql = "INSERT INTO PaymentTransaction (amount, method_type, transaction_no, transaction_content, pay_date, bank_code, bank_transaction_no, card_type) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setDouble(1, paymentTransaction.getAmount());
            ps.setString(2, paymentTransaction.getMethodTypeAsString());
            ps.setString(3, paymentTransaction.getTransactionNo());
            ps.setString(4, paymentTransaction.getTransactionContent());
            ps.setTimestamp(5, paymentTransaction.getPayDate());
            ps.setString(6, paymentTransaction.getBankCode());
            ps.setString(7, paymentTransaction.getBankTransactionNo());
            ps.setString(8, paymentTransaction.getCardType());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long paymentTransactionId = rs.getLong(1);
                    paymentTransaction.setPaymentTransactionId(paymentTransactionId);
                    return paymentTransactionId;
                }
            }
        }
        throw new SQLException("Failed to insert payment transaction");
    }
    
    public static long insertInvoice(Invoice invoice) throws SQLException {
        String sql = "INSERT INTO Invoice (order_id, payment_transaction_id, product_total, vat_amount, shipping_fee, total_amount, created_at) VALUES (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, invoice.getOrderId());
            ps.setLong(2, invoice.getPaymentTransactionId());
            ps.setDouble(3, invoice.getProductTotal());
            ps.setDouble(4, invoice.getVatAmount());
            ps.setDouble(5, invoice.getShippingFee());
            ps.setDouble(6, invoice.getTotalAmount());
            ps.executeUpdate();
            
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    long invoiceId = rs.getLong(1);
                    invoice.setInvoiceId(invoiceId);
                    return invoiceId;
                }
            }
        }
        throw new SQLException("Failed to insert invoice");
    }
    
    // =======================
    // Helper Methods
    // =======================
    
    private static List<Product> searchMediaProducts(String searchTerm, String category, 
                                                     Double minPrice, Double maxPrice, 
                                                     int offset, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT m.media_id, m.category, m.barcode, m.title, m.description, m.price, m.value, m.quantity, m.weight, m.width, m.height, m.length, m.condition, m.status, m.image_url, m.created_by, m.updated_by, m.created_at, m.updated_at " +
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
        
        try (Connection conn = DriverManager.getConnection(URL); 
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
                    Product p = ProductMapper.mapMediaToProduct(conn, rs);
                    if (p != null) list.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private static boolean hasMediaData() {
        try (Connection conn = DriverManager.getConnection(URL); 
             Statement st = conn.createStatement(); 
             ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM Media")) {
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {}
        return false;
    }
    
    private static void insertProductTypeDetails(Connection conn, long mediaId, Product product) throws SQLException {
        if (product instanceof Book) {
            Book b = (Book) product;
            insertBook(conn, mediaId, Map.of(
                "author", b.getAuthor() != null ? b.getAuthor() : "",
                "coverType", b.getCoverType() != null ? b.getCoverType() : "",
                "publisher", b.getPublisher() != null ? b.getPublisher() : "",
                "publicationDate", b.getPublicationDate() != null ? b.getPublicationDate() : "",
                "numberOfPages", b.getNumberOfPages() != null ? String.valueOf(b.getNumberOfPages()) : "",
                "language", b.getLanguage() != null ? b.getLanguage() : "",
                "bookCategory", b.getBookCategory() != null ? b.getBookCategory() : "",
                "genre", b.getGenre() != null ? b.getGenre() : ""
            ));
        } else if (product instanceof Newspaper) {
            Newspaper n = (Newspaper) product;
            insertNewspaper(conn, mediaId, Map.of(
                "editorInChief", n.getEditorInChief() != null ? n.getEditorInChief() : "",
                "publisher", n.getPublisher() != null ? n.getPublisher() : "",
                "publicationDate", n.getPublicationDate() != null ? n.getPublicationDate() : "",
                "issueNumber", n.getIssueNumber() != null ? n.getIssueNumber() : "",
                "publicationFrequency", n.getPublicationFrequency() != null ? n.getPublicationFrequency() : "",
                "issn", n.getIssn() != null ? n.getIssn() : "",
                "language", n.getLanguage() != null ? n.getLanguage() : "",
                "sections", n.getSections() != null ? n.getSections() : ""
            ));
        } else if (product instanceof CD) {
            CD c = (CD) product;
            insertCD(conn, mediaId, Map.of(
                "artist", c.getArtist() != null ? c.getArtist() : "",
                "recordLabel", c.getRecordLabel() != null ? c.getRecordLabel() : "",
                "album", c.getAlbum() != null ? c.getAlbum() : "",
                "releaseDate", c.getReleaseDate() != null ? c.getReleaseDate() : "",
                "genre", c.getGenre() != null ? c.getGenre() : ""
            ));
        } else if (product instanceof DVD) {
            DVD d = (DVD) product;
            insertDVD(conn, mediaId, Map.of(
                "discType", d.getDiscType() != null ? d.getDiscType() : "",
                "director", d.getDirector() != null ? d.getDirector() : "",
                "runtime", d.getRuntime() != null ? String.valueOf(d.getRuntime()) : "",
                "studio", d.getStudio() != null ? d.getStudio() : "",
                "language", d.getLanguage() != null ? d.getLanguage() : "",
                "subtitles", d.getSubtitles() != null ? d.getSubtitles() : "",
                "releaseDate", d.getReleaseDate() != null ? d.getReleaseDate() : "",
                "genre", d.getGenre() != null ? d.getGenre() : ""
            ));
        }
    }
    
    private static void updateProductTypeDetails(Connection conn, long mediaId, Product product) throws SQLException {
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
    
    private static void updateMediaImagePath(Connection conn, long mediaId, String imagePath) throws SQLException {
        String sql = "UPDATE Media SET image_url=? WHERE media_id=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, imagePath);
            ps.setLong(2, mediaId);
            ps.executeUpdate();
        }
    }
    
    private static void insertBook(Connection conn, long mediaId, Map<String, String> extra) throws SQLException {
        String sql = "INSERT INTO Book (media_id, author, cover_type, publisher, publish_date, number_of_page, language, book_category, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, extra.getOrDefault("author", null));
            ps.setString(3, extra.getOrDefault("coverType", null));
            ps.setString(4, extra.getOrDefault("publisher", null));
            ps.setString(5, extra.getOrDefault("publicationDate", null));
            String pages = extra.get("numberOfPages");
            if (pages != null && !pages.isEmpty()) {
                try {
                    ps.setInt(6, Integer.parseInt(pages));
                } catch (NumberFormatException e) {
                    ps.setNull(6, Types.INTEGER);
                }
            } else {
                ps.setNull(6, Types.INTEGER);
            }
            ps.setString(7, extra.getOrDefault("language", null));
            ps.setString(8, extra.getOrDefault("bookCategory", null));
            ps.setString(9, extra.getOrDefault("genre", null));
            ps.executeUpdate();
        }
    }
    
    private static void insertNewspaper(Connection conn, long mediaId, Map<String, String> extra) throws SQLException {
        String sql = "INSERT INTO Newspaper (media_id, editor_in_chief, publisher, publish_date, issue_number, publication_frequency, issn, language, sections) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, extra.getOrDefault("editorInChief", null));
            ps.setString(3, extra.getOrDefault("publisher", null));
            ps.setString(4, extra.getOrDefault("publicationDate", null));
            ps.setString(5, extra.getOrDefault("issueNumber", null));
            ps.setString(6, extra.getOrDefault("publicationFrequency", null));
            ps.setString(7, extra.getOrDefault("issn", null));
            ps.setString(8, extra.getOrDefault("language", null));
            ps.setString(9, extra.getOrDefault("sections", null));
            ps.executeUpdate();
        }
    }
    
    private static void insertCD(Connection conn, long mediaId, Map<String, String> extra) throws SQLException {
        String sql = "INSERT INTO CD (media_id, artist, record_label, music_type, release_date, genre) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, extra.getOrDefault("artist", null));
            ps.setString(3, extra.getOrDefault("recordLabel", null));
            ps.setString(4, extra.getOrDefault("album", null)); // Sử dụng trường album làm music_type
            ps.setString(5, extra.getOrDefault("releaseDate", null));
            ps.setString(6, extra.getOrDefault("genre", null));
            ps.executeUpdate();
        }
    }
    
    private static void insertDVD(Connection conn, long mediaId, Map<String, String> extra) throws SQLException {
        String sql = "INSERT INTO DVD (media_id, disc_type, director, runtime, studio, language, subtitle, release_date, genre) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            ps.setString(2, extra.getOrDefault("discType", null));
            ps.setString(3, extra.getOrDefault("director", null));
            String runtime = extra.get("runtime");
            if (runtime != null && !runtime.isEmpty()) {
                // Cố gắng tách phần số từ chuỗi dạng "120min" hoặc tương tự
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
            ps.setString(5, extra.getOrDefault("studio", null));
            ps.setString(6, extra.getOrDefault("language", null));
            ps.setString(7, extra.getOrDefault("subtitles", null));
            ps.setString(8, extra.getOrDefault("releaseDate", null));
            ps.setString(9, extra.getOrDefault("genre", null));
            ps.executeUpdate();
        }
    }
}

