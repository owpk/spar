package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.loymax.services.LoymaxService;
import ru.sparural.engine.repositories.CountersRepository;
import ru.sparural.engine.repositories.CountersValuesRepository;
import ru.sparural.engine.services.CounterService;
import ru.sparural.engine.services.UserService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;
import ru.sparural.tables.pojos.Counters;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CounterServiceImpl implements CounterService {
    private final CountersRepository countersRepository;
    private final CountersValuesRepository countersValuesRepository;
    private final UserService userService;
    private final LoymaxService loymaxService;

    @Override
    public List<Counters> index(Integer offset, Integer limit) {
        return countersRepository.list(offset, limit);
    }

    @Override
    public Counters get(Long id) {
        return countersRepository.fetchById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Counter not found with id: " + id));
    }

    @Override
    public Counters create(Counters data) {
        return countersRepository.saveOrUpdate(data)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create counter: " + data));
    }

    @Override
    public Counters update(Long id, Counters data) {
        return countersRepository.update(id, data)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot update counter: " + data));
    }

    @Override
    public Boolean delete(Long id) {
        return countersRepository.delete(id);
    }

    @Override
    public void importAndBindForUser(Long loymaxUserId, Integer loymaxCounterId, Long counterId) {
        var userId = userService.findUserIdsByLoymaxUserIds(List.of(loymaxUserId)).stream().findAny()
                .orElseThrow(() -> new ResourceNotFoundException("User not found for loymax user: " + loymaxUserId))
                .getUserId();
        var loymaxUser = loymaxService.getByLocalUserId(userId);
        var counterInfo = loymaxService.counterInfo(Long.valueOf(loymaxCounterId), loymaxUser.getLoymaxUserId());
        if (!counterInfo.isEmpty()) {
            var value = counterInfo.get(0).getValue();
            countersValuesRepository.bindCounterToUser(counterId, userId, Integer.valueOf(value));
        }
    }
}
