package com.raf.si.userservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.raf.si.userservice.model.enums.ShiftType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class Shift implements Comparable<Shift> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift_type", nullable = false)
    private ShiftType shiftType;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Override
    public int compareTo(Shift o) {
        if (o.getStartTime().isBefore(this.startTime)) {
            return 1;
        } else if (o.getStartTime().isAfter(this.startTime)) {
            return -1;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return shiftType.getNotation() + ", " + startTime + " - "  + endTime;
    }
}
