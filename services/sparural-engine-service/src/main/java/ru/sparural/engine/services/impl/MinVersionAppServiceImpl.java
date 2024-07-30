package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.entity.MinVersionAppEntity;
import ru.sparural.engine.repositories.MinVersionAppRepository;
import ru.sparural.engine.services.MinVersionAppService;
import ru.sparural.engine.services.exception.ResourceNotFoundException;

import java.util.List;

/**
 * @author Vorobyev Vyacheslav
 */
@Service
@RequiredArgsConstructor
public class MinVersionAppServiceImpl implements MinVersionAppService {
    private final MinVersionAppRepository minVersionAppRepository;

    @Override
    public List<MinVersionAppEntity> getAll() {
        return minVersionAppRepository.getALl();
    }

    @Override
    public MinVersionAppEntity getLast() {
        return minVersionAppRepository.getLast()
                .orElseThrow(() -> new ResourceNotFoundException("No 'version' record found"));
    }

    @Override
    public MinVersionAppEntity create(MinVersionAppEntity minVersionAppEntity) {
        return minVersionAppRepository.create(minVersionAppEntity)
                .orElseThrow(() -> new ResourceNotFoundException("Cannot create 'min version app' record"));
    }
}
