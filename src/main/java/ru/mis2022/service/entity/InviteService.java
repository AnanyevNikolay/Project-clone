package ru.mis2022.service.entity;

import ru.mis2022.models.entity.Invite;
import ru.mis2022.models.entity.User;

public interface InviteService {
    Invite save(Invite invite);
    Invite findByToken(String token);
    void delete(Invite invite);
    void deleteAll();
    Invite persist(User user);
}
