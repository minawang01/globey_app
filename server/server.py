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

        name, loc, start, end, file_path = trip["name"], trip["location"], trip["start"], trip["end"], trip["file_path"]
        insertQuery = "INSERT INTO TRIPS (NAME, LOCATION, START_DATE, END_DATE, FILE_PATH) values (?,?,?,?,?);"
        con.execute(insertQuery, (name,loc,start,end, file_path))
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
            "file_path": row[5]
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
    deleteQuery = "DELETE FROM NOTES WHERE ID=?;"
    con.execute(deleteQuery, (position,))
    con.commit()
    con.close()
        
    outdata = {
        "table": "trips",
        "sucess": True
    }
    return outdata

@app.route("/edit_trip", methods=["POST"])
def change_trip():
    print("app route change_trips working")
    con = sqlite3.connect("globey_app.db")
    json = request.get_json()
    trip_id = json["trip_id"]
    text = json["text"]
    element = json["element"]
    
    query = "UPDATE TRIPS SET "+element +"=? WHERE ID=?;"
    con.execute(query, (text, trip_id,))
    con.commit()
    
    query = "SELECT * FROM TRIPS WHERE ID=?;"
    cursor = con.execute(query, (trip_id,))
    row = cursor.fetchall()
    trip = {
        "id": row[0][0],
        "trip_id": row[0][1],
        "note": row[0][2],
        "updated_time": row[0][3] 
    }
    con.close()
    return trip

@app.route("/notes", methods=["POST", "GET"])
def get_notes():
    con = sqlite3.connect("globey_app.db")
    json = request.get_json()
    trip_id = json["id"]
    query = "SELECT * from NOTES WHERE TRIP_ID=?;"
    cursor = con.execute(query, (trip_id,))
    notes = []
    for row in cursor:
        notes.append({
            "id": row[0],
            "trip_id": row[1],
            "note": row[2],
            "updated_time": row[3]
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
    trip_id = json["id"]
    note = json["text"]
    print(trip_id, note)
    query = "INSERT INTO NOTES (TRIP_ID, NOTE) values (?,?);"
    con.execute(query, (trip_id, note))
    con.commit()
    cursor = con.execute("SELECT * FROM NOTES WHERE ID = (SELECT MAX(ID) FROM NOTES);")
    row = cursor.fetchall()
    notes = {
        "id": row[0][0],
        "trip_id": row[0][1],
        "note": row[0][2],
        "updated_time": row[0][3]
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
    note = json["text"]
    note_id = json["note_id"]
    query = "UPDATE NOTES SET NOTE=? WHERE ID=?;"
    con.execute(query, (note, note_id,))
    query = "UPDATE NOTES SET UPDATED_TIME=CURRENT_TIMESTAMP WHERE ID=?;"
    con.execute(query, (note_id,))
    con.commit()
    query = "SELECT * FROM NOTES WHERE ID=?;"
    cursor = con.execute(query, (note_id,))
    row = cursor.fetchall()
    notes = {
        "id": row[0][0],
        "trip_id": row[0][1],
        "note": row[0][2],
        "updated_time": row[0][3]
    }
    con.close()
    outdata = {
            "table": "notes",
            "notes": notes
        }
    return outdata  
    

@app.route("/delete_notes", methods=["POST"])
def delete_notes():
    con = sqlite3.connect("globey_app.db")
    json = request.get_json()
    note_id = json["note_id"]
    query = "DELETE FROM NOTES WHERE ID=?;"
    con.execute(query, (note_id,))
    con.commit()
    con.close()
    outdata = {
        "table":"notes",
        "success": True
    }
    return outdata
    
    
    
# adds host="0.0.0.0" to make the server publicly available
app.run(host="0.0.0.0")