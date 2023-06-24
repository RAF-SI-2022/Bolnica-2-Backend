package com.raf.si.userservice.model;

import com.raf.si.userservice.model.enums.Profession;
import com.raf.si.userservice.model.enums.Title;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private UUID lbz = UUID.randomUUID();
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "date_of_birth", nullable = false)
    private Date dateOfBirth;
    @Column(nullable = false)
    private String gender;
    @Column(nullable = false)
    private String JMBG;
    @Column(name = "residential_address", nullable = false)
    private String residentialAddress;
    @Column(name = "place_of_living", nullable = false)
    private String placeOfLiving;
    @Column
    private String phone;
    @Column(nullable = false, unique = true)
    private String email;
    @Enumerated
    @Column(nullable = false)
    private Title title;
    @Enumerated
    @Column(nullable = false)
    private Profession profession;
    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;
    @Column(name = "password_token", nullable = false)
    private UUID passwordToken = UUID.randomUUID();
    @Column(name = "covid_access", nullable = false)
    private boolean covidAccess = false;
    @Column(name = "days_off", nullable = false)
    private Integer daysOff;
    @Column(name = "used_days_off", nullable = false)
    private Integer usedDaysOff = 0;
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Shift> shifts;
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;
    @ManyToMany
    @JoinTable(
            name = "user_permissions",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private List<Permission> permissions;

    public void incrementUsedDaysOff() {
        this.usedDaysOff++;
    }

    public void decrementUsedDaysOff() {
        this.usedDaysOff--;
    }
}
