package ru.mis2022.models.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.springframework.lang.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@SuperBuilder
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private String firstName;

    private String lastName;

    @Nullable private String surname;

    private LocalDate birthday;

    private boolean enabled = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;

    @Nullable
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Invite invite;

    @ManyToOne
    @JoinColumn(name = "personal_history_id")
    private PersonalHistory personalHistory;

    public User(String firstName, String email, Role role, PersonalHistory personalHistory) {
        this.firstName = firstName;
        this.email = email;
        this.role = role;
        this.personalHistory = personalHistory;
    }

    public User(String firstName, String email, Role role) {
        this.firstName = firstName;
        this.email = email;
        this.role = role;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(role);
    }



    public String getPassword() {
        return this.password;
    }

    public String getUsername() {
        return this.email;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public User(String email, String password, String firstName, String lastName, @Nullable String surname,
                LocalDate birthday, Role role) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.surname = surname;
        this.birthday = birthday;
        this.role = role;
    }

    public User(String firstName, String lastName, @Nullable String surname, LocalDate birthday, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.surname = surname;
        this.birthday = birthday;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        User user = (User) o;
        return id != null && Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" +
                "id = " + id + ", " +
                "email = " + email + ", " +
                "password = " + password + ", " +
                "firstName = " + firstName + ", " +
                "lastName = " + lastName + ", " +
                "surname = " + surname + ", " +
                "birthday = " + birthday + ", " +
                "enabled = " + enabled + ")";
    }
}