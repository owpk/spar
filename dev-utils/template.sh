pname="sparural"
engine_dir="`pwd`/${pname}-engine-service"
rest_dir="`pwd`/${pname}-rest-proxy"

package_name="ru.sparural"
base_path="/src/main/java/ru/sparural"

engine_base_path="${engine_dir}/${base_path}/engine/"
rest_base_path="${rest_dir}/${base_path}/rest/"

name=$1
escape_char=$name
kebab_name=$(sed --expression 's/\([A-Z]\)/-\L\1/g' --expression 's/^-//' <<< "$name")

cat << EOF >> $rest_base_path/controller/"${name}Controller.java"
package ${package_name}.rest.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;
import ru.sparural.rest.security.UserPrincipal;
import ru.sparural.rest.config.KafkaTopics;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.utils.Constants;

import java.util.List;

import ru.sparural.engine.api.dto.${escape_char};

@RestController
@RequestMapping(value = "\${rest.base-url}/\${rest.version}/${kebab_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
@RequiredArgsConstructor
@Api(tags = "${kebab_name}")
public class ${name}Controller {
    
    private final SparuralKafkaRequestCreator restToKafkaService;
    private final KafkaTopics kafkaTopics;

    @GetMapping
    public DataResponse<List<AccountsDto>> index(@RequestParam(defaultValue = "0") Integer offset,
                                                 @RequestParam(defaultValue = "30") Integer limit) {
        List<${escape_char}> result = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("${kebab_name}/index")
                .withRequestParameter("offset", offset)
                .withRequestParameter("limit", limit)
                .sendForEntity();
        return DataResponse.<List<${escape_char}>>builder()
                .success(true)
                .data(result)
                .version(Constants.VERSION)
                .build();
    }

    @GetMapping("/{id}")
    public DataResponse<${escape_char}> get(@PathVariable Long id) {
        UserAttributesDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("${kebab_name}/get")
                .withRequestParameter("id", id)
                .sendForEntity();
        return DataResponse.<${escape_char}>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PostMapping
    public DataResponse<${escape_char}> create(@Valid @Parameter @RequestBody DataRequest<${escape_char}> restRequest) {
        UserAttributesDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("${kebab_name}/create")
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<${escape_char}>builder()
                .data(response)
                .success(true)
                .build();
    }

    @PutMapping("/{id}")
    public DataResponse<${escape_char}> update(@Valid @Parameter @RequestBody DataRequest<${escape_char}> restRequest,
                                               @PathVariable Long id) {
        UserAttributesDto response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("${kebab_name}/update")
                .withRequestParameter("id", id)
                .withRequestBody(restRequest.getData())
                .sendForEntity();
        return DataResponse.<${escape_char}>builder()
                .data(response)
                .success(true)
                .build();
    }

    @DeleteMapping("/{id}")
    @IsManagerOrAdmin
    public UnwrappedGenericDto<EmptyObject> delete(@PathVariable Long id) {
        Boolean response = restToKafkaService.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineRequestTopicName())
                .withAction("${kebab_name}/delete")
                .withRequestParameter("id", id)
                .sendForEntity();
        return UnwrappedGenericDto.<EmptyObject>builder()
                .success(response)
                .build();
    }
}
EOF


cat << EOF >> $engine_base_path/controllers/"${name}Controller.java"
package ${package_name}.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import ${package_name}.engine.api.dto.user.${escape_char}Dto;
import ${package_name}.engine.entity.${escape_char}Entity;
import ${package_name}.engine.services.${name}Service;
import ${package_name}.engine.utils.DtoMapperUtils;
import ${package_name}.kafka.annotation.KafkaSparuralController;
import ${package_name}.kafka.annotation.KafkaSparuralMapping;
import ${package_name}.kafka.annotation.Payload;
import ${package_name}.kafka.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "\${sparural.kafka.request-topics.engine}")
@Slf4j
public class ${name}Controller {
    private final ${name}Service ${name,}Service;
    private final ModelMapper modelMapper;

    @KafkaSparuralMapping("${kebab_name}/index")
    public List<UserAttributesDto> list(@RequestParam Integer offset,
                                        @RequestParam Integer limit) {
        var result = ${name,}Service.index(offset, limit);
        return result.stream()
                .map(attr -> modelMapper.map(attr, ${escape_char}Dto.class))
                .collect(Collectors.toList());
    }

    @KafkaSparuralMapping("${kebab_name}/get")
    public UserAttributesDto get(@RequestParam Long id) {
        var result = ${name,}Service.get(id);
        return modelMapper.map(result, ${escape_char}Dto.class);
    }

    @KafkaSparuralMapping("${kebab_name}/create")
    public UserAttributesDto create(@Payload ${escape_char}Dto data) {
        var result =  ${name,}Service.create(modelMapper.map(data, ${escape_char}Entity.class));
        return modelMapper.map(result, ${escape_char}Dto.class);
    }

