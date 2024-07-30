# dev-utils

- useful development utils

# template
 - use to create common ```rest -> kafka -> jooq``` project structure with GET POST PUT DELETE methods
 ```
# example usage
 ./template.sh UserAttributes

# will generate java classes for:

# rest/controller/UserAttributesController 
# engine/controllers/UserAttributesController

# engine/service/UserAttributesService
# engine/service/impl/UserAttributesServiceImpl

# engine/repository/UserAttributesRepository
# engine/repository/impl/UserAttributesRepositoryImpl

# engine/entity/UserAttributesEntity
 ```
