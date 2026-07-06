package com.seohamin.campon.domain.user.entity;

import com.seohamin.campon.global.constant.Equipment;
import com.seohamin.campon.global.constant.Facility;
import com.seohamin.campon.global.constant.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 연결된 OAuth2
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserOauth> userOauths = new ArrayList<>();

    // 유저 Role
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull
    private Role role;

    // 차량 보유 여부
    @Column(nullable = false)
    @NotNull
    private Boolean hasCar;

    // 선호 사항
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "member_preferred_conditions",
            joinColumns = @JoinColumn(name = "member_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "facility", nullable = false)
    private Set<Facility> preferredConditions = new HashSet<>();

    // 보유 장비
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
            name = "member_equipments",
            joinColumns = @JoinColumn(name = "member_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "equipment", nullable = false)
    private Set<Equipment> equipments = new HashSet<>();

    @CreatedDate
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public User(
            final Role role,
            final Boolean hasCar,
            final Set<Facility> preferredConditions,
            final Set<Equipment> equipments
    ) {
        this.role = role;
        this.hasCar = hasCar;
        this.preferredConditions = preferredConditions;
        this.equipments = equipments;
    }

    public void updateHasCar(final Boolean hasCar) {
        this.hasCar = hasCar;
    }

    public void updatePreferredConditions(final Set<Facility> preferredConditions) {
        this.preferredConditions = preferredConditions;
    }

    public void updateEquipments(final Set<Equipment> equipments) {
        this.equipments = equipments;
    }
}
