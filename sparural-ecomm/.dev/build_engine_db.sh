cd ./docker/postgres/

./psg_engine_dev.sh

cd ../../

export SPARURAL_JDBC=jdbc:postgresql://localhost/sparural_dev
export SPARURAL_DB_USER=postgres
export SPARURAL_DB_PASSWORD=postgres

GRADLE_CMD="../gradlew -p ../ :sparural-engine-service"

$GRADLE_CMD:update
$GRADLE_CMD:generateJooq
