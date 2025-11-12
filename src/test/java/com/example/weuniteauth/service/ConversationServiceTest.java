package com.example.weuniteauth.service;

import com.example.weuniteauth.domain.chat.Conversation;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.chat.ConversationDTO;
import com.example.weuniteauth.dto.chat.CreateConversationRequestDTO;
import com.example.weuniteauth.exceptions.NotFoundResourceException;
import com.example.weuniteauth.mapper.ConversationMapper;
import com.example.weuniteauth.repository.ConversationRepository;
import com.example.weuniteauth.repository.MessageRepository;
import com.example.weuniteauth.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ConversationService Tests")
class ConversationServiceTest {

    @Mock
    private ConversationRepository conversationRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private ConversationMapper conversationMapper;

    @InjectMocks
    private ConversationService conversationService;

    private User initiator;
    private User participant;
    private Conversation conversation;
    private ConversationDTO conversationDTO;

    @BeforeEach
    void setUp() {
        initiator = new User();
        initiator.setId(1L);
        initiator.setUsername("initiator");
        initiator.setEmail("initiator@example.com");

        participant = new User();
        participant.setId(2L);
        participant.setUsername("participant");
        participant.setEmail("participant@example.com");

        conversation = new Conversation();
        conversation.setId(1L);
        Set<User> participants = new HashSet<>();
        participants.add(initiator);
        participants.add(participant);
        conversation.setParticipants(participants);
        conversation.setCreatedAt(Instant.now());
        conversation.setUpdatedAt(Instant.now());

        conversationDTO = new ConversationDTO(
                1L,
                Collections.emptySet(),
                null,
                Instant.now(),
                Instant.now(),
                0
        );
    }

    @Test
    @DisplayName("Should create conversation successfully")
    void shouldCreateConversationSuccessfully() {
        // Given
        CreateConversationRequestDTO request = new CreateConversationRequestDTO(
                1L,
                Set.of(2L)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(initiator));
        when(userRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(conversationRepository.findConversationBetweenTwoUsers(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(conversationMapper.toDTO(any(Conversation.class), anyLong())).thenReturn(conversationDTO);

        // When
        ConversationDTO result = conversationService.createConversation(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);

        ArgumentCaptor<Conversation> conversationCaptor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).save(conversationCaptor.capture());
        Conversation capturedConversation = conversationCaptor.getValue();

        assertThat(capturedConversation.getParticipants()).hasSize(2);
        assertThat(capturedConversation.getParticipants()).contains(initiator, participant);
    }

    @Test
    @DisplayName("Should return existing conversation when it already exists between two users")
    void shouldReturnExistingConversationWhenItAlreadyExists() {
        // Given
        CreateConversationRequestDTO request = new CreateConversationRequestDTO(
                1L,
                Set.of(2L)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(initiator));
        when(userRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(conversationRepository.findConversationBetweenTwoUsers(anyLong(), anyLong()))
                .thenReturn(Optional.of(conversation));
        when(conversationMapper.toDTO(any(Conversation.class), anyLong())).thenReturn(conversationDTO);

        // When
        ConversationDTO result = conversationService.createConversation(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);

        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    @DisplayName("Should throw exception when initiator not found")
    void shouldThrowExceptionWhenInitiatorNotFound() {
        // Given
        CreateConversationRequestDTO request = new CreateConversationRequestDTO(
                1L,
                Set.of(2L)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> conversationService.createConversation(request))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessageContaining("User not found with id: 1");

        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    @DisplayName("Should throw exception when participant not found")
    void shouldThrowExceptionWhenParticipantNotFound() {
        // Given
        CreateConversationRequestDTO request = new CreateConversationRequestDTO(
                1L,
                Set.of(2L)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(initiator));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> conversationService.createConversation(request))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessageContaining("User not found with id: 2");

        verify(conversationRepository, never()).save(any(Conversation.class));
    }

    @Test
    @DisplayName("Should create group conversation with multiple participants")
    void shouldCreateGroupConversationWithMultipleParticipants() {
        // Given
        User participant2 = new User();
        participant2.setId(3L);
        participant2.setUsername("participant2");

        CreateConversationRequestDTO request = new CreateConversationRequestDTO(
                1L,
                Set.of(2L, 3L)
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(initiator));
        when(userRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(userRepository.findById(3L)).thenReturn(Optional.of(participant2));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(conversationMapper.toDTO(any(Conversation.class), anyLong())).thenReturn(conversationDTO);

        // When
        ConversationDTO result = conversationService.createConversation(request);

        // Then
        assertThat(result).isNotNull();

        ArgumentCaptor<Conversation> conversationCaptor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).save(conversationCaptor.capture());
        Conversation capturedConversation = conversationCaptor.getValue();

        assertThat(capturedConversation.getParticipants()).hasSize(3);
        assertThat(capturedConversation.getParticipants()).contains(initiator, participant, participant2);
    }

    @Test
    @DisplayName("Should get user conversations successfully")
    void shouldGetUserConversationsSuccessfully() {
        // Given
        List<Conversation> conversations = Arrays.asList(conversation);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(conversationRepository.findAllByUserId(1L)).thenReturn(conversations);
        when(conversationMapper.toDTO(any(Conversation.class), anyLong())).thenReturn(conversationDTO);

        // When
        List<ConversationDTO> result = conversationService.getUserConversations(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);

        verify(conversationRepository).findAllByUserId(1L);
    }

    @Test
    @DisplayName("Should throw exception when getting conversations for non-existent user")
    void shouldThrowExceptionWhenGettingConversationsForNonExistentUser() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> conversationService.getUserConversations(1L))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessageContaining("User not found with id: 1");

        verify(conversationRepository, never()).findAllByUserId(anyLong());
    }

    @Test
    @DisplayName("Should return empty list when user has no conversations")
    void shouldReturnEmptyListWhenUserHasNoConversations() {
        // Given
        when(userRepository.existsById(1L)).thenReturn(true);
        when(conversationRepository.findAllByUserId(1L)).thenReturn(Collections.emptyList());

        // When
        List<ConversationDTO> result = conversationService.getUserConversations(1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(conversationRepository).findAllByUserId(1L);
    }

    @Test
    @DisplayName("Should get conversation by id successfully")
    void shouldGetConversationByIdSuccessfully() {
        // Given
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));
        when(conversationMapper.toDTO(any(Conversation.class), anyLong())).thenReturn(conversationDTO);

        // When
        ConversationDTO result = conversationService.getConversationById(1L, 1L);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);

        verify(conversationRepository).findById(1L);
        verify(conversationMapper).toDTO(conversation, 1L);
    }

