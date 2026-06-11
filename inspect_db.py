import mysql.connector
from mysql.connector import errorcode
cfg = {
    'user': 'root',
    'password': 'NewStrongPassword123!',
    'host': '127.0.0.1',
    'port': 3306,
    'database': 'atm_db'
}
try:
    cnx = mysql.connector.connect(**cfg)
    cur = cnx.cursor()
    cur.execute('SHOW TABLES')
    print('tables:')
    for row in cur:
        print(' ', row[0])
    for t in ['customers','transactions','complaints']:
        try:
            cur.execute(f"SHOW CREATE TABLE {t}")
            print('\nSHOW CREATE TABLE', t)
            for r in cur:
                print(r[1])
        except mysql.connector.Error as err:
            print('ERROR', t, err)
    cur.close()
    cnx.close()
except mysql.connector.Error as err:
    if err.errno == errorcode.ER_BAD_DB_ERROR:
        print('Database does not exist')
    else:
        print('MySQL Error:', err)
