import sqlite3

class DB():
    def __init__(self, path='main.db') -> None:
        self.create = """CREATE TABLE IF NOT EXISTS discs
(
    id INTEGER,
    link TEXT,
    title TEXT
);"""
        self.create2 = """CREATE TABLE IF NOT EXISTS nameuuid
(
    name TEXT,
    uuid TEXT
);"""
        self.connection = sqlite3.connect(path, check_same_thread=False)
        self.cursor = self.connection.cursor()
        self.cursor.execute(self.create)
        self.cursor.execute(self.create2)
        self.connection.commit()

    def add(self, id, link, title):
        self.cursor.execute(f'INSERT INTO discs (id, link, title) VALUES ({id}, "{link}", "{title}")')
        self.connection.commit()

#    def delete(self, id)

    def find(self, title):
        self.cursor.execute(f'SELECT * FROM discs WHERE title LIKE "{title}" LIMIT 10')
        return self.cursor.fetchone()
    
    def getpage(self, num):
        self.cursor.execute(f'SELECT * FROM discs LIMIT 10 OFFSET {(num-1)*10}')
        return self.cursor.fetchall()
    
    def addname(self, name, uuid):
        self.cursor.execute(f'INSERT INTO nameuuid (name, uuid) VALUES ("{name}", "{uuid}")')
        self.connection.commit()

    def namefromuuid(self, uuid):
        self.cursor.execute(f'SELECT * FROM nameuuid WHERE uuid="{uuid}"')
        return self.cursor.fetchone()