    @Test
    @DisplayName("Should throw exception when conversation not found by id")
    void shouldThrowExceptionWhenConversationNotFoundById() {
        // Given
        when(conversationRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> conversationService.getConversationById(1L, 1L))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessageContaining("Conversation not found with id: 1");

        verify(conversationMapper, never()).toDTO(any(Conversation.class), anyLong());
    }

    @Test
    @DisplayName("Should throw exception when user is not participant of conversation")
    void shouldThrowExceptionWhenUserIsNotParticipantOfConversation() {
        // Given
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(conversation));

        // When & Then
        assertThatThrownBy(() -> conversationService.getConversationById(1L, 3L))
                .isInstanceOf(NotFoundResourceException.class)
                .hasMessageContaining("User is not a participant of this conversation");

        verify(conversationMapper, never()).toDTO(any(Conversation.class), anyLong());
    }

    @Test
    @DisplayName("Should create conversation when initiator creates conversation with themselves only")
    void shouldCreateConversationWhenInitiatorCreatesConversationWithThemselvesOnly() {
        // Given
        CreateConversationRequestDTO request = new CreateConversationRequestDTO(
                1L,
                Collections.emptySet()
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(initiator));
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(conversationMapper.toDTO(any(Conversation.class), anyLong())).thenReturn(conversationDTO);

        // When
        ConversationDTO result = conversationService.createConversation(request);

        // Then
        assertThat(result).isNotNull();

        ArgumentCaptor<Conversation> conversationCaptor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).save(conversationCaptor.capture());
        Conversation capturedConversation = conversationCaptor.getValue();

        assertThat(capturedConversation.getParticipants()).hasSize(1);
        assertThat(capturedConversation.getParticipants()).contains(initiator);
    }

    @Test
    @DisplayName("Should handle duplicate participant ids in create request")
    void shouldHandleDuplicateParticipantIdsInCreateRequest() {
        // Given
        CreateConversationRequestDTO request = new CreateConversationRequestDTO(
                1L,
                Set.of(2L) // Set will handle duplicates automatically
        );

        when(userRepository.findById(1L)).thenReturn(Optional.of(initiator));
        when(userRepository.findById(2L)).thenReturn(Optional.of(participant));
        when(conversationRepository.findConversationBetweenTwoUsers(anyLong(), anyLong()))
                .thenReturn(Optional.empty());
        when(conversationRepository.save(any(Conversation.class))).thenReturn(conversation);
        when(conversationMapper.toDTO(any(Conversation.class), anyLong())).thenReturn(conversationDTO);

        // When
        ConversationDTO result = conversationService.createConversation(request);

        // Then
        assertThat(result).isNotNull();

        ArgumentCaptor<Conversation> conversationCaptor = ArgumentCaptor.forClass(Conversation.class);
        verify(conversationRepository).save(conversationCaptor.capture());
        Conversation capturedConversation = conversationCaptor.getValue();

        // Set should handle duplicates, so only 2 participants
        assertThat(capturedConversation.getParticipants()).hasSize(2);
        assertThat(capturedConversation.getParticipants()).contains(initiator, participant);
    }
}
