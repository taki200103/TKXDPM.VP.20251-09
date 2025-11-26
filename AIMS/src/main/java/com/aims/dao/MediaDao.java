package com.aims.dao;

import com.aims.entity.db.DBconnection;
import com.aims.entity.media.Media;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.*;

public class MediaDao implements Dao<Media>{

    @Override
    public List<Media> getAll() throws SQLException {
        List<Media> mediaList = new ArrayList<>();

        // Lấy tất cả Book
        mediaList.addAll(new BookDao().getAll());

        // Lấy tất cả CD
        mediaList.addAll(new CDDao().getAll());

        // Lấy tất cả DVD
        mediaList.addAll(new DVDDao().getAll());

        return mediaList;
    }

    @Override
    public Optional<Media> get(int id) throws SQLException {
        String query = "SELECT * FROM Media WHERE media_id = ?"; // Thay "media_id" bằng tên cột thực tế trong DB nếu cần.
        try (PreparedStatement stmt = DBconnection.getConnection().prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Media media = new Media(
                            rs.getInt("media_id"),
                            rs.getString("title"),
                            rs.getString("category"),
                            rs.getInt("price"),
                            rs.getInt("quantity"),
                            rs.getString("image_url"),
                            rs.getString("description"),
                            rs.getDouble("weight")
                    );
                    return Optional.of(media);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving media with ID: " + id, e);
        }
        return Optional.empty();
    }


    @Override
    public void save(Media media) {

    }

    @Override
    public void update(Media media) throws SQLException {
        String updateSQL = "UPDATE Media SET title = ?, category = ?, price = ?, quantity = ?, image_url = ?, description = ?, weight = ? WHERE media_id = ?";

        try (PreparedStatement stmt = DBconnection.getConnection().prepareStatement(updateSQL)) {
            stmt.setString(1, media.getTitle());
            stmt.setString(2, media.getCategory());
            stmt.setInt(3, media.getPrice());
            stmt.setInt(4, media.getQuantity());
            stmt.setString(5, media.getImageURL());
            stmt.setString(6, media.getDescription());
            stmt.setDouble(7, media.getWeight());
            stmt.setInt(8, media.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Update failed: No media found with ID " + media.getId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating media with ID: " + media.getId(), e);
        }
    }


    @Override
    public void delete(Media media) {

    }
}
