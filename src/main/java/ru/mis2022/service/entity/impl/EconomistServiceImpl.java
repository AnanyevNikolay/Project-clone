package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Economist;
import ru.mis2022.repositories.EconomistRepository;
import ru.mis2022.service.entity.EconomistService;
import ru.mis2022.service.entity.InviteService;
import ru.mis2022.service.entity.MailService;
import ru.mis2022.utils.GenerateRandomString;

@Service
@RequiredArgsConstructor
public class EconomistServiceImpl implements EconomistService {

    private final EconomistRepository economistRepository;
    private final PasswordEncoder encoder;
    private final GenerateRandomString generator;
    private final MailService mailService;
    private final InviteService inviteService;

    @Value("15")
    private int randomPasswordLength;

    @Override
    public Economist findByEmail(String email) {
        return economistRepository.findByEmail(email);
    }

    @Override
    @Transactional
    public Economist persist(Economist economist) {
        economist.setPassword(encoder.encode(generator.getRndStr(randomPasswordLength)));
        return economistRepository.save(economist);
    }

    @Override
    public void deleteAll() {
        economistRepository.deleteAll();
    }

    @Override
    @Transactional
    public Economist saveAndSendRegistInviteToEconomist(Economist economist) {
        Economist savedEconomist = persist(economist);
        mailService.sendRegistrationInviteByEmail(inviteService.persist(economist), economist);
        return savedEconomist;
    }
}
