package ru.mis2022.models.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
public class Invite {
    @Id
    @Column(name = "user_id")
    private Long id;
    @Column(unique = true)
    private String token;
    private LocalDateTime expirationDate;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    public Invite(String token, LocalDateTime expirationDate, User user) {
        this.token = token;
        this.expirationDate = expirationDate;
        this.user = user;
    }
}
