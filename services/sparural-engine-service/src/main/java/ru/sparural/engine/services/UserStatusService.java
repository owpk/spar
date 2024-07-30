package ru.sparural.engine.services;

import ru.sparural.engine.api.dto.screen.mycards.ClientStatus;
import ru.sparural.engine.api.dto.screen.mycards.Status;
import ru.sparural.engine.entity.ClientStatusEntity;

/**
 * @author Vorobyev Vyacheslav
 */
public interface UserStatusService {

    void saveOrUpdate(Status status, Long userId);

    ClientStatusEntity createEntityFromLoymax(ClientStatus clientStatus);
}
