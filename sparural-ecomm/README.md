# prerequisites

* JDK 11 or newer - check `java -version`
* Gradle 7.0 or newer - check `gradle (or ./gradlew) -version`
* Docker compose 2.3 or newer - check `docker compose version`
* Postgresql 13 or newer - check `postgres -V`
* Redis server 6.2 or newer - check `redis-server -v`

---
# environment variables

## BASE
```
SPRING_ACTIVE_PROFILE=development
SPRING_ACTIVE_LABEL=master
SPRING_CONFIG_SERVER_URL=http://k8s-dev-master-1.ctmol.ru/ecomm-development/config-service
SPRING_APPLICATION_NAME={application name}
```

## ENGINE
```
SPARURAL_CONFIG_SERVER=http://192.168.0.186:8888
SPARURAL_JDBC=jdbc:postgresql://192.168.0.186/sparural_dev
SPARURAL_DB_USER=postgres
SPARURAL_DB_PASSWORD=postgres
SPARURAL_LOYMAX_BASE=https://sparch.loymax.tech
SPARURAL_LOYMAX_LOGIN_SOC_REDIRECT=https%3A%2F%2Fadmin.sparural.ru/api/v1/socials/$/login-success
SPARURAL_LOYMAX_SET_SOC_REDIRECT=https%3A%2F%2Fadmin.sparural.ru/api/v1/socials/$/binding-success
SPARURAL_LOG_FILE=./engine.log
SPARURAL_LOG_LEVEL=INFO
SPARURAL_KAFKA=w9100.ctmol.ru:9092
SPARURAL_REDIS_HOST=dev07
SPARURAL_REDIS_PORT=6379
SPARURAL_ENABLE_LOCAL_CACHE=false
SPARURAL_SHOW_API_PERFORMANCE=true
```

## REST
```
SPARURAL_CONFIG_SERVER=http://192.168.0.186:8888
SPARURAL_COOKIE_SECURE=false
SPARURAL_JWT_EXPIRATION_TIME=20000
SPARURAL_JWT_SECRET=testing
SPARURAL_REST_PROXY_PORT=8080
SPARURAL_LOG_FILE=./rest.log
SPARURAL_LOG_LEVEL=INFO
SPARURAL_KAFKA=w9100.ctmol.ru:9092
SPARURAL_REDIS_HOST=dev05
SPARURAL_REDIS_PORT=6379
SPARURAL_ENABLE_LOCAL_CACHE=false
SPARURAL_SHOW_API_PERFORMANCE=true
```

## TRIGGER
```
SPARURAL_CONFIG_SERVER=http://192.168.0.186:8888
SPARURAL_KAFKA=w9100.ctmol.ru:9092
SPARURAL_TRIGGER_JDBC=jdbc:postgresql://dev07/sparural_triggers
SPARURAL_TRIGGER_DB_USER=postgres
SPARURAL_TRIGGER_DB_PASSWORD=postgres
SPARURAL_LOG_FILE=./trigger.log
SPARURAL_LOG_LEVEL=INFO
```

## NOTIF
```
SPARURAL_CONFIG_SERVER=http://192.168.0.186:8888
SPARURAL_KAFKA=w9100.ctmol.ru:9092
SPARURAL_TRIGGER_JDBC=jdbc:postgresql://dev07/sparural_triggers
SPARURAL_TRIGGER_DB_USER=postgres
SPARURAL_TRIGGER_DB_PASSWORD=postgres
SPARURAL_LOG_FILE=./trigger.log
SPARURAL_LOG_LEVEL=INFO
```
---

# manual building

* run postgres docker image

```bash
docker run \
      -e POSTGRES_HOST_AUTH_METHOD=trust \
      -e POSTGRES_DB=sparural_dev \
      -e POSTGRES_USER=postgres \
      -e POSTGRES_PASSWORD=postgres \
      --rm -p 5432:5432 -d postgres
```

* export environment variables

```bash
export SPARURAL_JDBC=jdbc:postgresql://127.0.0.1/sparural_dev
export SPARURAL_DB_USER=postgres
export SPARURAL_DB_PASSWORD=postgres
```

>SPARURAL_JDBC - path to postgresql, for example 172.18.0.10/sparural_dev  
>SPARURAL_DB_USER - postgresql user name  
>SPARURAL_DB_PASSWORD - password of postgresql  

* migrate database

```bash
./gradlew update
```

---

### compile and build project

#### you can use gradle wrapper (./gradlew) or installed gradle

* generate jooq sources

```bash
./gradlew generateJooq
```

* build project

```bash
./gradlew build
```

---

### Running
---

#### up all needed middleware

* run config server

```bash
export SPARURAL_CONFIG_SERVER=http://localhost:8888
cd sparural-config-server
docker build -t config-server .
docker run --rm -d -p 8888:8888 -v `pwd`/../service-configurations:/home/dev01/tools/service-configurations config-server
```

* run postgresql database if it's not running

```bash
docker run \
      -e POSTGRES_HOST_AUTH_METHOD=trust \
      -e POSTGRES_DB=sparural_dev \
      -e POSTGRES_USER=postgres \
      -e POSTGRES_PASSWORD=postgres \
      --rm -p 5432:5432 -d postgres
```

* run redis server if it's not running

```bash
docker run -p 6379:6379 --rm -d redis
 ```

* run kafka if it's not running

```bash
cd ./scripts
docker compose up -d
```

### run services

* run rest proxy service

```bash
cd sparural-rest-proxy/build/libs
java -jar sparural-rest-proxy-${version}.jar --spring.profiles.active=local
```

* run engine service

```bash
cd sparural-engine-service/build/libs
java -jar sparural-engine-service-${version}.jar --spring.profiles.active=local
```
