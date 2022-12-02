import sqlite3


#Connects to the db
# The .db file is created automatically if it does not exist
con = sqlite3.connect('globey_app.db')

#creates table
con.execute("""CREATE TABLE IF NOT EXISTS TRIPS (
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    NAME TEXT NOT NULL,
    LOCATION TEXT NOT NULL,
    START_DATE TEXT NOT NULL,
    END_DATE TEXT NOT NULL,
    IMG_URI TEXT
    );""")

con.execute("""CREATE TABLE IF NOT EXISTS NOTES (
    ID INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    TRIP_ID INTEGER NOT NULL,
    NOTE TEXT NOT NULL,
    UPDATED_TIME DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(TRIP_ID) REFERENCES TRIPS(ID)
);""")

# insert test data
testData = [
    {
        "name": "Cabo Trip",
        "location": "Cabo",
        "start_date": "04/07/2022",
        "end_date": "11/07/2022",
        "img_uri": None
    },
    {
        "name": "Spring Break Trip",
        "location": "Athens, Greece",
        "start_date": "08/03/2023",
        "end_date": "20/03/2023",
        "img_uri": None
    },
]

for trip in testData:
    name, loc, start, end, uri = trip["name"], trip["location"], trip["start_date"], trip["end_date"], trip["img_uri"]
    insertQuery = "INSERT INTO TRIPS (NAME, LOCATION, START_DATE, END_DATE, IMG_URI) values (?, ?, ?, ?, ?);"
    con.execute(insertQuery, (name, loc, start, end, uri))
 
con.commit()