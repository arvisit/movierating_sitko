package com.company.movierating.service.converter.impl;

import com.company.movierating.AppConstants;
import com.company.movierating.dao.ScoreDao;
import com.company.movierating.dao.entity.Film;
import com.company.movierating.service.converter.Converter;
import com.company.movierating.service.dto.FilmDto;

public class FilmConverter implements Converter<Film, FilmDto> {
    private final ScoreDao scoreDao;

    public FilmConverter(ScoreDao scoreDao) {
        this.scoreDao = scoreDao;
    }

    @Override
    public FilmDto toDto(Film entity) {
        FilmDto dto = new FilmDto();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setReleaseYear(entity.getReleaseYear());
        dto.setLength(entity.getLength());
        dto.setAgeRating(FilmDto.AgeRating.valueOf(entity.getAgeRating().toString()));
        dto.setAverageScore(scoreDao.countFilmAverageScore(entity.getId()));
        dto.setPoster(posterToDto(entity.getPoster()));
        return dto;
    }

    @Override
    public Film toEntity(FilmDto dto) {
        Film entity = new Film();
        entity.setId(dto.getId());
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setReleaseYear(dto.getReleaseYear());
        entity.setLength(dto.getLength());
        entity.setAgeRating(Film.AgeRating.valueOf(dto.getAgeRating().toString()));
        entity.setPoster(posterToEntity(dto.getPoster()));
        return entity;
    }

    private String posterToDto(String img) {
        if (img == null) {
            return AppConstants.DEFAULT_APP_POSTER;
        }
        return AppConstants.IMAGE_APP_POSTER + "/" + img;
    }

    private String posterToEntity(String img) {
        if (AppConstants.DEFAULT_APP_POSTER.equals(img)) {
            return null;
        }
        return img.substring(img.lastIndexOf('/') + 1);
    }

}
