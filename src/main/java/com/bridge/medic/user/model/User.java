package com.bridge.medic.user.model;

import com.bridge.medic.auth.token.Token;
import com.bridge.medic.chat.model.Chat;
import com.bridge.medic.chat.model.Participant;
import com.bridge.medic.config.security.authorization.model.Role;
import com.bridge.medic.specialist.model.SpecialistData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User implements UserDetails {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "user_id", unique = true)
    private Long id;
    @Column(nullable = false, name = "first_name")
    private String firstName;
    @Column(nullable = false, name = "last_name")
    private String lastName;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = false, unique = true)
    private String login;
    @Column(nullable = false)
    private String password;
    @Column(name = "is_locked", columnDefinition = "boolean default false")
    private Boolean isLocked;
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinTable(
            name = "users_role_junction",
            joinColumns = {@JoinColumn(name = "user_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private List<Role> roles = new ArrayList<>();
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Token> tokens;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "credit_card_detail_id")
    CreditCardDetail creditCardDetail;
    @ManyToMany
    @JoinTable(
            name = "user_language",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "language_id")
    )
    private List<Language> languages = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Chat> chats = new ArrayList<>();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private SpecialistData specialistData;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @ManyToOne()
    @JoinColumn(name = "city_id")
    private City city;

    @Column(name = "profile_image_url")
    private String image_url;

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void addLanguageIfAbsent(Language language) {
        if (language == null) return;

        boolean alreadyAdded = this.languages.stream()
                .anyMatch(l -> l.getId().equals(language.getId()));

        if (!alreadyAdded) {
            this.languages.add(language);
        }
    }

    public void addRoleIfAbsent(Role role) {
        if (role == null) return;

        boolean alreadyAdded = this.roles.stream()
                .anyMatch(r -> r.getId().equals(role.getId()));

        if (!alreadyAdded) {
            this.roles.add(role);
        }
    }

    public void removeRoleIfPresent(Role role){
        if (role == null) return;

        boolean alreadyAdded = this.roles.stream()
                .anyMatch(r -> r.getId().equals(role.getId()));

        if (alreadyAdded) {
            this.roles.remove(role);
        }
    }
}
