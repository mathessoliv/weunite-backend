package com.example.weuniteauth.service;

import com.example.weuniteauth.dto.ResponseDTO;
import com.example.weuniteauth.dto.user.CreateUserRequestDTO;
import com.example.weuniteauth.dto.UserDTO;
import com.example.weuniteauth.dto.user.UpdateUserRequestDTO;
import com.example.weuniteauth.dto.SkillDTO;
import com.example.weuniteauth.exceptions.NotFoundResourceException;
import com.example.weuniteauth.exceptions.auth.ExpiredTokenException;
import com.example.weuniteauth.exceptions.auth.InvalidTokenException;
import com.example.weuniteauth.exceptions.user.UserAlreadyExistsException;
import com.example.weuniteauth.exceptions.user.UserNotFoundException;
import com.example.weuniteauth.mapper.UserMapper;
import com.example.weuniteauth.domain.opportunity.Skill;
import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.repository.RoleRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import com.example.weuniteauth.repository.SkillRepository;
import com.example.weuniteauth.repository.user.AthleteRepository;
import org.hibernate.Hibernate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.example.weuniteauth.service.cloudinary.CloudinaryService;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final int TOKEN_EXPIRATION_MINUTES = 10;

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final CloudinaryService cloudinaryService;
    private final SkillRepository skillRepository;
    private final AthleteRepository athleteRepository;

    public UserService(
            UserRepository userRepository,
            UserMapper userMapper,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            CloudinaryService cloudinaryService,
            SkillRepository skillRepository,
            AthleteRepository athleteRepository
    ) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.cloudinaryService = cloudinaryService;
        this.skillRepository = skillRepository;
        this.athleteRepository = athleteRepository;
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

        Role roleUser = roleRepository.findByName(userDTO.role().toUpperCase());
        if (roleUser == null) {
            throw new NotFoundResourceException("Role not found: " + userDTO.role());
        }

        newUser.setRole(Set.of(roleUser));
        newUser = generateAndSetToken(newUser);
        userRepository.save(newUser);

        return newUser;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ResponseDTO<UserDTO> deleteUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());

        ResponseDTO<UserDTO> response = userMapper.toResponseDTO("Usuário deletado com sucesso", user);
        user.getRole().clear();

        userRepository.delete(user);

        return response;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public ResponseDTO<UserDTO> deleteBanner(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException());

        user.setBannerImg(null);

        userRepository.save(user);

        return userMapper.toResponseDTO("Banner deletado com sucesso", user);
    }

    @Transactional(readOnly = true)
    public ResponseDTO<UserDTO> getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        return userMapper.toResponseDTO("Usuário encontrado com sucesso", user);
    }

    @Transactional(readOnly = true)
    public ResponseDTO<UserDTO> getUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        return userMapper.toResponseDTO("Usuário encontrado com sucesso", user);
    }

    @Transactional
    public ResponseDTO<UserDTO> updateUser(UpdateUserRequestDTO requestDTO, String username, MultipartFile profileImage, MultipartFile bannerImage) {
        // Tenta encontrar como Atleta primeiro para garantir a instância correta desde o início
        Athlete athlete = athleteRepository.findByUsername(username).orElse(null);

        if (athlete != null) {
            // === ATUALIZAÇÃO DE ATLETA ===
            // Validação de username único (se mudou)
            if (userRepository.existsByUsername(requestDTO.username()) && !athlete.getUsername().equals(requestDTO.username())) {
                throw new UserAlreadyExistsException();
            }

            athlete.setUsername(requestDTO.username());
            athlete.setName(requestDTO.name());
            athlete.setBio(requestDTO.bio());

            if (requestDTO.isPrivate() != null) {
                athlete.setPrivate(requestDTO.isPrivate());
            }

            if (profileImage != null && !profileImage.isEmpty()) {
                String imageUrl = cloudinaryService.uploadProfileImg(profileImage, username);
                athlete.setProfileImg(imageUrl);
            }

            if (bannerImage != null && !bannerImage.isEmpty()) {
                String bannerUrl = cloudinaryService.uploadBannerImg(bannerImage, username);
                athlete.setBannerImg(bannerUrl);
            }

            // Atualiza skills se fornecidas
            if (requestDTO.skills() != null) {
                updateAthleteSkills(athlete, requestDTO.skills());
            }

            athlete = athleteRepository.saveAndFlush(athlete);
            return userMapper.toResponseDTO("Perfil de atleta atualizado com sucesso!", athlete);

        } else {
            // === ATUALIZAÇÃO DE USUÁRIO COMUM/EMPRESA ===
            User user = findUserEntityByUsername(username);

            if (userRepository.existsByUsername(requestDTO.username()) && !user.getUsername().equals(requestDTO.username())) {
                throw new UserAlreadyExistsException();
            }

            user.setUsername(requestDTO.username());
            user.setName(requestDTO.name());
            user.setBio(requestDTO.bio());

            if (requestDTO.isPrivate() != null) {
                user.setPrivate(requestDTO.isPrivate());
            }

            if (profileImage != null && !profileImage.isEmpty()) {
                String imageUrl = cloudinaryService.uploadProfileImg(profileImage, username);
                user.setProfileImg(imageUrl);
            }

            if (bannerImage != null && !bannerImage.isEmpty()) {
                String bannerUrl = cloudinaryService.uploadBannerImg(bannerImage, username);
                user.setBannerImg(bannerUrl);
            }

            user = userRepository.save(user);
            return userMapper.toResponseDTO("Perfil atualizado com sucesso!", user);
        }
    }

    private void updateAthleteSkills(Athlete athlete, List<SkillDTO> skillDTOs) {
        // Limpa as skills antigas
        if (athlete.getSkills() != null) {
            athlete.getSkills().clear();
        } else {
            athlete.setSkills(new HashSet<>());
        }
        
        // Adiciona as novas skills
        for (SkillDTO skillDTO : skillDTOs) {
            String skillName = skillDTO.name();
            Skill skill = skillRepository.findByName(skillName);
            if (skill == null) {
                skill = new Skill(skillName);
                // Salva a nova skill antes de associar
                skill = skillRepository.save(skill);
            }
            athlete.getSkills().add(skill);
        }
    }

    @Transactional(readOnly = true)
    protected User findUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    protected User findUserEntityById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    protected User findUserEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Transactional(readOnly = true)
    protected User findUserByVerificationToken(String verificationToken) {
        return userRepository.findByVerificationToken(verificationToken)
                .orElseThrow(InvalidTokenException::new);
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

    @Transactional(readOnly = true)
    public ResponseDTO<List<UserDTO>> searchUsers(String query) {

        List<User> users = userRepository.searchUsers(
                query.trim()
        );

        return userMapper.toSearchResponseDTO("Usuários encontrados com sucesso", users);
    }

}
