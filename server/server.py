import flask
import sqlite3
from flask import request

app = flask.Flask(__name__)

#app.config["DEBUG"] = True # Enable debug mode to enable hot-reloader.
@app.route('/trips')
def trips():
    """trip_name = request.args.get('trip_name', '')
    
    #Connects to the db
    # The .db file is created automatically if it does not exist
    con = sqlite3.connect('globey_app.db')

    if len(trip_name) > 0:
        # insert into db
        insertQuery = "INSERT INTO STUDENT (NAME) values (?);"
        con.execute(insertQuery, (trip_name, ))
        con.commit()

    cursor = con.execute("SELECT NAME from STUDENT;")
    students = []
    for row in cursor:
        students.append(row[0])
    con.close()
    
    outdata = {
        "course_code": "COMP3330",
        "course_name": "Interactive Mobile Application Design and Programming",
        "teachers": ["Dr. T.W. Chim", "Mr. C.K. Lai", "Mr. X. Wang"],
        "students": students
        }
    return outdata"""
    
    con = sqlite3.connect('globey_app.db') 
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

