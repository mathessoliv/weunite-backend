package com.example.weuniteauth.config;

import com.example.weuniteauth.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DatabaseMigration implements CommandLineRunner {

    private final MessageRepository messageRepository;

    @Override
    @Transactional
    public void run(String... args) {
        messageRepository.updateNullIsReadToFalse();
        messageRepository.updateNullDeletedToFalse();
        messageRepository.updateNullEditedToFalse();
        System.out.println("✅ Database migration completed: null values fixed");
    }
}