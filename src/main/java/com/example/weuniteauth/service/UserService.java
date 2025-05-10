package com.example.weuniteauth.service;

import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.dto.user.UserResponseDTO;
import com.example.weuniteauth.exceptions.auth.ExpiredTokenException;
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
import java.util.Date;
import java.util.Set;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    private static final int TOKEN_EXPIRATION_MINUTES = 10;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    @Transactional
    public User createUser(CreateUserRequestDTO userDTO) {
        boolean userExists = userRepository.existsByUsernameOrEmail(userDTO.username(), userDTO.email());

        if (userExists) {
            throw new UserAlreadyExistsException("Usuário já existente");
        }

        String encodedPassword = passwordEncoder.encode(userDTO.password());

        User newUser = userMapper.toEntity(userDTO);

        newUser.setPassword(encodedPassword);

        Role roleUser = roleRepository.findByName(Role.Values.BASIC.name());

        if (roleUser == null) {
            roleUser = new Role();
            roleUser.setName(Role.Values.BASIC.name());
            roleUser = roleRepository.save(roleUser);
        }

        newUser.setRoles(Set.of(roleUser));

        newUser = generateAndSetToken(newUser);

        userRepository.save(newUser);

        return newUser;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public UserResponseDTO deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User", username));

        userRepository.delete(user);

        return userMapper.toUserResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User", id.toString()));

        return userMapper.toUserResponseDto(user);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User", username));

        return userMapper.toUserResponseDto(user);
    }

    @Transactional(readOnly = true)
    public User findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User", username));
    }

    @Transactional(readOnly = true)
    public User findUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Email", email));
    }

    @Transactional(readOnly = true)
    public User findUserByVerificationToken(String verificationToken) {
        return userRepository.findByVerificationToken(verificationToken)
                .orElseThrow(() -> new UserNotFoundException("Código", verificationToken));
    }

    @Transactional
    public User verifyUserEmail(User user) {
        Date now = new Date();

        if (user.getVerificationTokenExpires().before(now)) {
            throw new ExpiredTokenException("Código expirado");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpires(null);

        return userRepository.save(user);
    }

    protected User generateAndSetToken(User user) {
        SecureRandom generator = new SecureRandom();
        int randomNumber = 100000 + generator.nextInt(900000);

        Date now = new Date();
        Date expirationDate = new Date(now.getTime() + TOKEN_EXPIRATION_MINUTES * 60 * 1000);

        user.setVerificationToken(String.valueOf(randomNumber));
        user.setVerificationTokenExpires(expirationDate);

        return user;
    }


}
