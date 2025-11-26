package com.aims.dao;

import com.aims.entity.db.DBconnection;
import com.aims.entity.media.DVD;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.*;

public class DVDDao implements Dao<DVD>{

    @Override
    public List<DVD> getAll() throws SQLException {
        String sql = "SELECT * FROM DVD JOIN Media ON DVD.media_id = Media.media_id";
        List<DVD> dvds = new ArrayList<>();

        try (Statement stm = DBconnection.getConnection().createStatement();
             ResultSet res = stm.executeQuery(sql)) {

            while (res.next()) {
                DVD dvd = new DVD();
                dvd.setId(res.getInt("media_id"))
                        .setTitle(res.getString("title"))
                        .setQuantity(res.getInt("quantity"))
                        .setCategory(res.getString("category"))
                        .setMediaURL(res.getString("image_url"))
                        .setPrice(res.getInt("price"))
                        .setDescription(res.getString("description"))
                        .setWeight(res.getDouble("weight"))
                        .setReleaseDate(res.getString("release_date"))
                        .setSubtitle(res.getString("subtitle"))
                        .setStudio(res.getString("studio"))
                        .setRuntime(parseRuntime(res.getString("runtime")))
                        .setDirector(res.getString("director"))
                        .setDiscType(res.getString("disc_type"));
                dvds.add(dvd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all DVDs: " + e.getMessage(), e);
        }

        return dvds;
    }

    @Override
    public Optional<DVD> get(int id) throws SQLException {
        String sql = "SELECT * FROM DVD " +
                "INNER JOIN Media ON Media.media_id = DVD.media_id " +
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

                    // Lấy dữ liệu từ bảng DVD
                    String discType = res.getString("disc_type");
                    String director = res.getString("director");
                    int runtime = parseRuntime(res.getString("runtime"));
                    String studio = res.getString("studio");
                    String subtitles = res.getString("subtitle");
                    String releaseDate = res.getString("release_date");

                    // Trả về đối tượng DVD (kết hợp thông tin từ Media và DVD)
                    DVD dvd = new DVD(id, title, category, price, quantity, imageUrl, description, weight,
                            discType, director, runtime, studio, subtitles, releaseDate);
                    return Optional.of(dvd); // Trả về DVD như Optional
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving DVD with ID " + id, e);
        }

        // Nếu không tìm thấy bản ghi, trả về Optional.empty()
        return Optional.empty();
    }


    @Override
    public void save(DVD dvd) {

    }

    @Override
    public void update(DVD dvd) {

    }

    @Override
    public void delete(DVD dvd) {

    }

    /**
     * Chuyển giá trị runtime từ chuỗi sang số nguyên, chấp nhận cả chuỗi có chữ như "120 phút".
     * Nếu không parse được thì ném {@link RuntimeException} với thông báo rõ ràng.
     */
    private int parseRuntime(String runtimeRaw) {
        if (runtimeRaw == null) {
            throw new RuntimeException("Runtime value is null");
        }
        String digitsOnly = runtimeRaw.replaceAll("\\D+", "");
        if (digitsOnly.isEmpty()) {
            throw new RuntimeException("Cannot parse runtime from value: " + runtimeRaw);
        }
        try {
            return Integer.parseInt(digitsOnly);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Bad value for runtime: " + runtimeRaw, e);
        }
    }
}
