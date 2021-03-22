### HSL Visualisation

[![CI Workflow](https://github.com/xpyx/nokia-hsl/actions/workflows/android-workflow.yaml/badge.svg)](https://github.com/xpyx/nokia-hsl/actions/workflows/android-workflow.yaml)

To run the Android app, open NokiaHslVisualisation directory in Android Studio and run.

To run the Realtime Transport Updates API:

Prerequisites:

[Node](https://nodejs.org/en/) | 
[Npm](https://www.npmjs.com/) | 
[Docker](https://www.docker.com/products/docker-desktop)

1. `npm install gtfs -g`
2. `cd Realtime-Transport-Updates-API`
3. `npm i` 
4. `docker-compose up -d`
5. `docker exec -it docker-gtfs-db bash`
6. `/opt/mssql-tools/bin/sqlcmd -S localhost -U SA -P 10Password`
7. 1> `CREATE DATABASE gtfsdb`
8. 2> `GO`
9. ctrl-c 
10. ctrl-d
11. `npm run import`

This is going to take a while. Go get coffee.

After this you have a docker container with the GTFS data from HSL to make queries to.
