package com.pedalgenie.pedalgenieback.global.time.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Holiday {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long holidayId;

    private LocalDate holidayDate;

    private String holidayName;

    @Builder
    public Holiday(Long holidayId, LocalDate holidayDate, String holidayName){
        this.holidayId = holidayId;
        this.holidayDate = holidayDate;
        this.holidayName = holidayName;
    }
}
