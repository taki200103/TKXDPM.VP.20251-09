package com.hust.soict.aims.components;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import com.hust.soict.aims.utils.ImageUtils;
import static com.hust.soict.aims.utils.UIConstant.*;

public class ProductImagePanel extends RoundedPanel {

    private static final int DEFAULT_RADIUS = 8;
    private static final int DEFAULT_PADDING = SPACING_SMALL;

    private final int imageSize;
    private final JLabel imageLabel;

    public ProductImagePanel(String imagePath, long productId, int imageSize) {
        super(DEFAULT_RADIUS, false);
        this.imageSize = imageSize;

        setBackground(BACKGROUND_LIGHT);
        setLayout(new BorderLayout());
        setFixedSize(imageSize);
        setBorder(BorderFactory.createEmptyBorder(
                DEFAULT_PADDING,
                DEFAULT_PADDING,
                DEFAULT_PADDING,
                DEFAULT_PADDING));

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);

        add(imageLabel, BorderLayout.CENTER);
        loadImage(imagePath, productId);
    }

    /* ================= IMAGE LOADING ================= */

    private void loadImage(String imagePath, long productId) {
        String finalPath = resolveImagePath(imagePath, productId);

        if (finalPath != null && new File(finalPath).exists()) {
            setScaledImage(finalPath);
        } else {
            setPlaceholder();
        }
    }

    private String resolveImagePath(String imagePath, long productId) {
        if (imagePath != null && !imagePath.isEmpty()) {
            File f = new File(imagePath);
            if (f.exists())
                return imagePath;
        }
        return ImageUtils.getProductImagePath(productId);
    }

    private void setScaledImage(String path) {
        ImageIcon icon = new ImageIcon(path);

        int maxSize = imageSize - DEFAULT_PADDING * 2;
        int w = icon.getIconWidth();
        int h = icon.getIconHeight();

        double scale = Math.min(
                (double) maxSize / w,
                (double) maxSize / h);

        Image scaled = icon.getImage().getScaledInstance(
                (int) (w * scale),
                (int) (h * scale),
                Image.SCALE_SMOOTH);

        imageLabel.setIcon(new ImageIcon(scaled));
        imageLabel.setText(null);
    }

    private void setPlaceholder() {
        imageLabel.setIcon(null);
        imageLabel.setText(
                "<html><center><div style='color:#999;font-size:11px;'>No<br>Image</div></center></html>");
        imageLabel.setFont(FONT_SMALL);
        imageLabel.setForeground(TEXT_SECONDARY);
    }

    /* ================= UTILS ================= */

    private void setFixedSize(int size) {
        Dimension d = new Dimension(size, size);
        setPreferredSize(d);
        setMinimumSize(d);
        setMaximumSize(d);
    }
}
