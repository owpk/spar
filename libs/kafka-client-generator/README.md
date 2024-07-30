## kafka client generator plugin

- apply plugin  
> settings.gradle
```gradle
pluginManagement {
    repositories {
        maven { url 'http://nexus.ctmol.ru/repository/maven-public/'; allowInsecureProtocol = true }
    }
```

> build.gradle
```gradle
plugins {
    id 'ru.sparural.kafka-client-generator' version '1.2.3'
}
```


- example gradle build dsl configuration

```gradle
kafka {
    kafkaControllerAnnotationName = "KafkaSparuralController"
    kafkaMappingAnnotationName = "KafkaSparuralMapping"
    serviceName = "engine"

    source {
        moduleName = "sparural-engine-service"
        needToBuild = false

       gitConfig {
            remoteBranch = "master"
            remoteURI = "https://dev02.ctmol.ru/ecomm/backend-api-service/engine-service.git"
        }
    }
}
```
