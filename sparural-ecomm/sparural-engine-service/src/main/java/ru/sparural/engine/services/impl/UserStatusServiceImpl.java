package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.screen.mycards.ClientStatus;
import ru.sparural.engine.api.dto.screen.mycards.Status;
import ru.sparural.engine.entity.ClientStatusEntity;
import ru.sparural.engine.repositories.StatusRepository;
import ru.sparural.engine.repositories.UserStatusRepository;
import ru.sparural.engine.services.UserStatusService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserStatusServiceImpl implements UserStatusService {

    private final StatusRepository statusRepository;
    private final UserStatusRepository userStatusRepository;

    @Override
    public void saveOrUpdate(Status status, Long userId) {
        if (status.getClientStatus().getName() != null && status.getClientStatus().getThreshold() != null) {
            if (status == null || status.getClientStatus() == null) return;
            ClientStatusEntity clientStatus = statusRepository.saveOrUpdate(
                    createEntityFromLoymax(status.getClientStatus())).orElseThrow(
                    () -> new ResourceNotFoundException("Cannot save client status"));
            userStatusRepository.bind(clientStatus.getId(), userId,
                    status.getCurrentValue(), status.getLeftUntilNextStatus());
        }
    }

    @Override
    public ClientStatusEntity createEntityFromLoymax(ClientStatus clientStatus) {
        var clientStatusEntity = new ClientStatusEntity();
        clientStatusEntity.setName(clientStatus.getName());
        clientStatusEntity.setThreshold(clientStatus.getThreshold());
        return clientStatusEntity;
    }
}