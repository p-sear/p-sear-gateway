package com.pser.gateway.domain;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.lang.NonNull;

@ReadingConverter
public class RoleReadingConverter implements Converter<Integer, RoleEnum> {
    @Override
    public RoleEnum convert(@NonNull Integer source) {
        return RoleEnum.getByValue(source);
    }
}
