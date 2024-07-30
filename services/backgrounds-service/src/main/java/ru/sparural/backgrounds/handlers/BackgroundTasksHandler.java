package ru.sparural.backgrounds.handlers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.sparural.backgrounds.tasks.BackgroundTask;
import ru.sparural.backgrounds.tasks.UserAttributesImportTask;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BackgroundTasksHandler {

    private final List<BackgroundTask> backgroundTaskList;
    private final UserAttributesImportTask userAttributesImportTask;
    private ExecutorService executorService;
    @Value("${sparural.backgrounds.task-thread-pool}")
    private Integer threadCount;

    @PostConstruct
    private void init() {
        executorService = Executors.newFixedThreadPool(threadCount);
    }

    @Scheduled(fixedRateString = "${sparural.schedule-delay}")
    private void initTasks() {
        backgroundTaskList.forEach(task -> {
            log.info("executing background task: " + task.getClass().getSimpleName());
            executorService.submit(task::action);
        });
    }
}
