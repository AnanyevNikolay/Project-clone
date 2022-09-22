package ru.mis2022.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mis2022.models.entity.Invite;

public interface InviteRepository extends JpaRepository<Invite, Long> {
    Invite findByToken(String token);

}
