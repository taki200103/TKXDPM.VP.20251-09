package com.hust.soict.aims.components;

import javax.swing.*;
import java.awt.*;

/**
 * JPanel with rounded corners and optional shadow
 */
public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private Color shadowColor;
    private int shadowSize;
    private boolean paintShadow;

    public RoundedPanel() {
        this(10);
    }

    public RoundedPanel(int cornerRadius) {
        this(cornerRadius, false);
    }

    public RoundedPanel(int cornerRadius, boolean paintShadow) {
        this.cornerRadius = cornerRadius;
        this.paintShadow = paintShadow;
        this.shadowColor = new Color(0, 0, 0, 30);
        this.shadowSize = paintShadow ? 5 : 0;
        setOpaque(false);
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    public void setShadow(boolean enabled) {
        this.paintShadow = enabled;
        this.shadowSize = enabled ? 5 : 0;
        repaint();
    }

    public void setShadowColor(Color color) {
        this.shadowColor = color;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        int width = getWidth();
        int height = getHeight();

        // Draw shadow if enabled
        if (paintShadow && shadowSize > 0) {
            for (int i = 0; i < shadowSize; i++) {
                float alpha = shadowColor.getAlpha() / 255f * (1f - i / (float) shadowSize) * 0.5f;
                g2.setColor(new Color(shadowColor.getRed(), shadowColor.getGreen(), shadowColor.getBlue(),
                        (int) (alpha * 255)));
                g2.fillRoundRect(i, i, width - i * 2, height - i * 2, cornerRadius, cornerRadius);
            }
        }

        // Draw main panel
        g2.setColor(getBackground());
        g2.fillRoundRect(shadowSize, shadowSize, width - shadowSize * 2, height - shadowSize * 2, cornerRadius,
                cornerRadius);

        g2.dispose();
        super.paintComponent(g);
    }

    @Override
    protected void paintBorder(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (getBorder() != null && getBorder() instanceof javax.swing.border.LineBorder) {
            javax.swing.border.LineBorder border = (javax.swing.border.LineBorder) getBorder();
            g2.setColor(border.getLineColor());
            g2.setStroke(new BasicStroke(border.getThickness()));
            g2.drawRoundRect(shadowSize, shadowSize,
                    getWidth() - shadowSize * 2 - 1,
                    getHeight() - shadowSize * 2 - 1,
                    cornerRadius, cornerRadius);
        }

        g2.dispose();
    }
}
