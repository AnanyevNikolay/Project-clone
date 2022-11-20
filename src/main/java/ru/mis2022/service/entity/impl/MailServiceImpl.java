package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.mis2022.models.entity.Invite;
import ru.mis2022.models.entity.User;
import ru.mis2022.service.entity.MailService;

import java.time.LocalDateTime;

import static ru.mis2022.utils.DateFormatter.DATE_TIME_FORMATTER;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${testsystem.server.address}")
    private String serverAddress;


    @Override
    public void sendRegistrationInviteByEmail(Invite invite, User user) {
        send(user.getEmail(), "VL mis2222 confirm email n pwd"
                        + LocalDateTime.now().format(DATE_TIME_FORMATTER),
                String.format("confirm email and write new password here (in newPassword parameter in url) follow the link:\n\n" +
                                "%s/api/auth/confirm/emailpassword?token=%s",
                        serverAddress, invite.getToken()));
    }

    private void send(String mailTo, String subject, String message) {
        SimpleMailMessage mailMsg = new SimpleMailMessage();
        mailMsg.setFrom(username);
        mailMsg.setTo(mailTo);
        mailMsg.setSubject(subject);
        mailMsg.setText(message);
        mailMsg.setFrom(
                LocalDateTime.now().format(DATE_TIME_FORMATTER).replaceAll("[ , ., :]", "_") + " VL <" + username + ">"
        );
        mailSender.send(mailMsg);
    }
}
