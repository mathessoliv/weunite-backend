package com.example.weuniteauth.service;

import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.dto.user.UserResponseDTO;
import com.example.weuniteauth.exceptions.user.UserAlreadyExistsException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.UserMapper;
import com.example.weuniteauth.model.User;
import com.example.weuniteauth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponseDTO createUser(CreateUserRequestDTO userDTO) {

        if (userRepository.existsByUsername(userDTO.username())) {
            throw new UserAlreadyExistsException(userDTO.username());
        }

        if (userRepository.existsByEmail(userDTO.email())) {
            throw new UserAlreadyExistsException(userDTO.email());
        }

        String encodedPassword = passwordEncoder.encode(userDTO.password());

        User newUser = userMapper.toEntity(userDTO);
        newUser.setPassword(encodedPassword);
        userRepository.save(newUser);

        return userMapper.toUserResponseDto(newUser);
    }

    @Transactional
    public UserResponseDTO deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User", username));

        userRepository.delete(user);

        return userMapper.toUserResponseDto(user);
    }

    @Transactional
    public UserResponseDTO getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User", id.toString()));

        return userMapper.toUserResponseDto(user);
    }

    @Transactional
    public UserResponseDTO getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User", username));

        return userMapper.toUserResponseDto(user);
    }

    @Transactional
    public User findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User", username));
    }
}
