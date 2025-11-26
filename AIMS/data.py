import psycopg2
from faker import Faker
import random

DB_CONFIG = {
    'dbname': 'AIMS',
    'user': 'postgres',
    'password': '200103',
    'host': 'localhost',
    'port': '5432'
}

fake = Faker('vi_VN')

def main():
    conn = psycopg2.connect(**DB_CONFIG)
    cur = conn.cursor()
    
    # DeliveryInfo
    cities = ['Hà Nội', 'HCM', 'Đà Nẵng']
    delivery_ids = []
    for _ in range(50):
        cur.execute("""INSERT INTO DeliveryInfo (delivery_address, city, recipient_name, 
                       email, phone_number, instructions) VALUES (%s,%s,%s,%s,%s,%s) RETURNING delivery_info_id""",
                    (fake.street_address(), random.choice(cities), fake.name(), 
                     fake.email(), fake.phone_number(), fake.sentence()))
        delivery_ids.append(cur.fetchone()[0])
    
    # Media & Books
    book_ids = []
    for i in range(30):
        cur.execute("INSERT INTO Media (category, price, value, quantity, title, image_url) VALUES (%s,%s,%s,%s,%s,%s) RETURNING media_id",
                    ('Book', round(random.uniform(50000, 500000), 2), round(random.uniform(45000, 450000), 2),
                     random.randint(10, 100), f'Sách {i+1}', f'book{i}.jpg'))
        mid = cur.fetchone()[0]
        book_ids.append(mid)
        cur.execute("INSERT INTO Book VALUES (%s,%s,%s,%s,%s,%s,%s,%s)",
                    (mid, fake.date_time(), fake.name(), random.choice(['Cứng', 'Mềm']),
                     random.choice(['Văn học', 'Khoa học']), 'NXB Trẻ', 'Tiếng Việt', random.randint(100, 500)))
    
    # Media & CDs
    cd_ids = []
    for i in range(20):
        cur.execute("INSERT INTO Media (category, price, value, quantity, title, image_url) VALUES (%s,%s,%s,%s,%s,%s) RETURNING media_id",
                    ('CD', round(random.uniform(100000, 300000), 2), round(random.uniform(90000, 270000), 2),
                     random.randint(10, 50), f'CD {i+1}', f'cd{i}.jpg'))
        mid = cur.fetchone()[0]
        cd_ids.append(mid)
        cur.execute("INSERT INTO CD VALUES (%s,%s,%s,%s,%s)",
                    (mid, fake.date_time(), 'Sony Music', random.choice(['Pop', 'Rock']), fake.name()))
    
    # Media & DVDs
    dvd_ids = []
    for i in range(20):
        cur.execute("INSERT INTO Media (category, price, value, quantity, title, image_url) VALUES (%s,%s,%s,%s,%s,%s) RETURNING media_id",
                    ('DVD', round(random.uniform(150000, 400000), 2), round(random.uniform(135000, 360000), 2),
                     random.randint(10, 40), f'DVD {i+1}', f'dvd{i}.jpg'))
        mid = cur.fetchone()[0]
        dvd_ids.append(mid)
        cur.execute("INSERT INTO DVD VALUES (%s,%s,%s,%s,%s,%s,%s)",
                    (mid, fake.date_time(), 'Vietsub', 'Galaxy', '120 phút', fake.name(), 'Blu-ray'))
    
    # PaymentTransaction
    payment_ids = []
    for i in range(100):
        cur.execute("""INSERT INTO PaymentTransaction (amount, transaction_no, bank_code, bank_transaction_no,
                       card_type, method_type, pay_date, transaction_content) VALUES (%s,%s,%s,%s,%s,%s,%s,%s) RETURNING payment_transaction_id""",
                    (round(random.uniform(100000, 5000000), 2), f'TXN{i:010d}', random.choice(['VCB', 'TCB', 'MB']),
                     f'BNK{i:012d}', random.choice(['Visa', 'MasterCard']), 'Credit Card', fake.date_time(), fake.sentence()))
        payment_ids.append(cur.fetchone()[0])
    
    # Orders
    order_ids = []
    for _ in range(80):
        cur.execute("INSERT INTO Orders (delivery_info_id, shipping_fee) VALUES (%s,%s) RETURNING order_id",
                    (random.choice(delivery_ids), round(random.uniform(15000, 50000), 2)))
        order_ids.append(cur.fetchone()[0])
    
    # OrderMedia
    all_media = book_ids + cd_ids + dvd_ids
    for oid in order_ids:
        for mid in random.sample(all_media, random.randint(1, 3)):
            cur.execute("INSERT INTO OrderMedia VALUES (%s,%s,%s)", (oid, mid, random.randint(1, 3)))
    
    # Invoice
    for i, oid in enumerate(order_ids):
        if i < len(payment_ids):
            cur.execute("INSERT INTO Invoice (order_id, payment_transaction_id, total_amount) VALUES (%s,%s,%s)",
                        (oid, payment_ids[i], round(random.uniform(200000, 8000000), 2)))
    
    conn.commit()
    print("✓ Đã sinh dữ liệu thành công!")
    
    for table in ['DeliveryInfo', 'Media', 'Book', 'CD', 'DVD', 'PaymentTransaction', 'Orders', 'OrderMedia', 'Invoice']:
        cur.execute(f"SELECT COUNT(*) FROM {table}")
        print(f"{table}: {cur.fetchone()[0]}")
    
    cur.close()
    conn.close()

if __name__ == "__main__":
    main()