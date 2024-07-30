package ru.sparural.triggers.utils;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Vorobyev Vyacheslav
 */
@Component
@Scope("prototype")
@RequiredArgsConstructor
public class DtoMapperUtils {
    private final ModelMapper modelMapper;

    public <T, X> X convert(T object, Class<X> toType) {
        return modelMapper.map(object, toType);
    }

    public <T, X> X convert(Class<X> toType, Supplier<T> supplier) {
        var response = supplier.get();
        return modelMapper.map(response, toType);
    }

    public <T, B, X> T convert(B object, Class<X> toType, Function<X, T> function) {
        var data = modelMapper.map(object, toType);
        return function.apply(data);
    }

    public <T, B, X> List<T> convert(List<B> object, Class<X> toType, Function<X, T> function) {
        return object.stream()
                .map(x -> function.apply(modelMapper.map(x, toType)))
                .collect(Collectors.toList());
    }

    public <T, X> List<X> convertList(Class<X> toType, List<T> objects) {
        return objects.stream()
                .map(x -> modelMapper.map(x, toType)).collect(Collectors.toList());
    }

    public <T, X> List<X> convertList(Class<X> toType, Supplier<List<T>> supplier) {
        var response = supplier.get();
        return response.stream()
                .map(x -> modelMapper.map(x, toType))
                .collect(Collectors.toList());
    }

    public <T, B, X> List<T> convertToList(B object, Class<X> toType, Function<X, List<T>> function) {
        var data = modelMapper.map(object, toType);
        return function.apply(data);
    }

    public <T, B, X, R> R convert(B object, Class<X> toType,
                                  Function<X, T> function, Class<R> returnType) {
        var data = convert(object, toType, function);
        return modelMapper.map(data, returnType);
    }

    public <T, B, X> List<T> convertList(B object, Class<X> toType,
                                         Function<X, List<T>> function, Class<T> entityDtoRepresentation) {
        var data = convert(object, toType, function);
        return data.stream()
                .map(x -> modelMapper.map(x, entityDtoRepresentation))
                .collect(Collectors.toList());
    }

}