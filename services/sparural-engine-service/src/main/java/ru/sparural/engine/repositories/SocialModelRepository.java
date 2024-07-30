package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Social;

import java.util.List;
import java.util.Optional;

public interface SocialModelRepository {

    List<Social> getSocialList();

    Social updateById(Long id, Social data);

    Optional<Social> getSocialName(String name);
}
