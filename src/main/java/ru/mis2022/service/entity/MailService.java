package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Invite;
import ru.mis2022.models.entity.User;

public interface MailService {
    void send(String mailTo, String subject, String message);

    void sendRegistrationInviteByEmail(Invite invite, User user);
}
