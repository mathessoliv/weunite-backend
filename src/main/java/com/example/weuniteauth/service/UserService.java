package com.example.weuniteauth.service;

import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.user.UpdateUserRequestDTO;
import com.example.weuniteauth.exceptions.auth.ExpiredTokenException;
import com.example.weuniteauth.exceptions.auth.InvalidTokenException;
import com.example.weuniteauth.exceptions.user.UserAlreadyExistsException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.UserMapper;
import com.example.weuniteauth.domain.Role;
import com.example.weuniteauth.domain.User;
import com.example.weuniteauth.repository.RoleRepository;
import com.example.weuniteauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Set;

@Service
public class UserService {

    private static final int TOKEN_EXPIRATION_MINUTES = 10;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User createUser(CreateUserRequestDTO userDTO) {
        boolean userExists = userRepository.existsByUsernameOrEmail(userDTO.username(), userDTO.email());

        if (userExists) {
            throw new UserAlreadyExistsException();
        }

        String encodedPassword = passwordEncoder.encode(userDTO.password());

        User newUser = userMapper.toEntity(userDTO);

        newUser.setPassword(encodedPassword);

        Role roleUser = roleRepository.findByName(Role.Values.BASIC.name());

        newUser.setRoles(Set.of(roleUser));

        newUser = generateAndSetToken(newUser);

        userRepository.save(newUser);

        return newUser;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserDTO deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());

        userRepository.delete(user);

        return userMapper.toDeleteUserDTO("Usuário deletado com sucesso", username);
    }

    @Transactional(readOnly = true)
    public UserDTO getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException());

        return userMapper.toGetUser("Usuário encontrado com sucesso", user.getId().toString(), user.getName(), user.getUsername(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    public UserDTO getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());

        return userMapper.toGetUser("Usuário encontrado com sucesso", user.getId().toString(), user.getName(), user.getUsername(), user.getEmail(), user.getCreatedAt(), user.getUpdatedAt());
    }

    @Transactional(readOnly = true)
    protected User findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());
    }

    @Transactional(readOnly = true)
    protected User findUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException());
    }

    @Transactional(readOnly = true)
    protected User findUserByVerificationToken(String verificationToken) {
        return userRepository.findByVerificationToken(verificationToken)
                .orElseThrow(() -> new InvalidTokenException());
    }

    @Transactional
    protected User verifyUserEmail(User user) {
        Instant now = Instant.now();

        if (user.getVerificationTokenExpires().isBefore(now)) {
            throw new ExpiredTokenException();
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpires(null);

        return userRepository.save(user);
    }

    protected User generateAndSetToken(User user) {
        SecureRandom generator = new SecureRandom();
        int randomNumber = 100000 + generator.nextInt(900000);

        Instant now = Instant.now();
        Instant expirationDate = now.plusSeconds(TOKEN_EXPIRATION_MINUTES * 60);

        user.setVerificationToken(String.valueOf(randomNumber));
        user.setVerificationTokenExpires(expirationDate);

        return user;
    }

    public UserDTO updateUser(UpdateUserRequestDTO requestDTO, String username) {
        User user = findUserEntityByUsername(username);

        if (userRepository.existsByUsername(requestDTO.username())) {
            throw new UserAlreadyExistsException();
        }

        user.setUsername(requestDTO.username());
        user.setName(requestDTO.name());
        user.setBio(requestDTO.bio());

        userRepository.save(user);

        return userMapper.toUpdateUserDTO("Usuário atualizado com sucesso!", user);
    }
}
