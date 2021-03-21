### HSL Visualisation

To run the Android app, open NokiaHslVisualisation directory in Android Studio and run.

To run the Realtime Transport Updates API:

Prerequisites:

Node & Npm
Docker

1. `npm install gtfs -g`
2. `git clone https://github.com/matthewoleary/Realtime-Transport-Updates-API.git`
3. `cd Realtime-Transport-Updates-API`
4. `npm i` 
5. `docker-compose up -d`
6. `docker exec -it docker-gtfs-db bash`
7. `/opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P 10Password`
8. 1> `CREATE DATABASE gtfsdb`
9. 2> `GO`
10. ctrl-c 
11. ctrl-d
12. `npm run import`

This is going to take a while. Go get coffee.

After this you have a docker container with the GTFS data from HSL to make queries to.
