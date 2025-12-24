package com.hust.soict.aims.controls;

import com.hust.soict.aims.entities.*;

import java.util.List;
import java.util.UUID;

public class PlaceOrderController {
    private Order currentOrder;
    
    private List<CartItem> orderItems;
    private double subtotal;
    private double totalWeight;
    
    /**
     * Result class for operations that can fail
     */
    public static class PlaceOrderResult {
        public boolean success;
        public String message;
        public Invoice invoice;
        
        public PlaceOrderResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
    
    /**
     * Initialize order from cart
     * @param cart CartController containing items to order
     * @return true if order created successfully, false if cart is empty
     */
    private boolean setOrder(CartController cart) {
        if (cart == null || cart.isEmpty()) {
            return false;
        }
        
        // Create new order
        currentOrder = new Order();
        currentOrder.setId(UUID.randomUUID().toString());
        
        // Copy cart items to order
        orderItems = cart.getItems();
        currentOrder.setItems(orderItems);
        
        // Calculate totals
        subtotal = cart.getSubtotal();
        totalWeight = cart.getTotalWeight();
        
        return true;
    }
    
    /**
     * Check if all items in current order have sufficient stock
     * @return PlaceOrderResult with success=true if stock is sufficient
     */
    private PlaceOrderResult checkQuantityInStock() {
        if (currentOrder == null || orderItems == null) {
            return new PlaceOrderResult(false, "No order to check. Call setOrder() first.");
        }
        
        StringBuilder insufficient = new StringBuilder();
        for (CartItem item : orderItems) {
            int stock = Database.getStock(item.getProduct().getId());
            if (stock < item.getQuantity()) {
                insufficient.append(String.format("%s (available: %d, needed: %d)\n", 
                    item.getProduct().getTitle(), stock, item.getQuantity()));
            }
        }
        
        if (insufficient.length() > 0) {
            return new PlaceOrderResult(false, "Insufficient stock for:\n" + insufficient.toString());
        }
        
        return new PlaceOrderResult(true, "Stock is sufficient");
    }
    
    /**
     * Set delivery information for current order
     * Also calculates and sets shipping fee
     * @param deliveryInfo Delivery information
     * @return true if successful
     */
    private boolean setDeliveryInfo(DeliveryInfo deliveryInfo) {
        if (currentOrder == null) {
            return false;
        }
        
        currentOrder.setDeliveryInfo(deliveryInfo);
        
        // Auto-calculate shipping fee when delivery info is set
        double shippingFee = calculateShippingFee(subtotal, totalWeight, deliveryInfo.getCity());
        currentOrder.setShippingFee(shippingFee);
        
        return true;
    }
    
    /**
     * Calculate shipping fee based on order details
     * Rules:
     * - Free if subtotal > 100,000 VND
     * - Different rates for Hanoi/HCM vs other cities
     * - Weight-based pricing
     * - Max fee: 25,000 VND
     * 
     * @param subtotal Order subtotal
     * @param totalWeight Total weight in kg
     * @param city Delivery city
     * @return Shipping fee in VND
     */
    private double calculateShippingFee(double subtotal, double totalWeight, String city) {
        // Free shipping for orders over 100,000 VND
        if (subtotal > 100000.0) {
            return 0.0;
        }
        
        // Check if major city (Hanoi or Ho Chi Minh)
        boolean isHnHcm = city != null && (
            city.toLowerCase().contains("hanoi") || 
            city.toLowerCase().contains("ha noi") ||
            city.toLowerCase().contains("ho chi minh") || 
            city.toLowerCase().contains("hochiminh") || 
            city.toLowerCase().contains("hcm")
        );
        
        // Base rates differ by location
        double baseWeight = isHnHcm ? 3.0 : 0.5;  // kg
        double basePrice = isHnHcm ? 22000.0 : 30000.0;  // VND
        
        double fee;
        if (totalWeight <= baseWeight) {
            fee = basePrice;
        } else {
            // Additional fee for extra weight (per 0.5kg)
            double extraWeight = totalWeight - baseWeight;
            int extraUnits = (int) Math.ceil(extraWeight / 0.5);
            fee = basePrice + (extraUnits * 2500.0);
        }
        
        // Cap at maximum fee
        return Math.min(fee, 25000.0);
    }
    
    /**
     * Create invoice from current order
     * @return Invoice object, or null if order is not ready
     */
    private Invoice createInvoice() {
        if (currentOrder == null || currentOrder.getDeliveryInfo() == null) {
            return null;
        }
        
        double shippingFee = currentOrder.getShippingFee();
        return new Invoice(currentOrder, subtotal, shippingFee);
    }
    
    /**
     * This method:
     * 1. Re-validates stock availability
     * 2. Reduces stock from database
     * 3. Marks order as completed
     * 
     * Must be called after placeOrder() and payment confirmation
     * 
     * @return PlaceOrderResult with final invoice if successful
     */
    public PlaceOrderResult payOrder() {
        if (currentOrder == null || orderItems == null) {
            return new PlaceOrderResult(false, "No order to pay");
        }
        
        // Check stock one more time before reducing
        PlaceOrderResult stockCheck = checkQuantityInStock();
        if (!stockCheck.success) {
            return stockCheck;
        }
        
        // Reduce stock for all items
        try {
            for (CartItem item : orderItems) {
                Database.reduceStock(item.getProduct().getId(), item.getQuantity());
            }
            
            PlaceOrderResult result = new PlaceOrderResult(true, "Order completed successfully");
            result.invoice = createInvoice();
            return result;
            
        } catch (Exception e) {
            return new PlaceOrderResult(false, "Failed to process order: " + e.getMessage());
        }
    }
    
    /**
     * This method:
     * 1. Validates stock availability
     * 2. Creates order with delivery information
     * 3. Calculates shipping fee
     * 4. Generates invoice for preview
     * 
     * Note: Does NOT reduce stock - call payOrder() after payment confirmation
     * 
     * @param cart Cart to order from
     * @param deliveryInfo Delivery information
     * @return PlaceOrderResult with invoice if successful
     */
    public PlaceOrderResult placeOrder(CartController cart, DeliveryInfo deliveryInfo) {
        // Step 1: Set order from cart
        if (!setOrder(cart)) {
            return new PlaceOrderResult(false, "Cart is empty");
        }
        
        // Step 2: Check stock
        PlaceOrderResult stockCheck = checkQuantityInStock();
        if (!stockCheck.success) {
            return stockCheck;
        }
        
        // Step 3: Set delivery info (auto-calculates shipping)
        if (!setDeliveryInfo(deliveryInfo)) {
            return new PlaceOrderResult(false, "Failed to set delivery information");
        }
        
        // Step 4: Create invoice (ready for payment)
        PlaceOrderResult result = new PlaceOrderResult(true, "Order created successfully");
        result.invoice = createInvoice();
        return result;
    }
    
    /**
     * Get current order being processed
     */
    public Order getCurrentOrder() {
        return currentOrder;
    }
}
