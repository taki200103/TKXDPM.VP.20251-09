package com.aims.dao;

import com.aims.entity.db.DBconnection;
import com.aims.entity.media.CD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.*;

public class CDDao implements Dao<CD>{

    @Override
    public List<CD> getAll() throws SQLException{
        String sql = "SELECT * FROM CD JOIN Media ON CD.media_id = Media.media_id";
        List<CD> cds = new ArrayList<>();

        try (Statement stm = DBconnection.getConnection().createStatement();
             ResultSet res = stm.executeQuery(sql)) {

            while (res.next()) {
                CD cd = new CD();
                cd.setId(res.getInt("media_id"))
                        .setTitle(res.getString("title"))
                        .setQuantity(res.getInt("quantity"))
                        .setCategory(res.getString("category"))
                        .setMediaURL(res.getString("image_url"))
                        .setPrice(res.getInt("price"))
                        .setDescription(res.getString("description"))
                        .setWeight(res.getDouble("weight"))
                        .setReleasedDate(res.getString("release_date"))
                        .setRecordLabel(res.getString("record_label"))
                        .setMusicType(res.getString("music_type"))
                        .setArtist(res.getString("artist"));
                cds.add(cd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all CDs: " + e.getMessage(), e);
        }

        return cds;
    }

    @Override
    public Optional<CD> get(int id) throws SQLException{
        String sql = "SELECT * FROM CD " +
                "INNER JOIN Media ON Media.media_id = CD.media_id " +
                "WHERE Media.media_id = ?"; // Sử dụng tham số thay vì nối trực tiếp ID

        try (PreparedStatement stm = DBconnection.getConnection().prepareStatement(sql)) {
            stm.setInt(1, id); // Gán tham số ID vào câu truy vấn

            try (ResultSet res = stm.executeQuery()) {
                if (res.next()) {
                    // Lấy dữ liệu từ bảng Media
                    String title = res.getString("title");
                    String category = res.getString("category");
                    int price = res.getInt("price");
                    int quantity = res.getInt("quantity");
                    String imageUrl = res.getString("image_url");
                    String description = res.getString("description");
                    double weight = res.getDouble("weight");

                    // Lấy dữ liệu từ bảng CD
                    String artist = res.getString("artist");
                    String recordLabel = res.getString("record_label");
                    String musicType = res.getString("music_type");
                    String releasedDate = res.getString("release_date");

                    // Trả về đối tượng CD (kết hợp thông tin từ Media và CD)
                    CD cd = new CD(id, title, category, price, quantity, imageUrl, description, weight,
                            artist, recordLabel, musicType, releasedDate);
                    return Optional.of(cd); // Trả về CD như Optional
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving CD with ID " + id, e);
        }

        // Nếu không tìm thấy bản ghi, trả về Optional.empty()
        return Optional.empty();
    }


    @Override
    public void save(CD cd) {

    }

    @Override
    public void update(CD cd) {

    }

    @Override
    public void delete(CD cd) {

    }
}
