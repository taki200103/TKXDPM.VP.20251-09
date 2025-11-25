-- Bảng DeliveryInfo
CREATE TABLE DeliveryInfo (
    id SERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address TEXT NOT NULL,
    province VARCHAR(100),
    instructions TEXT
);

-- Bảng CD
CREATE TABLE CD (
    id SERIAL PRIMARY KEY,
    recordLabel VARCHAR(255),
    artist VARCHAR(255),
    releaseDate DATE,
    musicType VARCHAR(100)
);

-- Bảng Book
CREATE TABLE Book (
    id SERIAL PRIMARY KEY,
    author VARCHAR(255),
    coverType VARCHAR(50),
    publisher VARCHAR(255),
    publicDate DATE,
    numberOfPage INTEGER,
    language VARCHAR(50),
    bookCategory VARCHAR(100)
);

-- Bảng DVD
CREATE TABLE DVD (
    id SERIAL PRIMARY KEY,
    discType VARCHAR(50),
    director VARCHAR(255),
    runtime INTEGER,
    studio VARCHAR(255),
    subtitle VARCHAR(255),
    releaseDate DATE
);

-- Bảng Media
CREATE TABLE Media (
    id SERIAL PRIMARY KEY,
    category VARCHAR(100),
    price DECIMAL(10, 2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 0,
    title VARCHAR(255) NOT NULL,
    value DECIMAL(10, 2),
    imageUrl TEXT,
    cd_id INTEGER REFERENCES CD(id) ON DELETE CASCADE,
    book_id INTEGER REFERENCES Book(id) ON DELETE CASCADE,
    dvd_id INTEGER REFERENCES DVD(id) ON DELETE CASCADE,
    CONSTRAINT chk_media_type CHECK (
        (cd_id IS NOT NULL AND book_id IS NULL AND dvd_id IS NULL) OR
        (cd_id IS NULL AND book_id IS NOT NULL AND dvd_id IS NULL) OR
        (cd_id IS NULL AND book_id IS NULL AND dvd_id IS NOT NULL)
    )
);

-- Bảng order
CREATE TABLE "order" (
    id SERIAL PRIMARY KEY,
    shippingFees DECIMAL(10, 2) DEFAULT 0,
    deliveryInfo_id INTEGER NOT NULL REFERENCES DeliveryInfo(id) ON DELETE RESTRICT
);

-- Bảng order_media (bảng trung gian Many-to-Many)
CREATE TABLE order_media (
    order_id INTEGER REFERENCES "order"(id) ON DELETE CASCADE,
    media_id INTEGER REFERENCES Media(id) ON DELETE CASCADE,
    price DECIMAL(10, 2) NOT NULL,
    quantity INTEGER NOT NULL DEFAULT 1,
    PRIMARY KEY (order_id, media_id)
);

-- Bảng Invoice
CREATE TABLE Invoice (
    id SERIAL PRIMARY KEY,
    totalAmount DECIMAL(10, 2) NOT NULL,
    order_id INTEGER NOT NULL REFERENCES "order"(id) ON DELETE CASCADE
);

-- Bảng Card
CREATE TABLE Card (
    id SERIAL PRIMARY KEY,
    cardCode VARCHAR(50) NOT NULL UNIQUE,
    owner VARCHAR(255) NOT NULL,
    cvcCode VARCHAR(4) NOT NULL,
    dateExpired DATE NOT NULL
);

-- Bảng PaymentTransaction
CREATE TABLE PaymentTransaction (
    id SERIAL PRIMARY KEY,
    content TEXT,
    method VARCHAR(50) NOT NULL,
    createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    invoice_id INTEGER NOT NULL REFERENCES Invoice(id) ON DELETE CASCADE,
    card_id INTEGER NOT NULL REFERENCES Card(id) ON DELETE RESTRICT
);

-- Tạo indexes để tối ưu hiệu suất
CREATE INDEX idx_media_cd ON Media(cd_id);
CREATE INDEX idx_media_book ON Media(book_id);
CREATE INDEX idx_media_dvd ON Media(dvd_id);
CREATE INDEX idx_order_delivery ON "order"(deliveryInfo_id);
CREATE INDEX idx_invoice_order ON Invoice(order_id);
CREATE INDEX idx_payment_invoice ON PaymentTransaction(invoice_id);
CREATE INDEX idx_payment_card ON PaymentTransaction(card_id);
CREATE INDEX idx_order_media_order ON order_media(order_id);
CREATE INDEX idx_order_media_media ON order_media(media_id);