package ru.sparural.triggers.handlers.task;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import ru.sparural.triggerapi.dto.MessageTemplateDto;
import ru.sparural.triggers.annotations.SparuralUserTask;
import ru.sparural.triggers.model.EventType;
import ru.sparural.triggers.tasks.UserTask;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.stream.Collectors;
import ru.sparural.engine.api.dto.user.UserFilterDto;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
public class UserTasksHandler {
    private Map<EventType, UserTask> userTaskMap;
    private final ApplicationContext ctx;

    @PostConstruct
    private void init() {
        userTaskMap = ctx.getBeansWithAnnotation(SparuralUserTask.class)
                .entrySet()
                .stream()
                .collect(Collectors.toMap(
                        k -> k.getValue().getClass().getAnnotation(SparuralUserTask.class).value(),
                        v -> ((UserTask) v.getValue()))
                );
    }

    public void handleTask(UserFilterDto filter, EventType eventType, MessageTemplateDto messageTemplate) {
        userTaskMap.get(eventType).executeUserTask(messageTemplate, filter);
    }
}
