package com.aims.views.popup;

public class PopupForm {

    public static void success(String message) {
        System.out.println("SUCCESS: " + message);
    }

    public static void error(String message) {
        System.err.println("ERROR: " + message);
    }
}
