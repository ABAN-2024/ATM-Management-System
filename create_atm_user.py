import mysql.connector
from mysql.connector import errorcode

cfg = {
    'user': 'root',
    'password': 'NewStrongPassword123!',
    'host': '127.0.0.1',
    'port': 3306,
}

user = 'atm_user'
passwd = 'S3cureP@ssw0rd!'
try:
    cnx = mysql.connector.connect(**cfg)
    cur = cnx.cursor()
    cur.execute(f"CREATE USER IF NOT EXISTS '{user}'@'localhost' IDENTIFIED BY '{passwd}'")
    cur.execute(f"GRANT ALL PRIVILEGES ON atm_db.* TO '{user}'@'localhost'")
    cur.execute('FLUSH PRIVILEGES')
    cnx.commit()
    print('Created user', user)
    cur.close()
    cnx.close()
except mysql.connector.Error as err:
    print('MySQL Error:', err)
