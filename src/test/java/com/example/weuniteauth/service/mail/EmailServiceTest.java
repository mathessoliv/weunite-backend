package com.example.weuniteauth.service.mail;

import com.example.weuniteauth.exceptions.mail.EmailSendingException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private MimeMessage mimeMessage;

    @InjectMocks
    private EmailService emailService;

    private void setFromEmail(String value) throws Exception {
        Field field = EmailService.class.getDeclaredField("fromEmail");
        field.setAccessible(true);
        field.set(emailService, value);
    }

    @Test
    @DisplayName("sendEmail deve enviar email com template e variaveis preenchidas")
    void sendEmail_sucesso() throws Exception {
        setFromEmail("from@example.com");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", "User");

        assertDoesNotThrow(() -> emailService.sendEmail("to@example.com", "Assunto", "welcome.html", vars));

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(any(MimeMessage.class));
    }

    @Test
    @DisplayName("sendEmail deve lancar EmailSendingException quando ocorrer MessagingException")
    void sendEmail_deveLancarEmailSendingException() throws Exception {
        setFromEmail("from@example.com");
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doAnswer(invocation -> { throw new MessagingException("erro"); })
                .when(mailSender).send(any(MimeMessage.class));

        Map<String, Object> vars = new HashMap<>();

        assertThrows(EmailSendingException.class,
                () -> emailService.sendEmail("to@example.com", "Assunto", "welcome.html", vars));
    }

    @Test
    @DisplayName("sendVerificationEmailAsync deve delegar para sendEmail com template correto")
    void sendVerificationEmailAsync_deveDelegarParaSendEmail() throws Exception {
        setFromEmail("from@example.com");
        EmailService spyService = spy(emailService);

        doNothing().when(spyService)
                .sendEmail(eq("to@example.com"), eq("Verificação de email"), eq("verification.html"), anyMap());

        spyService.sendVerificationEmailAsync("to@example.com", "token123");

        verify(spyService).sendEmail(eq("to@example.com"), eq("Verificação de email"), eq("verification.html"), anyMap());
    }

    @Test
    @DisplayName("sendVerificationEmailClub deve delegar para sendEmail com template de clube")
    void sendVerificationEmailClub_deveDelegarParaSendEmail() throws Exception {
        setFromEmail("from@example.com");
        EmailService spyService = spy(emailService);

        doNothing().when(spyService)
                .sendEmail(eq("to@example.com"), eq("Solicitação de cadastro WeUnite"), eq("verificationClub.html"), anyMap());

        spyService.sendVerificationEmailClub("to@example.com", "123", "Nome", "user", "email@example.com");

        verify(spyService).sendEmail(eq("to@example.com"), eq("Solicitação de cadastro WeUnite"), eq("verificationClub.html"), anyMap());
    }

    @Test
    @DisplayName("sendPasswordResetRequestEmail deve delegar para sendEmail com template de reset request")
    void sendPasswordResetRequestEmail_deveDelegarParaSendEmail() throws Exception {
        setFromEmail("from@example.com");
        EmailService spyService = spy(emailService);

        doNothing().when(spyService)
                .sendEmail(eq("to@example.com"), eq("Redefinição de senha"), eq("passwordResetRequest.html"), anyMap());

        spyService.sendPasswordResetRequestEmail("to@example.com", "token");

        verify(spyService).sendEmail(eq("to@example.com"), eq("Redefinição de senha"), eq("passwordResetRequest.html"), anyMap());
    }

    @Test
    @DisplayName("sendPasswordResetSuccessEmail deve delegar para sendEmail com template de sucesso")
    void sendPasswordResetSuccessEmail_deveDelegarParaSendEmail() throws Exception {
        setFromEmail("from@example.com");
        EmailService spyService = spy(emailService);

        doNothing().when(spyService)
                .sendEmail(eq("to@example.com"), eq("Senha redefinida com sucesso!"), eq("passwordResetSuccess.html"), anyMap());

        spyService.sendPasswordResetSuccessEmail("to@example.com");

        verify(spyService).sendEmail(eq("to@example.com"), eq("Senha redefinida com sucesso!"), eq("passwordResetSuccess.html"), anyMap());
    }

    @Test
    @DisplayName("sendWelcomeEmail deve delegar para sendEmail com template de boas-vindas")
    void sendWelcomeEmail_deveDelegarParaSendEmail() throws Exception {
        setFromEmail("from@example.com");
        EmailService spyService = spy(emailService);

        doNothing().when(spyService)
                .sendEmail(eq("to@example.com"), eq("Bem-vindo a WeUnite!"), eq("welcome.html"), anyMap());

        spyService.sendWelcomeEmail("to@example.com", "User");

        verify(spyService).sendEmail(eq("to@example.com"), eq("Bem-vindo a WeUnite!"), eq("welcome.html"), anyMap());
    }
}
