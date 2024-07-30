#!/bin/sh
DB_NAME=$1

docker stop $DB_NAME 2>/dev/null
docker rm $DB_NAME 2>/dev/null

#RANDOM_PORT=`shuf -i 5000-65000 -n 1`
PORT=$2
echo "EXPOSED WITH PORT: $PORT"
docker run --name $DB_NAME -p $PORT:5432 -e POSTGRES_PASSWORD=postgres -e POSTGRES_USER=postgres -e POSTGRES_DB=$DB_NAME -d postgres

sleep 5
./dump_dev_db.sh $DB_NAME
docker ps
