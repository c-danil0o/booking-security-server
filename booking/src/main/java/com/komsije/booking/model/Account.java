package com.komsije.booking.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true)
    private String email;
    @Column(nullable = true)
    private String password;
    @Column(nullable = false)
    private boolean isBlocked = false;
    @Column(nullable = false)
    private boolean isActivated = false;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ElementCollection
    @Enumerated(EnumType.STRING)
    private Set<Settings> settings = new HashSet<>();
    @OneToMany(mappedBy = "receiver", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Notification> notifications = new HashSet<>();
    @OneToMany(mappedBy = "reportedUser", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Report> reports = new HashSet<>();
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Report> reports2 = new HashSet<>();
    @OneToMany(mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Review> reviews = new HashSet<>();
    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<ConfirmationToken> tokens = new HashSet<>();
    @Column(nullable = true)
    private String alias;

}