    @KafkaSparuralMapping("${kebab_name}/update")
    public UserAttributesDto update(@Payload ${escape_char}Dto updateDto, @RequestParam Long id) {
        var result = ${name,}Service.update(id, modelMapper.map(updateDto, ${escape_char}Entity.class));
        return modelMapper.map(result, ${escape_char}Dto.class);
    }

    @KafkaSparuralMapping("${kebab_name}/delete")
    public Boolean delete(@RequestParam Long id) {
        return ${name,}Service.delete(id);
    }

}
EOF


cat << EOF >> $engine_base_path/services/"${name}Service.java"
package ${package_name}.engine.services;

import ${package_name}.engine.api.dto.user.${escape_char}Dto;
import ${package_name}.engine.entity.${escape_char}Entity;

import java.util.List;

public interface ${name}Service {
    List<${escape_char}Entity> index(Integer offset, Integer limit);

    ${escape_char}Entity get(Long id);

    ${escape_char}Entity update(Long id, ${escape_char}Entity data);

    Boolean delete(Long id);

    ${escape_char}Entity create(${escape_char}Entity data);
}
EOF


cat << EOF >> $engine_base_path/services/impl/"${name}ServiceImpl.java"
package ${package_name}.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ${package_name}.engine.entity.${escape_char}Entity;
import ${package_name}.engine.repositories.${name}Repository;
import ${package_name}.engine.services.${name}Service;
import ${package_name}.engine.services.exception.ResourceNotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ${name}ServiceImpl implements ${name}Service {
    private final ${name}Repository ${name,}Repository;

    @Override
    public List<${escape_char}Entity> index(Integer offset, Integer limit) {
        return ${name,}Repository.list(offset, limit);
    }

    @Override
    public ${escape_char}Entity get(Long id) {
        return ${name,}Repository.fetchById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.valueOf(id)));
    }

    @Override
    public ${escape_char}Entity update(Long id, ${escape_char}Entity data) {
        return ${name,}Repository.update(id, data)
                .orElseThrow(() -> new RuntimeException("Cannot update ${kebab_name} with id: " + id));
    }

    @Override
    public Boolean delete(Long id) {
        return ${name,}Repository.delete(id);
    }

    @Override
    public ${escape_char}Entity create(${name}Entity data) {
        return ${name,}Repository.create(data)
                .orElseThrow(() -> new RuntimeException("Cannot create ${kebab_name}"));
    }
}
EOF


cat << EOF >> $engine_base_path/repositories/"${name}Repository.java"
package ${package_name}.engine.repositories;

import ${package_name}.engine.entity.${escape_char}Entity;

import java.util.List;
import java.util.Optional;

public interface ${name}Repository {
    Optional<${escape_char}Entity> fetchById(Long id);

    List<${escape_char}Entity> list(Integer offset, Integer limit);

    Boolean delete(Long id);

    Optional<${escape_char}Entity> update(Long id, ${escape_char}Entity entity);

    Optional<${escape_char}Entity> create(${escape_char}Entity data);

}
EOF


cat << EOF >> $engine_base_path/repositories/impl/"${name}RepositoryImpl.java"
package ${package_name}.engine.repositories.impl;

import lombok.RequiredArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import ${package_name}.engine.entity.UserAttributesEntity;
import ${package_name}.engine.repositories.UserAttributesRepository;
import ${package_name}.tables.UsersAttributes;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ${name}Impl implements ${name}Repository {
    private final DSLContext dslContext;
    private final ${name} table = ${name}.{replace_me};

    @Override
    public Optional<${escape_char}Entity> fetchById(Long id) {
        return dslContext.select().from(table)
                .where(table.ID.eq(id))
                .fetchOptionalInto(${escape_char}Entity.class);
    }

    @Override
    public List<${escape_char}Entity> list(Integer offset, Integer limit) {
        return dslContext.select().from(table)
                .offset(offset)
                .limit(limit)
                .fetchInto(${escape_char}Entity.class);
    }

    @Override
    public Boolean delete(Long id) {
        return dslContext
                .delete(table)
                .where(table.ID.eq(id))
                .execute() > 0;
    }

    @Override
    public Optional<${escape_char}Entity> update(Long id, ${escape_char}Entity entity) {
        return dslContext.update(table)
                .set()
                .where(table.ID.eq(id))
                .returning()
                .fetchOptionalInto(${escape_char}Entity.class);
    }

    @Override
    public Optional<${escape_char}Entity> create(${escape_char}Entity data) {
        return dslContext.insertInto(table)
                .set()
                .returning()
                .fetchOptionalInto(${escape_char}Entity.class);
    }
}
EOF


cat << EOF >> $engine_base_path/entity/"${name}Entity.java"
package ${package_name}.engine.entity;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ${escape_char}Entity {
    
}
EOF
