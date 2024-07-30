package ru.sparural.backgrounds.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.sparural.backgrounds.SparuralKafkaTopics;
import ru.sparural.backgrounds.services.EngineUserService;
import ru.sparural.engine.api.dto.OffersCounterDto;
import ru.sparural.kafka.utils.SparuralKafkaRequestCreator;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class ImportOffersUserCountersTask implements BackgroundTask {

    private final SparuralKafkaRequestCreator kafkaRequestCreator;
    private final SparuralKafkaTopics kafkaTopics;
    private final EngineUserService engineUserService;

    @Override
    public void action() {
        List<OffersCounterDto> counters = kafkaRequestCreator.createRequestBuilder()
                .withTopicName(kafkaTopics.getEngineTopicName())
                .withAction("offers-counters/index")
                .withRequestParameter("offset", null)
                .withRequestParameter("limit", null)
                .sendForEntity();
        log.info("Import counters task:: Current counters size: " + counters.size());
        var loymaxUsers = engineUserService.loadAllLoymaxUsers();
        loymaxUsers.forEach(user -> {
            try {
                log.info("Import counters task:: importing for loymax user: " + user.getLoymaxUserId());
                counters.forEach(counter -> kafkaRequestCreator.createRequestBuilder()
                        .withTopicName(kafkaTopics.getEngineTopicName())
                        .withAction("offers-counters/import")
                        .withRequestParameter("loymaxUserId", user.getLoymaxUserId())
                        .withRequestParameter("loymaxCounterId", counter.getLoymaxCounterId())
                        .withRequestParameter("counterId", counter.getId())
                        .send()
                );
            } catch (Exception e) {
                log.error("Cannot import user offer");
            }
        });
    }
}
