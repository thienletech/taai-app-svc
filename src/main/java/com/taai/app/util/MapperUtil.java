package com.taai.app.util;

import org.modelmapper.ModelMapper;

import java.lang.reflect.Type;

public class MapperUtil {
    private static final ModelMapper modelMapper = new ModelMapper();

    private MapperUtil() {
    }

    public static <T> T map(Object source, Class<T> destinationType) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, destinationType);
    }

    @SuppressWarnings("unused")
    public <T> T map(Object source, Type destinationType) {
        if (source == null) {
            return null;
        }
        return modelMapper.map(source, destinationType);
    }
}
