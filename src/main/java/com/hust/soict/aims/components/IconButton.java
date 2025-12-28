package com.hust.soict.aims.components;

import javax.swing.*;
import java.awt.*;

import static com.hust.soict.aims.utils.UIConstant.*;

public class IconButton extends JButton {

    private static final int DEFAULT_RADIUS = 16;

    private final Color hoverColor;
    private final int radius;

    public IconButton(String iconPath, int iconSize, Color hoverColor) {
        this(iconPath, iconSize, hoverColor, DEFAULT_RADIUS);
    }

    public IconButton(String iconPath, int iconSize, Color hoverColor, int radius) {
        this.hoverColor = hoverColor;
        this.radius = radius;

        setIcon(loadIcon(iconPath, iconSize));
        setupButton();
        installHoverEffect();
    }

    private void setupButton() {
        setFocusPainted(false);
        setContentAreaFilled(false);
        setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));
        setCursor(CURSOR_HAND);
        setOpaque(false);
    }

    private void installHoverEffect() {
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                putClientProperty("hover", true);
                repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                putClientProperty("hover", false);
                repaint();
            }
        });
    }

    private ImageIcon loadIcon(String path, int size) {
        ImageIcon icon = new ImageIcon(getClass().getResource(path));
        Image scaled = icon.getImage().getScaledInstance(size, size, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (Boolean.TRUE.equals(getClientProperty("hover"))) {
            g2.setColor(hoverColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}
