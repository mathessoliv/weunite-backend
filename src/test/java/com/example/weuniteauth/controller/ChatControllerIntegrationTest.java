package com.example.weuniteauth.controller;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class ChatControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @AfterEach
    void cleanUploads() throws IOException {
        Path uploads = Path.of("uploads");
        if (Files.exists(uploads)) {
            try (var paths = Files.list(uploads)) {
                paths.forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException ignored) {}
                });
            }
            Files.deleteIfExists(uploads);
        }
    }

    @Test
    void uploadFileShouldReturnMetadata() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "hello".getBytes());

        mockMvc.perform(multipart("/api/messages/upload")
                        .file(file)
                        .param("conversationId", "1")
                        .param("senderId", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileType").value("FILE"));
    }
}

