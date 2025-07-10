package com.github.gelald.caffeine.service;

import com.github.gelald.caffeine.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserService {
    public Long save(UserDTO userDTO) {
        log.info("save user: {} successfully", userDTO);
        return userDTO.getId();
    }

    public UserDTO getUserById(Long id) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(id);
        userDTO.setUsername("mock213213");
        userDTO.setType(0);
        log.info("get user by mock data successfully, id: {}", id);
        return userDTO;
    }

    public void remove(Long id) {
        log.info("delete user successfully, id: {}", id);
    }
}
