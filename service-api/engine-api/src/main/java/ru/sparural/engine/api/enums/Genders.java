package ru.sparural.engine.api.enums;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Genders {
    MALE("male"),
    FEMALE("female"),
    OTHER("other");

    static final Map<String, Genders> genderMap;

    static {
        genderMap = Stream.of(Genders.values())
                .collect(Collectors.toMap(x -> x.gender, x -> x));
    }

    final String gender;

    Genders(String gender) {
        this.gender = gender;
    }

    public static Genders of(String genderName) {
        return genderMap.get(genderName);
    }

    @JsonValue
    public String getGender() {
        return gender;
    }
}
