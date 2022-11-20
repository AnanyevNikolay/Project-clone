package ru.mis2022.service.entity.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.mis2022.models.entity.Invite;
import ru.mis2022.models.entity.User;
import ru.mis2022.repositories.UserRepository;
import ru.mis2022.service.entity.InviteService;
import ru.mis2022.service.entity.MailService;
import ru.mis2022.service.entity.UserService;
import ru.mis2022.utils.GenerateRandomString;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final InviteService inviteService;
    private final GenerateRandomString generator;

    private final MailService mailService;

    @Value("15")
    private int randomPasswordLength;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email);
    }

    @Override
    public User getCurrentUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public boolean existsById(Long id) {
        return userRepository.existsById(id);
    }

    @Override
    public boolean isExistsByNameAndId(String name, Long id) {
        return userRepository.existsByEmailAndId(name, id);
    }

    @Override
    public List<User> findPersonalByFullName(String fullName, String roleName) {
        return userRepository.findPersonalByFullName(fullName, roleName);
    }

    @Override
    public User persist(User user) {
        return userRepository.save(user);
    }

    @Override
    public User findUserById(Long id) {
        return userRepository.findUserById(id);
    }

    @Override
    public User save(User user) {
        user.setPassword(encoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Override
    public User saveUserByInviteAndDeleteInvite(User user, String password, Invite invite) {
        user.setPassword(password);
        user = userRepository.save(user);
        inviteService.delete(invite);
        return user;
    }

    @Override
    public List<User> findPersonalByBirthdayInRange(LocalDate from, LocalDate to) {
        return userRepository.findPersonalWhoBirthdayInRange(from, to);
    }

    @Override
    public List<User> findPersonalWhoGoVacationInRange(LocalDate from, LocalDate to) {
        return userRepository.findPersonalWhoGoVacationInRange(from, to);
    }

    @Override
    @Transactional
    public void sendingEmailToChangePassword(String email) {
        User user = userRepository.findByEmail(email);
        mailService.sendRegistrationInviteByEmail(inviteService.persist(user), user);
    }
}
