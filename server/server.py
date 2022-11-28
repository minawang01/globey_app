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
        name, loc, start, end, uri = trip["name"], trip["location"], trip["start"], trip["end"], trip["uri"]
        insertQuery = "INSERT INTO TRIPS (NAME, LOCATION, START_DATE, END_DATE, IMG_URI) values (?,?,?,?,?);"
        con.execute(insertQuery, (name,loc,start,end, uri))
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
            "img_uri": row[5]
        })
    con.close()
        
    outdata = {
        "table": "trips",
        "trips": trips
    }
    return outdata
   
@app.route('/delete_trips', methods=["POST"])
def delete_trips():
    print("app route delete_trips working")
    con = sqlite3.connect("globey_app.db")
    print("request method is post")
    pos_json = request.get_json()
    position = pos_json["id"]
    deleteQuery = "DELETE FROM TRIPS WHERE ID=?;"
    con.execute(deleteQuery, (position,))
    con.commit()
       
    cursor = con.execute("SELECT * from TRIPS;")
    trips = []
    for row in cursor:  
        trips.append( {
            "id": row[0],
            "name": row[1],
            "location": row[2],
            "start_date": row[3],
            "end_date": row[4],
            "img_uri": row[5]
        })
    con.close()
        
    outdata = {
        "table": "trips",
        "trips": trips
    }
    return outdata

@app.route("/change_trip", methods=["POST"])
def change_trip():
    print("app route change_trips working")
    con = sqlite3.connect("globey_app.db")
    json = request.get_json()
    
    #create code to change trip
    
    
    cursor = con.execute("SELECT * from TRIPS;")
    trips = []
    for row in cursor:  
        trips.append( {
            "id": row[0],
            "name": row[1],
            "location": row[2],
            "start_date": row[3],
            "end_date": row[4],
            "img_uri": row[5]
        })
    con.close()

@app.route("/notes", methods=["POST", "GET"])
def get_notes():
    con = sqlite3.connect("globey_app.db")
    
    json = request.get_json()
    position = json["id"]
    query = "SELECT * from NOTES WHERE ID=?;"
    cursor = con.execute(query, (position,))
    notes = []
    for row in cursor:
        notes.append({
            "timestamp": row[0],
            "id": row[1],
            "text": row[2]  
        })
        
    con.close()
        
    outdata = {
        "table": "notes",
        "notes": notes
    }
    return outdata

@app.route("/add_notes", methods=["POST"])
def add_notes():
    con = sqlite3.connect("globey_app.db")
    json = request.get_json()
    position = json["id"]
    text = json["text"]
    query = "INSERT INTO NOTES (ID, NOTE) values (?,?);"
    con.execute(query, (position, text))
    con.commit()
    cursor = con.execute("SELECT * FROM NOTES WHERE TIMESTAMP = (SELECT MAX(TIMESTAMP) FROM NOTES);")
    print(cursor)
    for row in cursor:
        print(row)
        notes = {
            "timestamp": row[0],
            "id": row[1],
            "text": row[2]
        }
    print(notes)
    con.close()
    outdata = {
            "table": "notes",
            "notes": notes
        }
    return outdata

@app.route("/edit_notes", methods=["POST"])
def edit_notes():
    con = sqlite3.connect("globey_app.db")
    json = request.get_json()
    position = json["id"]
    text = json["text"]
    timestamp = json["timestamp"]
    query = "UPDATE NOTES SET NOTE=? WHERE ID=? AND TIMESTAMP=?;"
    con.execute(query, (text, position, timestamp))
    notes = {
        "timestamp": timestamp,
        "id": id,
        "text": text
    }
        
    con.close()
    outdata = {
            "table": "notes",
            "notes": notes
        }
    return outdata  
    

# adds host="0.0.0.0" to make the server publicly available
app.run(host="0.0.0.0")