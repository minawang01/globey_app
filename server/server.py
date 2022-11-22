import flask
import sqlite3
from flask import request
import json

app = flask.Flask(__name__)

#app.config["DEBUG"] = True # Enable debug mode to enable hot-reloader.
@app.route('/trips', methods=["GET", "POST"])
def trips():
    print("app route trips working")
    con = sqlite3.connect('globey_app.db')
    if request.method == 'POST':
        print("request method is post")
        trip = request.get_json()
        name, loc, start, end = trip["name"], trip["location"], trip["start"], trip["end"]
        insertQuery = "INSERT INTO TRIPS (NAME, LOCATION, START_DATE, END_DATE) values (?,?,?,?);"
        con.execute(insertQuery, (name,loc,start,end ))
        con.commit()

    cursor = con.execute("SELECT * from TRIPS;")
    trips = []
    for row in cursor:
        print(row)    
        trips.append( {
            "id": row[0],
            "name": row[1],
            "location": row[2],
            "start_date": row[3],
            "end_date": row[4],
        })
    con.close()
        
    outdata = {
        "table": "trips",
        "trips": trips
    }
    

    return outdata
    
# adds host="0.0.0.0" to make the server publicly available
app.run(host="0.0.0.0")