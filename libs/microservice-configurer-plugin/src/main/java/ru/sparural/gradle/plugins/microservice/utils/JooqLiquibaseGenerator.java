package ru.sparural.gradle.plugins.microservice.utils;

import liquibase.Liquibase;
import liquibase.exception.DatabaseException;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.apache.commons.lang3.StringUtils;
import org.gradle.api.Project;
import org.gradle.api.tasks.SourceSetContainer;
import org.jooq.codegen.GenerationTool;
import org.jooq.meta.jaxb.*;

import java.io.File;
import java.util.List;
import java.util.Set;

public class JooqLiquibaseGenerator {
    private static final String H2_DRIVER_NAME = "org.h2.Driver";
    private static final String H2_JDBC_URL = "jdbc:h2:mem:builddb;MODE=PostgreSQL";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";

    private final Project project;
    private String scriptPath;
    private String changeLogFile;
    private String packageName;

    public JooqLiquibaseGenerator(Project project) {
        this.project = project;
    }

    public JooqLiquibaseGenerator withScriptPath(String scriptPath) {
        this.scriptPath = scriptPath;
        return this;
    }

    public JooqLiquibaseGenerator withChangeLogFile(String changeLogFile) {
        this.changeLogFile = changeLogFile;
        return this;
    }

    public JooqLiquibaseGenerator withPackageName(String packageName) {
        this.packageName = packageName;
        return this;
    }

    public void generateClasses() throws Exception {
        addJooqSourceDir();
        var jooqTask = project.task("generateJooq").doFirst(action -> {
            generateClassesTask(StringUtils.isEmpty(scriptPath) ? "src/main/resources/liquibase" : scriptPath,
                    StringUtils.isEmpty(changeLogFile) ? "master.yaml" : changeLogFile,
                    StringUtils.isEmpty(packageName) ? "ru.sparural" : packageName);
        });
        project.getTasksByName("compileJava", false)
                .forEach(task -> task.dependsOn(jooqTask));
    }

    private void generateClassesTask(String scriptPath, String changeLogFile, String packageName) {
        ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(new File(project.getProjectDir().getPath() + "/" + scriptPath));
        liquibase.database.Database database = null;
        try {
            database = liquibase.database.DatabaseFactory.getInstance().openDatabase(H2_JDBC_URL, H2_USER, H2_PASSWORD, H2_DRIVER_NAME, null, null, null, resourceAccessor);
            Liquibase liquibase = new Liquibase(changeLogFile, resourceAccessor, database);
            liquibase.update("tableSchema");
            generateJooq(packageName);
        } catch (Exception ex) {
            project.getLogger().error("Error on Jooq generation", ex);
            throw new RuntimeException(ex);
        } finally {
            closeDatabase(database);
        }
    }

    private void generateJooq(String packageName) throws Exception {
        var jsonb = new ForcedType();
        jsonb.setName("varchar");
        jsonb.setIncludeExpression(".*");
        jsonb.setIncludeTypes("JSONB?");

        var inet = new ForcedType();
        jsonb.setName("varchar");
        jsonb.setIncludeExpression(".*");
        jsonb.setIncludeTypes("INET");

        var forcedTypes = List.of(jsonb, inet);

        Configuration cfg = new Configuration()
                .withJdbc(new Jdbc()
                        .withDriver(H2_DRIVER_NAME)
                        .withUrl(H2_JDBC_URL)
                        .withUser(H2_USER)
                        .withPassword(H2_PASSWORD)
                ).withGenerator(new Generator()
                        .withDatabase(new Database()
                                .withForcedTypes(forcedTypes)
                                .withSchemata(new SchemaMappingType().withInputSchema("PUBLIC"))
                        )
                        .withTarget(new Target()
                                .withPackageName(packageName)
                                .withDirectory(project.getBuildDir().getPath() + "/jooq/main/java")
                        )
                        .withGenerate(jooqGenerate())

                );

        GenerationTool.generate(cfg);
    }

    private Generate jooqGenerate() {
        return new Generate().withRelations(Boolean.TRUE)
                .withDeprecated(Boolean.FALSE)
                .withRecords(Boolean.TRUE)
                .withImmutablePojos(Boolean.FALSE)
                .withDaos(Boolean.TRUE)
                .withFluentSetters(Boolean.TRUE);
    }

    private void addJooqSourceDir() {
        SourceSetContainer extension = (SourceSetContainer) project.getExtensions().getByName("sourceSets");
        Set<File> sourceDirs = extension.getByName("main").getJava().getSrcDirs();
        sourceDirs.add(new File(project.getBuildDir().getPath() + "/jooq/main/java/"));
        extension.getByName("main").getJava().setSrcDirs(sourceDirs);
    }

    private void closeDatabase(liquibase.database.Database database) {
        try {
            if (database != null) {
                database.close();
            }
        } catch (DatabaseException ex) {

        }
    }
}
