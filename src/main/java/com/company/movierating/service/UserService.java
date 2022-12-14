package com.company.movierating.service;

import com.company.movierating.service.dto.UserDto;

public interface UserService extends AbstractService<Long, UserDto> {
    UserDto getByEmail(String email);

    UserDto getByLogin(String login);

    UserDto signIn(String login, String password);

    UserDto create(UserDto entity, String confirmedPassword);

}
