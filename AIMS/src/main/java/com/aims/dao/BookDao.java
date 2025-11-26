package com.aims.dao;

import com.aims.entity.db.DBconnection;
import com.aims.entity.media.Book;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.sql.*;

public class BookDao implements Dao<Book>{
    @Override
    public List<Book> getAll() throws SQLException{
        String sql = "SELECT * FROM Book JOIN Media ON Book.media_id = Media.media_id";
        List<Book> books = new ArrayList<>();

        try (Statement stm = DBconnection.getConnection().createStatement();
             ResultSet res = stm.executeQuery(sql)) {

            while (res.next()) {
                Book book = new Book();
                book.setId(res.getInt("media_id"))
                        .setTitle(res.getString("title"))
                        .setQuantity(res.getInt("quantity"))
                        .setCategory(res.getString("category"))
                        .setMediaURL(res.getString("image_url"))
                        .setPrice(res.getInt("price"))
                        .setDescription(res.getString("description"))
                        .setWeight(res.getDouble("weight"))
                        .setAuthor(res.getString("author"))
                        .setCoverType(res.getString("cover_type"))
                        .setBookCategory(res.getString("book_category"))
                        .setPublisher(res.getString("publisher"))
                        .setPublishDate(res.getString("publish_date"))
                        .setLanguage(res.getString("language"))
                        .setNumOfPages(res.getInt("number_of_page"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving all books: " + e.getMessage(), e);
        }

        return books;
    }

    @Override
    public Optional<Book> get(int id) throws SQLException{
        String sql = "SELECT * FROM Media " +
                "INNER JOIN Book ON Media.media_id = Book.media_id " +
                "WHERE Media.media_id = ?"; // Sử dụng tham số để bảo vệ SQL Injection

        try (PreparedStatement stm = DBconnection.getConnection().prepareStatement(sql)) {
            stm.setInt(1, id); // Gán tham số ID
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

                    // Lấy dữ liệu từ bảng Book
                    String author = res.getString("author");
                    String coverType = res.getString("cover_type");
                    String publisher = res.getString("publisher");
                    String publishDate = res.getString("publish_date");
                    int numOfPages = res.getInt("number_of_page");
                    String language = res.getString("language");
                    String bookCategory = res.getString("book_category");

                    // Tạo và trả về đối tượng Book
                    Book book = new Book(id, title, category, price, quantity, imageUrl, description, weight,
                            author, coverType, publisher, publishDate, numOfPages, language, bookCategory);
                    return Optional.of(book);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error retrieving book with ID: " + id, e);
        }

        return Optional.empty(); // Trả về Optional trống nếu không tìm thấy
    }


    @Override
    public void save(Book book) {

    }

    @Override
    public void update(Book book) {

    }

    @Override
    public void delete(Book book) {

    }
}
