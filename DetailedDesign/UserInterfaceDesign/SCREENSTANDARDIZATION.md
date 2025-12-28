# AIMS Application - Screen Standardization Guide

> **Mục đích**: Tài liệu này định nghĩa các tiêu chuẩn UI/UX chung cho toàn bộ ứng dụng AIMS nhằm đảm bảo tính nhất quán và chuyên nghiệp.

---

## 📋 Mục lục

1. [Color Palette (Bảng màu)](#1-color-palette-bảng-màu)
2. [Typography (Kiểu chữ)](#2-typography-kiểu-chữ)
3. [Spacing (Khoảng cách)](#3-spacing-khoảng-cách)
4. [Components (Các thành phần)](#4-components-các-thành-phần)
5. [Layout Standards (Tiêu chuẩn bố cục)](#5-layout-standards-tiêu-chuẩn-bố-cục)

---

## 1. Color Palette (Bảng màu)

### Primary Colors (Màu chính)
```java
// Primary Blue - Dùng cho các elements chính, headers
public static final Color PRIMARY_COLOR = new Color(52, 152, 219);  // #3498DB

// Primary Blue (Dark) - Dùng cho hover states
public static final Color PRIMARY_DARK = new Color(41, 128, 185);   // #2980B9

// Text on Primary - Màu text trên nền primary
public static final Color TEXT_ON_PRIMARY = Color.WHITE;
```

### Secondary Colors (Màu phụ)
```java
// Danger Red - Nút xóa, cancel, nguy hiểm
public static final Color DANGER_COLOR = new Color(231, 76, 60);    // #E74C3C
public static final Color DANGER_DARK = new Color(192, 57, 43);     // #C0392B

// Warning Orange - Cảnh báo
public static final Color WARNING_COLOR = new Color(243, 156, 18);  // #F39C12

// Info Blue - Thông tin, giá tiền
public static final Color INFO_COLOR = new Color(52, 152, 219);     // #3498DB
```

### Neutral Colors (Màu trung tính)
```java
// Background colors
public static final Color BACKGROUND_WHITE = Color.WHITE;
public static final Color BACKGROUND_LIGHT = new Color(245, 245, 245);  // #F5F5F5
public static final Color BACKGROUND_GRAY = new Color(236, 240, 241);   // #ECF0F1

// Text colors
public static final Color TEXT_PRIMARY = new Color(33, 33, 33);     // #212121
public static final Color TEXT_SECONDARY = new Color(117, 117, 117); // #757575
public static final Color TEXT_DISABLED = new Color(189, 189, 189); // #BDBDBD

// Border colors
public static final Color BORDER_LIGHT = new Color(220, 220, 220);  // #DCDCDC
public static final Color BORDER_MEDIUM = new Color(200, 200, 200); // #C8C8C8
public static final Color BORDER_DARK = new Color(149, 165, 166);   // #95A5A6
```

---

## 2. Typography (Kiểu chữ)

### Font Family
```java
// Font family chính cho toàn bộ ứng dụng
public static final String FONT_FAMILY = "Arial";

// Fallback: "Segoe UI", "Helvetica", "sans-serif"
```

### Font Sizes
```java
// Screen titles (tiêu đề màn hình)
public static final int FONT_TITLE = 24;           // Bold
public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);

// Section headers (tiêu đề sections)
public static final int FONT_HEADER = 18;          // Bold
public static final Font HEADER_FONT = new Font("Arial", Font.BOLD, 18);

// Large buttons (nút lớn)
public static final int FONT_BUTTON_LARGE = 16;    // Bold
public static final Font BUTTON_LARGE_FONT = new Font("Arial", Font.BOLD, 16);

// Product names, important labels
public static final int FONT_PRODUCT_NAME = 15;    // Bold
public static final Font PRODUCT_NAME_FONT = new Font("Arial", Font.BOLD, 15);

// Normal text, body, regular buttons
public static final int FONT_BODY = 14;            // Plain
public static final Font BODY_FONT = new Font("Arial", Font.PLAIN, 14);

// Small buttons
public static final int FONT_BUTTON = 13;          // Bold
public static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 13);

// Secondary text, helper text
public static final int FONT_SMALL = 13;           // Plain
public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 13);

// Very small text (captions)
public static final int FONT_CAPTION = 12;         // Plain
public static final Font CAPTION_FONT = new Font("Arial", Font.PLAIN, 12);
```

---

## 3. Spacing (Khoảng cách)

### Padding & Margins
```java
// Standard spacing units (theo bội số của 4 hoặc 5)
public static final int SPACING_XSMALL = 5;   // 5px
public static final int SPACING_SMALL = 10;   // 10px
public static final int SPACING_MEDIUM = 15;  // 15px
public static final int SPACING_LARGE = 20;   // 20px
public static final int SPACING_XLARGE = 30;  // 30px

// Border padding
public static final EmptyBorder PADDING_SMALL = new EmptyBorder(5, 5, 5, 5);
public static final EmptyBorder PADDING_MEDIUM = new EmptyBorder(10, 10, 10, 10);
public static final EmptyBorder PADDING_LARGE = new EmptyBorder(15, 15, 15, 15);
public static final EmptyBorder PADDING_XLARGE = new EmptyBorder(20, 20, 20, 20);
```

---

## 4. Components (Các thành phần)

### 4.1 Buttons (Nút bấm)

#### Primary Button (Nút chính - Place Order, Login, Submit)
```java
JButton button = new JButton("Place Order");
button.setFont(new Font("Arial", Font.BOLD, 16));
button.setBackground(new Color(46, 204, 113));  // SUCCESS_COLOR
button.setForeground(Color.WHITE);
button.setFocusPainted(false);
button.setPreferredSize(new Dimension(200, 50));
button.setCursor(new Cursor(Cursor.HAND_CURSOR));
```

#### Secondary Button (Nút phụ - Back, Cancel)
```java
JButton button = new JButton("← Back");
button.setFont(new Font("Arial", Font.BOLD, 13));
button.setBackground(new Color(236, 240, 241));  // BACKGROUND_GRAY
button.setForeground(new Color(33, 33, 33));     // TEXT_PRIMARY
button.setFocusPainted(false);
button.setCursor(new Cursor(Cursor.HAND_CURSOR));
```

#### Danger Button (Nút nguy hiểm - Delete, Remove, Clear)
```java
JButton button = new JButton("Remove");
button.setFont(new Font("Arial", Font.BOLD, 13));
button.setBackground(new Color(231, 76, 60));  // DANGER_COLOR
button.setForeground(Color.WHITE);
button.setFocusPainted(false);
button.setPreferredSize(new Dimension(110, 35));
button.setCursor(new Cursor(Cursor.HAND_CURSOR));
```

#### Small Action Button (Add to Cart, Info)
```java
JButton button = new JButton("Add");
button.setFont(new Font("Arial", Font.BOLD, 13));
button.setBackground(new Color(52, 152, 219));  // PRIMARY_COLOR
button.setForeground(Color.WHITE);
button.setFocusPainted(false);
button.setPreferredSize(new Dimension(80, 35));
button.setCursor(new Cursor(Cursor.HAND_CURSOR));
```

### 4.2 Headers (Tiêu đề)

#### Screen Header (with background color)
```java
JPanel headerPanel = new JPanel(new BorderLayout());
headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
headerPanel.setBackground(new Color(52, 152, 219));  // PRIMARY_COLOR

JLabel titleLabel = new JLabel("🛒 Your Shopping Cart");
titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
titleLabel.setForeground(Color.WHITE);
headerPanel.add(titleLabel, BorderLayout.WEST);
```

### 4.3 Cards (Thẻ hiển thị)

#### Product Card
```java
JPanel card = new JPanel(new BorderLayout(10, 10));
card.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(220, 220, 220)),  // BORDER_LIGHT
    new EmptyBorder(15, 15, 15, 15)  // PADDING_LARGE
));
card.setBackground(Color.WHITE);
```

#### Cart Item Card
```java
JPanel itemCard = new JPanel(new BorderLayout(15, 10));
itemCard.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(220, 220, 220)),
    new EmptyBorder(15, 15, 15, 15)
));
itemCard.setBackground(Color.WHITE);
itemCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
```

### 4.4 Text Labels

#### Product Name / Title
```java
JLabel nameLabel = new JLabel("Product Name");
nameLabel.setFont(new Font("Arial", Font.BOLD, 15));
nameLabel.setForeground(new Color(33, 33, 33));  // TEXT_PRIMARY
```

#### Price / Important Info
```java
JLabel priceLabel = new JLabel("$99.99");
JLabel.setFont(new Font("Arial", Font.BOLD, 16));
priceLabel.setForeground(new Color(52, 152, 219));  // INFO_COLOR
```

#### Secondary Info
```java
JLabel infoLabel = new JLabel("Additional info");
infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
infoLabel.setForeground(new Color(117, 117, 117));  // TEXT_SECONDARY
```

### 4.5 Input Fields

#### Text Fields
```java
JTextField textField = new JTextField(20);
textField.setFont(new Font("Arial", Font.PLAIN, 14));
textField.setPreferredSize(new Dimension(250, 35));
textField.setBorder(BorderFactory.createCompoundBorder(
    BorderFactory.createLineBorder(new Color(200, 200, 200)),
    new EmptyBorder(5, 10, 5, 10)
));
```

#### Spinners (Quantity)
```java
JSpinner spinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
spinner.setFont(new Font("Arial", Font.PLAIN, 14));
spinner.setPreferredSize(new Dimension(70, 35));
```

---

## 5. Layout Standards (Tiêu chuẩn bố cục)

### 5.1 Screen Structure

```
┌─────────────────────────────────────────────┐
│  HEADER (Primary Color background)          │ ← 50-60px height
│  • Title (left)                             │
│  • Action buttons (right)                   │
├─────────────────────────────────────────────┤
│                                             │
│  CONTENT (White/Light background)           │
│  • Main content area with scroll            │
│  • Padding: 10-15px                         │
│                                             │
├─────────────────────────────────────────────┤
│  FOOTER (Light gray background)             │ ← 70-100px height
│  • Summary info (left)                      │
│  • Action buttons (right)                   │
└─────────────────────────────────────────────┘
```

### 5.2 Default Window Sizes
```java
// Homepage / Main screens
public static final Dimension DEFAULT_SIZE = new Dimension(1000, 800);

// Dialog screens / Secondary screens
public static final Dimension DIALOG_SIZE = new Dimension(600, 500);

// Small dialogs
public static final Dimension SMALL_DIALOG_SIZE = new Dimension(400, 300);
```

### 5.3 Grid Layouts
```java
// Product grid (Homepage)
GridLayout productGrid = new GridLayout(5, 4, 10, 10);  // 5 rows x 4 cols

// Spacing between grid items: 10px horizontal, 10px vertical
```

---

## 6. Icons & Emojis

### Standard Icons
```java
// Cart icon
"🛒"

// Back arrow
"←"

// Info
"ℹ" or "i"

// Success
"✓"

// Warning
"⚠"

// Error
"✗"
```

---

## 7. Best Practices

### ✅ DO:
- Sử dụng `setFocusPainted(false)` cho tất cả các buttons
- Sử dụng `setCursor(new Cursor(Cursor.HAND_CURSOR))` cho buttons
- Sử dụng `EmptyBorder` thay vì hardcode padding
- Sử dụng color constants thay vì hardcode RGB
- Đặt font size và font family consistent
- Sử dụng `BorderLayout` cho main screen structure
- Sử dụng `BoxLayout` hoặc `GridLayout` cho lists
- Padding: 15px cho cards, 10px cho panels

### ❌ DON'T:
- Không mix font families trong cùng 1 screen
- Không sử dụng quá nhiều màu sắc khác nhau
- Không hardcode sizes - sử dụng `setPreferredSize()`
- Không quên set `setOpaque(false)` cho nested panels
- Không dùng `null` layout (absolute positioning)

---

## 8. Example Code Templates

### Complete Screen Template
```java
@Override
protected void initComponents() {
    // Initialize components with standard styles
    titleLabel = new JLabel("Screen Title");
    titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    titleLabel.setForeground(Color.WHITE);
    
    actionButton = new JButton("Action");
    actionButton.setFont(new Font("Arial", Font.BOLD, 16));
    actionButton.setBackground(new Color(46, 204, 113));
    actionButton.setForeground(Color.WHITE);
    actionButton.setFocusPainted(false);
    actionButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
}

@Override
protected void setupLayout() {
    setLayout(new BorderLayout(10, 10));
    
    // Header
    JPanel header = new JPanel(new BorderLayout());
    header.setBackground(new Color(52, 152, 219));
    header.setBorder(new EmptyBorder(15, 15, 15, 15));
    header.add(titleLabel, BorderLayout.WEST);
    add(header, BorderLayout.NORTH);
    
    // Content
    JScrollPane scrollPane = new JScrollPane(contentPanel);
    scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    add(scrollPane, BorderLayout.CENTER);
    
    // Footer
    JPanel footer = new JPanel(new BorderLayout());
    footer.setBackground(new Color(245, 245, 245));
    footer.setBorder(new EmptyBorder(15, 15, 15, 15));
    footer.add(actionButton, BorderLayout.EAST);
    add(footer, BorderLayout.SOUTH);
}
```

---

## 9. Summary Checklist

Khi tạo screen mới, check:
- [ ] Font family: `Arial` cho toàn bộ
- [ ] Header background: `#3498DB` (Primary Blue)
- [ ] Footer background: `#F5F5F5` (Light Gray)
- [ ] Primary button: `#2ECC71` (Green)
- [ ] Danger button: `#E74C3C` (Red)
- [ ] Text màu đen: `#212121`
- [ ] Text màu xám: `#757575`
- [ ] Border màu xám nhạt: `#DCDCDC`
- [ ] Padding cho cards: `15px`
- [ ] Padding cho panels: `10px`
- [ ] Button focus painted: `false`
- [ ] Button cursor: `HAND_CURSOR`

---

**Version**: 1.0  
**Last Updated**: 2025-11-09  
**Author**: AIMS Development Team

