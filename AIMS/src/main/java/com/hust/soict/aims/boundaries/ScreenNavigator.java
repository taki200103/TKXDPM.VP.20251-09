package com.hust.soict.aims.boundaries;

import java.util.ArrayList;
import java.util.List;

public class ScreenNavigator {
    private static ScreenNavigator instance;
    private final List<BaseScreenHandler> navigationHistory;
    private int currentIndex;
    private final List<NavigationListener> listeners;
    
    /**
     * Private constructor for Singleton pattern
     */
    private ScreenNavigator() {
        this.navigationHistory = new ArrayList<>();
        this.currentIndex = -1;
        this.listeners = new ArrayList<>();
    }
    
    /**
     * Get the singleton instance of ScreenNavigator
     * @return ScreenNavigator instance
     */
    public static ScreenNavigator getInstance() {
        if (instance == null) {
            synchronized (ScreenNavigator.class) {
                if (instance == null) {
                    instance = new ScreenNavigator();
                }
            }
        }
        return instance;
    }
    
    /**
     * Navigate to a new screen
     * This will hide the current screen and show the new screen
     * If there are forward screens in history, they will be cleared
     * @param screen The screen to navigate to
     */
    public void navigateTo(BaseScreenHandler screen) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen cannot be null");
        }
        
        // Hide current screen if exists
        if (currentIndex >= 0 && currentIndex < navigationHistory.size()) {
            BaseScreenHandler currentScreen = navigationHistory.get(currentIndex);
            currentScreen.hideScreen();
        }
        
        // Clear forward history if navigating from middle of history
        if (currentIndex < navigationHistory.size() - 1) {
            // Dispose all forward screens
            for (int i = navigationHistory.size() - 1; i > currentIndex; i--) {
                BaseScreenHandler screenToDispose = navigationHistory.get(i);
                screenToDispose.dispose();
                navigationHistory.remove(i);
            }
        }
        
        navigationHistory.add(screen);
        currentIndex++;
        screen.showScreen();

        // Notify listeners
        notifyNavigationChanged();
    }
    
    /**
     * Go back to the previous screen
     * Returns false if there's no previous screen
     * @return true if successfully went back, false otherwise
     */
    public boolean goBack() {
        if (!canGoBack()) {
            return false;
        }
        
        BaseScreenHandler currentScreen = navigationHistory.get(currentIndex);
        currentScreen.hideScreen();

        // Move to previous screen
        currentIndex--;
        
        // Show previous screen
        BaseScreenHandler previousScreen = navigationHistory.get(currentIndex);
        previousScreen.showScreen();
        previousScreen.refresh();
        
        // Notify listeners
        notifyNavigationChanged();
        
        return true;
    }
    
    /**
     * Go forward to the next screen
     * Returns false if there's no next screen
     * @return true if successfully went forward, false otherwise
     */
    public boolean goForward() {
        if (!canGoForward()) {
            return false;
        }
        
        // Hide current screen
        BaseScreenHandler currentScreen = navigationHistory.get(currentIndex);
        currentScreen.hideScreen();
        
        // Move to next screen
        currentIndex++;
        
        // Show next screen
        BaseScreenHandler nextScreen = navigationHistory.get(currentIndex);
        nextScreen.showScreen();
        nextScreen.refresh();
        
        // Notify listeners
        notifyNavigationChanged();
        
        return true;
    }
    
    /**
     * Check if can go back
     * @return true if there's a previous screen, false otherwise
     */
    public boolean canGoBack() {
        return currentIndex > 0;
    }
    
    /**
     * Check if can go forward
     * @return true if there's a next screen, false otherwise
     */
    public boolean canGoForward() {
        return currentIndex < navigationHistory.size() - 1;
    }
    
    /**
     * Get the current screen
     * @return Current screen or null if no screen in history
     */
    public BaseScreenHandler getCurrentScreen() {
        if (currentIndex >= 0 && currentIndex < navigationHistory.size()) {
            return navigationHistory.get(currentIndex);
        }
        return null;
    }
    
    /**
     * Get the previous screen without navigating to it
     * @return Previous screen or null if no previous screen
     */
    public BaseScreenHandler getPreviousScreen() {
        if (canGoBack()) {
            return navigationHistory.get(currentIndex - 1);
        }
        return null;
    }
    
    /**
     * Get the next screen without navigating to it
     * @return Next screen or null if no next screen
     */
    public BaseScreenHandler getNextScreen() {
        if (canGoForward()) {
            return navigationHistory.get(currentIndex + 1);
        }
        return null;
    }
    
    /**
     * Replace the current screen with a new screen
     * This will not affect the navigation history size
     * @param screen The new screen to replace the current one
     */
    public void replaceCurrentScreen(BaseScreenHandler screen) {
        if (screen == null) {
            throw new IllegalArgumentException("Screen cannot be null");
        }
        
        if (currentIndex >= 0 && currentIndex < navigationHistory.size()) {
            // Dispose old screen
            BaseScreenHandler oldScreen = navigationHistory.get(currentIndex);
            oldScreen.dispose();
            
            // Replace with new screen
            navigationHistory.set(currentIndex, screen);
            screen.showScreen();
            
            // Notify listeners
            notifyNavigationChanged();
        } else {
            navigateTo(screen);
        }
    }
    
    /**
     * Clear all navigation history and dispose all screens
     */
    public void clearHistory() {
        // Dispose all screens
        for (BaseScreenHandler screen : navigationHistory) {
            screen.dispose();
        }
        
        // Clear history
        navigationHistory.clear();
        currentIndex = -1;
        
        // Notify listeners
        notifyNavigationChanged();
    }
    
    /**
     * Clear all navigation history except the current screen
     */
    public void clearHistoryExceptCurrent() {
        if (currentIndex >= 0 && currentIndex < navigationHistory.size()) {
            BaseScreenHandler currentScreen = navigationHistory.get(currentIndex);
            
            // Dispose all other screens
            for (int i = 0; i < navigationHistory.size(); i++) {
                if (i != currentIndex) {
                    navigationHistory.get(i).dispose();
                }
            }
            
            // Clear and keep only current screen
            navigationHistory.clear();
            navigationHistory.add(currentScreen);
            currentIndex = 0;
            
            // Notify listeners
            notifyNavigationChanged();
        }
    }
    
    /**
     * Get the size of navigation history
     * @return Size of navigation history
     */
    public int getHistorySize() {
        return navigationHistory.size();
    }
    
    /**
     * Get the current index in navigation history
     * @return Current index (0-based)
     */
    public int getCurrentIndex() {
        return currentIndex;
    }
    
    /**
     * Check if navigation history is empty
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        return navigationHistory.isEmpty();
    }
    
    /**
     * Add a navigation listener
     * @param listener The listener to add
     */
    public void addNavigationListener(NavigationListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }
    
    /**
     * Remove a navigation listener
     * @param listener The listener to remove
     */
    public void removeNavigationListener(NavigationListener listener) {
        listeners.remove(listener);
    }
    
    /**
     * Notify all listeners that navigation has changed
     */
    private void notifyNavigationChanged() {
        for (NavigationListener listener : listeners) {
            listener.onNavigationChanged(getCurrentScreen(), canGoBack(), canGoForward());
        }
    }
    
    /**
     * Interface for navigation listeners
     */
    public interface NavigationListener {
        /**
         * Called when navigation state changes
         * @param currentScreen The current screen
         * @param canGoBack Whether can go back
         * @param canGoForward Whether can go forward
         */
        void onNavigationChanged(BaseScreenHandler currentScreen, boolean canGoBack, boolean canGoForward);
    }
    
    /**
     * Print navigation history for debugging
     * @return String representation of navigation history
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ScreenNavigator History:\n");
        for (int i = 0; i < navigationHistory.size(); i++) {
            BaseScreenHandler screen = navigationHistory.get(i);
            if (i == currentIndex) {
                sb.append(" -> ");
            } else {
                sb.append("    ");
            }
            sb.append(i).append(": ").append(screen.getScreenTitle()).append("\n");
        }
        return sb.toString();
    }
}
