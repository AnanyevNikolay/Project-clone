package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Invite;
import ru.mis2022.models.entity.User;
import ru.mis2022.repositories.InviteRepository;
import ru.mis2022.service.entity.InviteService;
import ru.mis2022.utils.GenerateRandomString;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InviteServiceImpl implements InviteService {
    private final InviteRepository inviteRepository;
    private final GenerateRandomString generator;
    @Value("15")
    private int randomPasswordLength;
    @Value("${mis.property.Invite.expirationPeriod}")
    private Integer expirationPeriod;
    @Override
    @Transactional
    public Invite save(Invite invite) {
        return inviteRepository.save(invite);
    }

    @Override
    public Invite findByToken(String token) {
        return inviteRepository.findByToken(token);
    }

    @Override
    public void delete(Invite invite) {
        inviteRepository.delete(invite);
    }

    @Override
    public void deleteAll() {
        inviteRepository.deleteAll();
    }

    @Override
    @Transactional
    public Invite persist(User user) {
        String tmpPwd = generator.getRndStr(randomPasswordLength);
        Invite invite = new Invite(tmpPwd, LocalDateTime.now().plusHours(expirationPeriod), user);
        inviteRepository.save(invite);
        return invite;
    }
}
