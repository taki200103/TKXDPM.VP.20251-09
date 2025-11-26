-- Bảng DeliveryInfo
CREATE TABLE IF NOT EXISTS DeliveryInfo (
    delivery_info_id SERIAL PRIMARY KEY,
    delivery_address VARCHAR(100) NOT NULL,
    city VARCHAR(50) NOT NULL,
    recipient_name VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    phone_number VARCHAR(50) NOT NULL,
    instructions VARCHAR(200) NOT NULL
);

-- Bảng Orders 
CREATE TABLE IF NOT EXISTS Orders (
    order_id SERIAL PRIMARY KEY,
    delivery_info_id INTEGER NOT NULL,
    shipping_fee DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (delivery_info_id) REFERENCES DeliveryInfo(delivery_info_id)
);

-- Bảng PaymentTransaction 
CREATE TABLE IF NOT EXISTS PaymentTransaction (
    payment_transaction_id SERIAL PRIMARY KEY,
    amount DOUBLE PRECISION NOT NULL,
    transaction_no VARCHAR(50) NOT NULL,
    bank_code VARCHAR(50) NOT NULL,
    bank_transaction_no VARCHAR(50) NOT NULL,
    card_type VARCHAR(50) NOT NULL,
    method_type VARCHAR(50) NOT NULL,   
    pay_date TIMESTAMP NOT NULL,
    transaction_content VARCHAR(200)
);

-- Bảng Invoice
CREATE TABLE IF NOT EXISTS Invoice (
    invoice_id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    payment_transaction_id INTEGER NOT NULL,
    total_amount DOUBLE PRECISION NOT NULL,
    FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    FOREIGN KEY (payment_transaction_id) REFERENCES PaymentTransaction(payment_transaction_id)
);

-- Bảng Media
CREATE TABLE IF NOT EXISTS Media (
    media_id SERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    value DOUBLE PRECISION NOT NULL,
    quantity INTEGER NOT NULL,
    title VARCHAR(50) NOT NULL,
    image_url VARCHAR(200) NOT NULL
);

-- Bảng OrderMedia
CREATE TABLE IF NOT EXISTS OrderMedia (
    order_id INTEGER NOT NULL,
    media_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (order_id, media_id),
    FOREIGN KEY (order_id) REFERENCES Orders(order_id),
    FOREIGN KEY (media_id) REFERENCES Media(media_id)
);

-- Bảng DVD
CREATE TABLE IF NOT EXISTS DVD (
    media_id INTEGER PRIMARY KEY,
    release_date TIMESTAMP NOT NULL,
    subtitle VARCHAR(50) NOT NULL,
    studio VARCHAR(50) NOT NULL,
    runtime VARCHAR(50) NOT NULL,
    director VARCHAR(50) NOT NULL,
    disc_type VARCHAR(50) NOT NULL,
    FOREIGN KEY (media_id) REFERENCES Media(media_id)
);

-- Bảng CD
CREATE TABLE IF NOT EXISTS CD (
    media_id INTEGER PRIMARY KEY,
    release_date TIMESTAMP NOT NULL,
    record_label VARCHAR(50) NOT NULL,
    music_type VARCHAR(50) NOT NULL,
    artist VARCHAR(50) NOT NULL,
    FOREIGN KEY (media_id) REFERENCES Media(media_id)
);

-- Bảng Book
CREATE TABLE IF NOT EXISTS Book (
    media_id INTEGER PRIMARY KEY,
    publish_date TIMESTAMP NOT NULL,
    author VARCHAR(50) NOT NULL,
    cover_type VARCHAR(50) NOT NULL,
    book_category VARCHAR(50) NOT NULL,
    publisher VARCHAR(50) NOT NULL,
    language VARCHAR(50) NOT NULL,
    number_of_page INTEGER NOT NULL,
    FOREIGN KEY (media_id) REFERENCES Media(media_id)
);
