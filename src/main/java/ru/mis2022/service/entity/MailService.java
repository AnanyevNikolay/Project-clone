package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Invite;
import ru.mis2022.models.entity.User;

public interface MailService {

    void sendRegistrationInviteByEmail(Invite invite, User user);
}
