package ru.mis2022.service.entity;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.mis2022.models.entity.Invite;
import ru.mis2022.models.entity.User;

import java.time.LocalDate;
import java.util.List;

public interface UserService extends UserDetailsService {

    User getCurrentUserByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsById(Long id);

    boolean isExistsByNameAndId(String name, Long id);

    List<User> findPersonalByFullName(String fullName, String roleName);

    List<User> findPersonalByBirthdayInRange(LocalDate from, LocalDate to);

    User persist(User user);

    User findUserById(Long id);

    User save(User user);

    User saveUserByInviteAndDeleteInvite(User user, String password, Invite invite);

    List<User> findPersonalWhoGoVacationInRange(LocalDate from, LocalDate to);

    void sendingEmailToChangePassword(String email);

}
