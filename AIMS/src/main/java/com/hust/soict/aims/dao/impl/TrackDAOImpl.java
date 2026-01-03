package com.hust.soict.aims.dao.impl;

import com.hust.soict.aims.dao.BaseDAO;
import com.hust.soict.aims.dao.TrackDAO;
import com.hust.soict.aims.entities.Track;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO Implementation for Track operations
 * Contains SQL queries and database access logic
 */
public class TrackDAOImpl extends BaseDAO implements TrackDAO {
    
    @Override
    public List<Track> loadTracks(long mediaId) {
        List<Track> tracks = new ArrayList<>();
        String sql = "SELECT track_id, title, length, track_number " +
                     "FROM Track WHERE media_id = ? ORDER BY track_number ASC";
        
        try (Connection conn = getConnection();
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
            System.err.println("Error loading tracks: " + e.getMessage());
            e.printStackTrace();
        }
        
        return tracks;
    }
    
    @Override
    public boolean saveTracks(long mediaId, List<Track> tracks) {
        try (Connection conn = getConnectionWithTransaction()) {
            try {
                // Delete all existing tracks for this CD
                String deleteSql = "DELETE FROM Track WHERE media_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(deleteSql)) {
                    ps.setLong(1, mediaId);
                    ps.executeUpdate();
                }
                
                // Insert new tracks
                if (tracks != null && !tracks.isEmpty()) {
                    String insertSql = "INSERT INTO Track (media_id, title, length, track_number) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(insertSql)) {
                        for (Track track : tracks) {
                            ps.setLong(1, mediaId);
                            ps.setString(2, track.getTitle());
                            if (track.getLength() != null) {
                                ps.setInt(3, track.getLength());
                            } else {
                                ps.setNull(3, Types.INTEGER);
                            }
                            if (track.getTrackNumber() != null) {
                                ps.setInt(4, track.getTrackNumber());
                            } else {
                                ps.setNull(4, Types.INTEGER);
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
            System.err.println("Error saving tracks: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    @Override
    public boolean deleteTracks(long mediaId) {
        String sql = "DELETE FROM Track WHERE media_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, mediaId);
            int affected = ps.executeUpdate();
            return affected >= 0; // 0 is also success (no tracks to delete)
        } catch (SQLException e) {
            System.err.println("Error deleting tracks: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}

