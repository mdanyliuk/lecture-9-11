Before run project: <br>
`git clone https://github.com/mdanyliuk/lecture-9-11.git` <br>
`cd lecture-9-11` <br>
Change password for user root in file src/main/resources/application.properties <br>
<br>
To run tests: <br>
`mvn test` <br>
To run project: <br>
`./mvnw spring-boot:run` <br>
(or you can run main method of CalcioApplication.class from your IDE) <br>
<br>
### Endpoints
GET `http://localhost:8080/api/players` get list of all players <br>
GET `http://localhost:8080/api/clubs` get list of all football clubs <br>
GET `http://localhost:8080/api/players/{id}` find player by id <br>
POST `http://localhost:8080/api/players` create new player <br>
example of correct request body: <br>
`{
"name": "Lionel Messi",
"position": "attacker",
"clubId": 3
}` <br>
PUT `http://localhost:8080/api/players/{id}` update an existing player by id <br>
example of correct request body: <br>
`{
"name": "Mario Pasalic",
"position": "midfielder",
"clubId": 3
}` <br>
DELETE `http://localhost:8080/api/players/{id}` delete player by id <br>
POST `http://localhost:8080/api/players/_search` search players by position and club <br>
example of correct request body: <br>
`{
"position": "midfielder",
"clubId": 1,
"page": 0,
"size": 3
}` <br>