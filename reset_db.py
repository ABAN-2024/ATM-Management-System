import mysql.connector
from mysql.connector import errorcode
cfg = {
    'user': 'root',
    'password': 'NewStrongPassword123!',
    'host': '127.0.0.1',
    'port': 3306,
}
cnx = mysql.connector.connect(**cfg)
cur = cnx.cursor()
cur.execute('DROP DATABASE IF EXISTS atm_db')
cur.execute('CREATE DATABASE atm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci')
print('Dropped and recreated atm_db')
cur.close()
cnx.close()
