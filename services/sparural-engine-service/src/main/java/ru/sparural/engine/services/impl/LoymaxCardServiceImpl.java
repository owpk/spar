package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.LoymaxCard;
import ru.sparural.engine.repositories.LoymaxCardRepository;
import ru.sparural.engine.services.LoymaxCardService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class LoymaxCardServiceImpl implements LoymaxCardService {

    private final LoymaxCardRepository loymaxCardRepository;

    @Override
    public void bindCard(Long cardId, Long loymaxCardId) {
        loymaxCardRepository.batchBindToLoymaxCard(
                List.of(cardId), List.of(loymaxCardId));
    }

    @Override
    public LoymaxCard findByLocalCardId(Long id) {
        return loymaxCardRepository.findByLocalCardId(id)
                .orElseThrow(() -> new ResourceNotFoundException("No loymax card present"));
    }

    @Override
    public void batchBindAsync(List<Long> cards, List<Long> loymaxCardsIds) {
        loymaxCardRepository.batchBindToLoymaxCard(cards, loymaxCardsIds);
    }
}
