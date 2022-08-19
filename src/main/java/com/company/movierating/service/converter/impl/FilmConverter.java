package com.company.movierating.service.converter.impl;

import com.company.movierating.dao.entity.Film;
import com.company.movierating.service.converter.Converter;
import com.company.movierating.service.dto.FilmDto;

public class FilmConverter implements Converter<Film, FilmDto> {

    @Override
    public FilmDto toDto(Film entity) {
        FilmDto dto = new FilmDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setReleaseYear(entity.getReleaseYear());
        dto.setLength(entity.getLength());
        dto.setAgeRating(FilmDto.AgeRating.valueOf(entity.getAgeRating().toString()));
        return dto;
    }

    @Override
    public Film toEntity(FilmDto dto) {
        Film entity = new Film();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setReleaseYear(entity.getReleaseYear());
        entity.setLength(dto.getLength());
        entity.setAgeRating(Film.AgeRating.valueOf(dto.getAgeRating().toString()));
        return entity;
    }

}