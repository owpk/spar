package ru.sparural.engine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.sparural.engine.api.dto.FileDto;
import ru.sparural.engine.api.dto.UserRequestsDto;
import ru.sparural.engine.api.dto.request.UserRequestDto;
import ru.sparural.engine.api.dto.user.UserProfileDto;
import ru.sparural.engine.api.enums.FileDocumentTypeField;
import ru.sparural.engine.services.FileDocumentService;
import ru.sparural.engine.services.UserRequestsService;
import ru.sparural.engine.services.UserRequestsSubjectsService;
import ru.sparural.engine.services.UserService;
import ru.sparural.kafka.annotation.KafkaSparuralController;
import ru.sparural.kafka.annotation.KafkaSparuralMapping;
import ru.sparural.kafka.annotation.Payload;
import ru.sparural.kafka.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@KafkaSparuralController(topic = "${sparural.kafka.request-topics.engine}")
@Slf4j
public class UserRequestsController {
    private final UserRequestsService userRequestsService;
    private final UserRequestsSubjectsService userRequestsSubjectsService;
    private final FileDocumentService fileDocumentService;
    private final UserService userService;

    @KafkaSparuralMapping("user-request/create")
    public UserRequestsDto create(@RequestParam Long userId, @Payload UserRequestsDto userRequestsDto) {
        return userRequestsService.save(userId, userRequestsDto);
    }

    @KafkaSparuralMapping("user-request/update")
    public UserRequestsDto update(@RequestParam Long id,
                                  @RequestParam Long userId,
                                  @Payload UserRequestsDto userRequestsDto) {
        return userRequestsService.update(id, userId, userRequestsDto);
    }

    @KafkaSparuralMapping("user-requests/index")
    public List<UserRequestDto> list(@RequestParam Integer offset,
                                     @RequestParam Integer limit,
                                     @RequestParam String search) {
        List<UserRequestsDto> userRequests = userRequestsService.list(offset, limit, search);

        List<UserRequestDto> result = new ArrayList<>();
        for (UserRequestsDto x : userRequests) {
            UserRequestDto userRequestDto = new UserRequestDto();
            userRequestDto.setId(x.getId());
            userRequestDto.setEmail(x.getEmail());
            userRequestDto.setFullName(x.getFullName());
            userRequestDto.setMessage(x.getMessage());
            userRequestDto.setSubject(userRequestsSubjectsService.getWithoutEx(x.getSubjectId()));


            UserProfileDto userProfileDto = userService.getUserProfileInfoWithoutEx(x.getUserId());
            if (userProfileDto != null) {
                ru.sparural.engine.api.dto.request.UserProfileDto user = new ru.sparural.engine.api.dto.request.UserProfileDto();
                user.setBirthday(userProfileDto.getBirthday());
                user.setFirstName(userProfileDto.getFirstName());
                user.setPhoto(userProfileDto.getPhoto());
                user.setLastName(userProfileDto.getLastName());
                user.setPhoneNumber(userProfileDto.getPhoneNumber());
                user.setEmailConfirmed(userProfileDto.getEmailConfirmed());
                user.setEmail(userProfileDto.getEmail());
                user.setGender(userProfileDto.getGender());
                userRequestDto.setUser(user);
            }

            result.add(userRequestDto);
        }
        result.forEach(dto -> {
            List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.USER_REQUEST_ATTACHMENTS, dto.getId());
            if (!files.isEmpty()) {
                dto.setAttachments(files);
            }
        });
        return result;
    }

    @KafkaSparuralMapping("user-requests/get")
    public UserRequestDto get(@RequestParam Long id) {
        UserRequestsDto userRequests = userRequestsService.get(id);

        UserRequestDto result = new UserRequestDto();
        result.setId(userRequests.getId());
        result.setEmail(userRequests.getEmail());
        result.setFullName(userRequests.getFullName());
        result.setMessage(userRequests.getMessage());

        result.setSubject(userRequestsSubjectsService.get(userRequests.getSubjectId()));

        UserProfileDto userProfileDto = userService.getUserProfileInfo(userRequests.getUserId());
        ru.sparural.engine.api.dto.request.UserProfileDto user = new ru.sparural.engine.api.dto.request.UserProfileDto();
        user.setBirthday(userProfileDto.getBirthday());
        user.setFirstName(userProfileDto.getFirstName());
        user.setPhoto(userProfileDto.getPhoto());
        user.setLastName(userProfileDto.getLastName());
        user.setPhoneNumber(userProfileDto.getPhoneNumber());
        user.setEmailConfirmed(userProfileDto.getEmailConfirmed());
        user.setEmail(userProfileDto.getEmail());
        user.setGender(userProfileDto.getGender());
        result.setUser(user);
        List<FileDto> files = fileDocumentService.getFileByDocumentId(FileDocumentTypeField.USER_REQUEST_ATTACHMENTS, result.getId());
        if (!files.isEmpty()) {
            result.setAttachments(files);
        }
        return result;
    }

    @KafkaSparuralMapping("user-requests/delete")
    public Boolean delete(@RequestParam Long id) {
        return userRequestsService.delete(id);
    }
}
