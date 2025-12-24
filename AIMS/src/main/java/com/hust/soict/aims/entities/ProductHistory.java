package com.hust.soict.aims.entities;

import java.sql.Timestamp;

public class ProductHistory {
    private long historyId;
    private Long mediaId;
    private Integer userId;
    private String actionType; // add | edit | delete | stock_adjust
    private String reason;
    private Timestamp actionDate;

    public ProductHistory() {}

    public ProductHistory(long historyId, Long mediaId, Integer userId, String actionType) {
        this.historyId = historyId;
        this.mediaId = mediaId;
        this.userId = userId;
        this.actionType = actionType;
    }

    public long getHistoryId() { return historyId; }
    public void setHistoryId(long historyId) { this.historyId = historyId; }
    public Long getMediaId() { return mediaId; }
    public void setMediaId(Long mediaId) { this.mediaId = mediaId; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public String getActionType() { return actionType; }
    public void setActionType(String actionType) { this.actionType = actionType; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public Timestamp getActionDate() { return actionDate; }
    public void setActionDate(Timestamp actionDate) { this.actionDate = actionDate; }
}
