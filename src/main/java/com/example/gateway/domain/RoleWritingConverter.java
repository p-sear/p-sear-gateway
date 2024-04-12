package com.example.gateway.domain;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

@WritingConverter
public class RoleWritingConverter implements Converter<RoleEnum, Integer> {
    @Override
    public Integer convert(@NonNull RoleEnum source) {
        return source.getValue();
    }
}
