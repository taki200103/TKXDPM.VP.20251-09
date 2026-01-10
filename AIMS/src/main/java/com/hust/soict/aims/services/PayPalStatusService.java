package com.hust.soict.aims.services;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PayPalStatusService {

    public enum Status {
        PENDING, COMPLETED, FAILED, CANCELLED, UNKNOWN
    }

    private static PayPalStatusService instance;

    // orderId -> status
    private final ConcurrentMap<String, Status> statusMap = new ConcurrentHashMap<>();

    private PayPalStatusService() {}

    public static synchronized PayPalStatusService getInstance() {
        if (instance == null) instance = new PayPalStatusService();
        return instance;
    }

    public void markPending(String orderId) {
        if (orderId != null) statusMap.put(orderId, Status.PENDING);
    }

    public void markCompleted(String orderId) {
        if (orderId != null) statusMap.put(orderId, Status.COMPLETED);
    }

    public void markFailed(String orderId) {
        if (orderId != null) statusMap.put(orderId, Status.FAILED);
    }

    public void markCancelled(String orderId) {
        if (orderId != null) statusMap.put(orderId, Status.CANCELLED);
    }

    public Status getStatus(String orderId) {
        if (orderId == null) return Status.UNKNOWN;
        return statusMap.getOrDefault(orderId, Status.UNKNOWN);
    }

    public void remove(String orderId) {
        if (orderId != null) statusMap.remove(orderId);
    }
}
