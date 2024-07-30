package ru.sparural.engine.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.sparural.engine.api.dto.PersonalOfferUserDto;
import ru.sparural.engine.entity.PersonalOfferUserEntity;
import ru.sparural.engine.repositories.PersonalOfferUserRepository;
import ru.sparural.engine.services.PersonalOfferUserService;
import ru.sparural.engine.services.exception.ServiceException;
import ru.sparural.engine.utils.DtoMapperUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonalOfferUserServiceImpl implements PersonalOfferUserService {

    private final DtoMapperUtils dtoMapperUtils;
    private final PersonalOfferUserRepository personalOfferUserRepository;

    @Override
    public PersonalOfferUserDto getByUserIdAndOfferId(Long userId, Long offerId) {
        return createDto(personalOfferUserRepository.getByUserIdAndOfferId(userId, offerId).get());
    }

    @Override
    public PersonalOfferUserDto createDto(PersonalOfferUserEntity entity) {
        return dtoMapperUtils.convert(entity, PersonalOfferUserDto.class);
    }

    @Override
    public PersonalOfferUserEntity createEntity(PersonalOfferUserDto dto) {
        return dtoMapperUtils.convert(dto, PersonalOfferUserEntity.class);
    }

    @Override
    public Boolean checkIfExist(Long userId, Long offerId) {
        return personalOfferUserRepository.getByUserIdAndOfferId(userId, offerId).isPresent();
    }

    @Override
    public PersonalOfferUserDto create(PersonalOfferUserDto dto) {
        return createDto(personalOfferUserRepository.create(createEntity(dto))
                .orElseThrow(() -> new ServiceException("Failed to create personal offers user")));
    }

    @Override
    public void updateData(PersonalOfferUserDto dto) {
        createDto(personalOfferUserRepository.create(createEntity(dto))
                .orElseThrow(() -> new ServiceException("Failed to update date in personal offers user")));
    }

    @Override
    public List<PersonalOfferUserDto> listByUserId(Long userId) {
        List<PersonalOfferUserEntity> list = personalOfferUserRepository.listByUserId(userId);
        if (list.isEmpty()) {
            return null;
        }
        return createListDto(list);
    }

    @Override
    public List<PersonalOfferUserDto> createListDto(List<PersonalOfferUserEntity> list) {
        return dtoMapperUtils.convertList(PersonalOfferUserDto.class, list);
    }
}
