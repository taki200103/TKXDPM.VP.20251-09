package com.aims.entity.order;

import com.aims.entity.deliveryInfo.DeliveryInfo;
import com.aims.utils.Configs;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private int shippingFees;
    private List<OrderMedia> listOrderMedia; // Updated to OrderMedia type
    private DeliveryInfo deliveryInfo;
    private Integer id;
    private String status;


    public Order(){
        this.listOrderMedia = new ArrayList<>();
    }

    public Order(List<OrderMedia> listOrderMedia) {
        this.listOrderMedia = listOrderMedia;
    }

    public void addOrderMedia(OrderMedia om){
        this.listOrderMedia.add(om);
    }

    public List<OrderMedia> getlistOrderMedia() {
        return List.copyOf(this.listOrderMedia);
    }

    public void setShippingFees() {
        this.shippingFees = calculateShippingFee();
    }

    public int getShippingFees() {
        return shippingFees;
    }

    public DeliveryInfo getDeliveryInfo() {
        return deliveryInfo;
    }

    public void setDeliveryInfo(DeliveryInfo deliveryInfo) {
        this.deliveryInfo = deliveryInfo;
    }

    public Integer getId() {
        return id;
    }

    public int getAmount() {
        double amount = 0;
        for (OrderMedia om : listOrderMedia) { // Calculate subtotal without tax
            amount += om.getPrice() * om.getQuantity();
        }
        // Add VAT
        amount += (Configs.PERCENT_VAT / 100) * amount;
        return (int) amount; // Return total including shipping fees
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getAmountWithoutTax() {
        double amount = 0;
        for (OrderMedia om : listOrderMedia) { // No need to cast anymore
            amount += om.getPrice();
        }
        return (int) amount;
    }

    public boolean isInnerHanoiHCM() {
        String city = deliveryInfo.getCity();
        if (city == null) return false;
        return city.equalsIgnoreCase("Hà Nội") || city.equalsIgnoreCase("Hồ Chí Minh");
    }

    private int calculateShippingFee(){
        double fees = 0;
        List<OrderMedia> orderMediaList = listOrderMedia;
        int amount = getAmountWithoutTax();
        double maxWeight = 0;
        for (OrderMedia orderMedia: orderMediaList) {
            if (orderMedia.getMedia().getWeight() > maxWeight) {
                maxWeight = orderMedia.getMedia().getWeight();
            }
        }
        if (isInnerHanoiHCM()) {
            fees += 22;
            maxWeight = maxWeight - 3;
            if (maxWeight > 0) {
                fees += 2.5 * Math.ceil(maxWeight / 0.5);
            }
        } else {
            fees += 30;
            maxWeight = maxWeight - 0.5;
            if (maxWeight > 0) {
                fees += 2.5 * Math.ceil(maxWeight / 0.5);
            }
        }
        if (amount >= 100) {
            if (fees >= 25) {
                fees -= 25;
            } else {
                fees = 0;
            }
        }
        return (int) fees;
    }

    public void setId(int orderId) {
        this.id = orderId;
    }
}
