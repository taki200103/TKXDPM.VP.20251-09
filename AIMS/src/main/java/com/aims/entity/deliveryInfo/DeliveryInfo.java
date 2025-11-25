package com.aims.entity.deliveryInfo;

import java.util.regex.Pattern;

import com.aims.exception.InvalidDeliveryInfoException;

public class DeliveryInfo {
    private int id;
    private String deliveryAddress;
    private String city;
    private String recipientName;
    private String email;
    private String phoneNumber;
    private String instructions;

    public DeliveryInfo(String deliveryAddress, String city, String recipientName, String email, String phoneNumber, String instructions) {
        this.deliveryAddress = deliveryAddress;
        this.city = city;
        this.recipientName = recipientName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.instructions = instructions;
    }

    public DeliveryInfo() {

    }

    public int getId() {
        return id;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        if (!(validateAddress(deliveryAddress))) {
            // Ném ngoại lệ khi thông tin không hợp lệ
            throw new InvalidDeliveryInfoException("Invalid delivery information: Address is incorrect.");
        } else
            this.deliveryAddress = deliveryAddress;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        if (!(validateName(recipientName))) {
            // Ném ngoại lệ khi thông tin không hợp lệ
            throw new InvalidDeliveryInfoException("Invalid delivery information: Name is incorrect.");
        } else
            this.recipientName = recipientName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (!(validateEmail(email))) {
            // Ném ngoại lệ khi thông tin không hợp lệ
            throw new InvalidDeliveryInfoException("Invalid delivery information: Email is incorrect.");
        } else
            this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (!(validatePhoneNumber(phoneNumber))) {
            // Ném ngoại lệ khi thông tin không hợp lệ
            throw new InvalidDeliveryInfoException("Invalid delivery information: Phone number is incorrect.");
        } else
            this.phoneNumber = phoneNumber;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
    private boolean validatePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return false;
        }

        if (phoneNumber.isEmpty() || phoneNumber.charAt(0) != '0') {
            return false;
        }

        char separator = '\0';
        StringBuilder cleaned = new StringBuilder();
        boolean lastWasSeparator = false;

        for (char c : phoneNumber.toCharArray()) {
            if (Character.isDigit(c)) {
                cleaned.append(c);
                lastWasSeparator = false;
            } else if (c == '.' || c == '-' || c == '/') {
                if (separator == '\0') {
                    separator = c;
                } else if (c != separator) {
                    return false;
                }

                if (lastWasSeparator) {
                    return false;
                }
                lastWasSeparator = true;
            } else {
                return false;
            }
        }

        if (lastWasSeparator) {
            return false; // Ends with a separator
        }

        return cleaned.length() == 10;
    }

    private boolean validateName(String name) {
        if (name == null) {
            return false;
        }

        if (name.length() > 30) {
            return false;
        }

        return name.matches("[a-zA-Z]+");
    }

    private boolean validateAddress(String address) {
        if (address == null) {
            return false;
        }

        if (address.isEmpty()) {
            return false;
        }

        if (address.length() > 100) {
            return false;
        }

        for (char c : address.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && c != '/') {
                return false;
            }
        }

        return true;
    }

    private boolean validateEmail(String email){
        String emailRegex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);

        if (email == null || email.isEmpty()) {
            return false;
        }
        return pattern.matcher(email).matches();
    }

    public void setId(int deliveryInfoId) {
        this.id = deliveryInfoId;
    }
}
