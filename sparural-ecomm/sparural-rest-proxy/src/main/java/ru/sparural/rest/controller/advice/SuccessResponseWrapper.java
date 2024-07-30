package ru.sparural.rest.controller.advice;

import java.lang.reflect.Method;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import ru.sparural.rest.controller.advice.annotations.ControllerResponseType;
import ru.sparural.rest.dto.DataResponse;
import ru.sparural.rest.dto.UnwrappedGenericDto;
import ru.sparural.rest.controller.advice.annotations.ResponseType;

@ControllerAdvice
@Slf4j
public class SuccessResponseWrapper implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !returnType.getContainingClass().isAnnotationPresent(ControllerAdvice.class);
    }

    @Override
    public Object beforeBodyWrite(
            Object body,
            MethodParameter returnType,
            MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request,
            ServerHttpResponse response) {

        if (request.getURI().getPath().startsWith("/swagger")) {
            return body;
        }

        if (request.getURI().getPath().endsWith("/api-docs")) {
            return body;
        }

        if (body instanceof DataResponse) {
            return body;
        }

        if (body instanceof ResponseEntity) {
            return body;
        }

        if (body instanceof UnwrappedGenericDto) {
            return body;
        }

        var responseType = getResponseType(returnType);
        switch (responseType) {
            case RAW:
                return body;
            case UNWRAPPED:
                return UnwrappedGenericDto.builder()
                        .data(body)
                        .success(Boolean.TRUE)
                        .build();
            case WRAPPED:
            default:
                return new DataResponse(body);
        }
    }

    private ControllerResponseType getResponseType(MethodParameter returnType) {
        Method method = returnType.getMethod();
        if (method.isAnnotationPresent(ResponseType.class)) {
            ResponseType responseType = method.getAnnotation(ResponseType.class);
            return responseType.value();
        }

        if (method.getDeclaringClass().isAnnotationPresent(ResponseType.class)) {
            ResponseType responseType = method.getDeclaringClass().getAnnotation(ResponseType.class);
            return responseType.value();
        }

        return ControllerResponseType.WRAPPED;
    }

}
