package ru.sparural.engine.repositories;

import ru.sparural.engine.entity.Content;

import java.util.List;
import java.util.Optional;

public interface ContentsRepository {
    Optional<Content> create(Content data);

    boolean deleteByAlias(String alias);

    Content updateByAlias(String alias, Content data);

    Content getByAlias(String alias);

    List<Content> getList(int offset, int limit);
}
