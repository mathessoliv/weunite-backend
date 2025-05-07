package com.example.weuniteauth.service;

import com.example.weuniteauth.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
    public UserResponseDTO updateUser(UpdateUserRequestDTO userDTO, String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User", username));

        if (!user.getUsername().equals(userDTO.username()) &&
                userRepository.existsByUsername(userDTO.username())) {
            throw new UserAlreadyExistsException(userDTO.username());
        }

        userMapper.updateUserFromDto(userDTO, user);

        userRepository.save(user);

        return userMapper.toUserResponseDto(user);
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
    public List<UserResponseDTO> searchUsersByUsername(String username) {
        List<User> users = userRepository.findByUsernameContaining(username);

        return users.stream()
                .map(userMapper::toUserResponseDto)
                .toList();
    }

    @Transactional
    public User findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User", username));
    }
}
