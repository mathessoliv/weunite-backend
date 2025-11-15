package com.example.weuniteauth.mapper;

import com.example.weuniteauth.domain.post.Comment;
import com.example.weuniteauth.domain.post.Post;
import com.example.weuniteauth.domain.users.Athlete;
import com.example.weuniteauth.domain.users.Role;
import com.example.weuniteauth.domain.users.User;
import com.example.weuniteauth.dto.CommentDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DisplayName("CommentMapper Tests")
class CommentMapperTest {

    @Autowired
    private CommentMapper commentMapper;

    private Comment testComment;
    private Post testPost;
    private User testUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName("ATHLETE");

        testUser = new Athlete();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");

        Set<Role> roles = new HashSet<>();
        roles.add(role);
        testUser.setRole(roles);
        testUser.setCreatedAt(Instant.now());

        testPost = new Post(testUser, "Test post", null);
        testPost.setId(1L);
        testPost.setCreatedAt(Instant.now());

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setText("Test comment");
        testComment.setUser(testUser);
        testComment.setPost(testPost);
        testComment.setCreatedAt(Instant.now());
        testComment.setUpdatedAt(Instant.now());
    }

    // TO COMMENT DTO TESTS

    @Test
    @DisplayName("Should convert Comment entity to CommentDTO")
    void toCommentDTO() {
        CommentDTO result = commentMapper.toCommentDTO(testComment);

        assertNotNull(result);
        assertEquals("1", result.id());
        assertEquals("Test comment", result.text());
        assertNull(result.imageUrl());
        assertNotNull(result.user());
        assertEquals("testuser", result.user().username());
        assertNotNull(result.post());
        assertEquals("1", result.post().id());
        assertNotNull(result.createdAt());
    }

    @Test
    @DisplayName("Should convert comment with image URL")
    void toCommentDTOWithImage() {
        testComment.setImageUrl("http://image.url/comment.jpg");

        CommentDTO result = commentMapper.toCommentDTO(testComment);

        assertNotNull(result);
        assertEquals("http://image.url/comment.jpg", result.imageUrl());
    }

    @Test
    @DisplayName("Should convert comment with parent comment")
    void toCommentDTOWithParentComment() {
        Comment parentComment = new Comment();
        parentComment.setId(2L);
        parentComment.setText("Parent comment");
        parentComment.setUser(testUser);
        parentComment.setPost(testPost);
        parentComment.setCreatedAt(Instant.now());

        testComment.setParentComment(parentComment);

        CommentDTO result = commentMapper.toCommentDTO(testComment);

        assertNotNull(result);
        assertNotNull(result.parentComment());
        assertEquals("2", result.parentComment().id());
        assertEquals("Parent comment", result.parentComment().text());
        assertNull(result.parentComment().parentComment()); // Avoid circular reference
    }

    @Test
    @DisplayName("Should convert comment with replies")
    void toCommentDTOWithReplies() {
        Comment reply1 = new Comment();
        reply1.setId(2L);
        reply1.setText("Reply 1");
        reply1.setUser(testUser);
        reply1.setPost(testPost);
        reply1.setParentComment(testComment);
        reply1.setCreatedAt(Instant.now());

        Comment reply2 = new Comment();
        reply2.setId(3L);
        reply2.setText("Reply 2");
        reply2.setUser(testUser);
        reply2.setPost(testPost);
        reply2.setParentComment(testComment);
        reply2.setCreatedAt(Instant.now());

        List<Comment> replies = new ArrayList<>();
        replies.add(reply1);
        replies.add(reply2);
        testComment.setComments(replies);

        CommentDTO result = commentMapper.toCommentDTO(testComment);

        assertNotNull(result);
        assertNotNull(result.comments());
        assertEquals(2, result.comments().size());
        assertEquals("Reply 1", result.comments().get(0).text());
        assertEquals("Reply 2", result.comments().get(1).text());
    }

    @Test
    @DisplayName("Should handle null optional fields")
    void toCommentDTONullFields() {
        testComment.setImageUrl(null);
        testComment.setParentComment(null);
        testComment.setComments(null);
        testComment.setUpdatedAt(null);

        CommentDTO result = commentMapper.toCommentDTO(testComment);

        assertNotNull(result);
        assertNull(result.imageUrl());
        assertNull(result.parentComment());
        assertNotNull(result.comments());
        assertTrue(result.comments().isEmpty());
        assertNull(result.updatedAt());
    }

    // MAP POST WITHOUT LIKES TESTS

    @Test
    @DisplayName("Should map post without likes and comments")
    void mapPostWithoutLikesFromComment() {
        CommentDTO result = commentMapper.toCommentDTO(testComment);

        assertNotNull(result);
        assertNotNull(result.post());
        assertEquals("Test post", result.post().text());
        // Post deve vir sem likes e comments para evitar referência circular (ignored no mapper)
        // likes e comments são null porque foram ignorados no @Mapping
    }

    // MAP COMMENTS TO LIST TESTS

    @Test
    @DisplayName("Should convert list of comments to list of DTOs")
    void mapCommentsToList() {
        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setText("Second comment");
        comment2.setUser(testUser);
        comment2.setPost(testPost);
        comment2.setCreatedAt(Instant.now());

        List<Comment> comments = new ArrayList<>();
        comments.add(testComment);
        comments.add(comment2);

        List<CommentDTO> result = commentMapper.mapCommentsToList(comments);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Test comment", result.get(0).text());
        assertEquals("Second comment", result.get(1).text());
    }

    @Test
    @DisplayName("Should handle empty comment list")
    void mapCommentsToListEmpty() {
        List<Comment> comments = new ArrayList<>();

        List<CommentDTO> result = commentMapper.mapCommentsToList(comments);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle null comment list")
    void mapCommentsToListNull() {
        List<CommentDTO> result = commentMapper.mapCommentsToList(null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}

