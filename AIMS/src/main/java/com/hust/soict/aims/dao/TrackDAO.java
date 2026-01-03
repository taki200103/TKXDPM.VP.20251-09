package com.hust.soict.aims.dao;

import com.hust.soict.aims.entities.Track;

import java.util.List;

/**
 * DAO Interface for Track operations
 * Defines contract for Track data access operations
 */
public interface TrackDAO {
    
    /**
     * Load all tracks for a CD
     * @param mediaId CD media ID
     * @return List of tracks
     */
    List<Track> loadTracks(long mediaId);
    
    /**
     * Save tracks for a CD
     * This method deletes all existing tracks and inserts new ones
     * @param mediaId CD media ID
     * @param tracks List of tracks to save
     * @return true if successful, false otherwise
     */
    boolean saveTracks(long mediaId, List<Track> tracks);
    
    /**
     * Delete all tracks for a CD
     * @param mediaId CD media ID
     * @return true if successful, false otherwise
     */
    boolean deleteTracks(long mediaId);
}
