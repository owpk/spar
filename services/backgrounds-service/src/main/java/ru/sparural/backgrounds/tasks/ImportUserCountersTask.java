package ru.sparural.backgrounds.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sparural.backgrounds.SparuralKafkaTopics;
import ru.sparural.backgrounds.services.EngineUserService;
import ru.sparural.engine.api.dto.counters.CounterDto;
import ru.sparural.engine.api.dto.user.LoymaxUserDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ImportUserCountersTask implements BackgroundTask {

    private final SparuralKafkaRequestCreator kafkaRequestCreator;
    private final SparuralKafkaTopics kafkaTopics;
    private final EngineUserService engineUserService;

    @Override
    public void action() {
        List<CounterDto> counters = kafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineTopicName())
                .withAction("counters/index")
                .withRequestParameter("offset", null)
                .withRequestParameter("limit", null)
                .sendForEntity();
        log.info("Import counters task:: Current counters size: " + counters.size());
        var loymaxUsers = engineUserService.loadAllLoymaxUsers();
        for (LoymaxUserDto user : loymaxUsers) {
            try {
                log.info("Import counters task:: importing for loymax user: " + user.getLoymaxUserId());
                for (CounterDto counter : counters) {
                    kafkaRequestCreator.createRequestBuilder()
                            .withTopicName(kafkaTopics.getEngineTopicName())
                            .withAction("counters/import")
                            .withRequestParameter("loymaxUserId", user.getLoymaxUserId())
                            .withRequestParameter("loymaxCounterId", counter.getLoymaxId())
                            .withRequestParameter("counterId", counter.getId())
                            .sendForEntity();
                }
            } catch (Exception e) {
                log.error("Cannot import user offer");
            }
        }
    }
}
