package com.example.csb869_medicinsko_dosie.util;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperUtil {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
