DB_NAME=$1

CONTAINER_NAME=$DB_NAME
DB_USER=postgres

DUMP="/tmp/$DB_NAME.pg_dev.sql"

ssh dev01@dev07 "pg_dump -v -h localhost -U $DB_USER -Fc $DB_NAME" > $DUMP
docker exec -i $CONTAINER_NAME pg_restore -U $DB_USER -v -d $DB_NAME < $DUMP
