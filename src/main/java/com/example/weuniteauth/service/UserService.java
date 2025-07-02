package com.example.weuniteauth.service;

import com.example.weuniteauth.dto.ResponseDTO;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.weuniteauth.service.cloudinary.CloudinaryService;

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
    private final CloudinaryService cloudinaryService;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            CloudinaryService cloudinaryService
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.cloudinaryService = cloudinaryService;
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
    public ResponseDTO<UserDTO> deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());

        userRepository.delete(user);

        return userMapper.toResponseDTO("Usuário deletado com sucesso", user);
    }

    @Transactional(readOnly = true)
    public ResponseDTO<UserDTO> getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException());

        return userMapper.toResponseDTO("Usuário encontrado com sucesso", user);
    }

    @Transactional(readOnly = true)
    public ResponseDTO<UserDTO> getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());

        return userMapper.toResponseDTO("Usuário encontrado com sucesso", user);
    }

    public ResponseDTO<UserDTO> updateUser(UpdateUserRequestDTO requestDTO, String username, MultipartFile image) {
        User user = findUserEntityByUsername(username);

        if (userRepository.existsByUsername(requestDTO.username())) {
            throw new UserAlreadyExistsException();
        }

        user.setUsername(requestDTO.username());
        user.setName(requestDTO.name());
        user.setBio(requestDTO.bio());

        String imageUrl = null;

        if (image != null && !image.isEmpty()) {
            imageUrl = cloudinaryService.uploadProfileImg(image, username);
            user.setProfileImg(imageUrl);
        }

        userRepository.save(user);

        return userMapper.toResponseDTO("Usuário atualizado com sucesso!", user);
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
}
