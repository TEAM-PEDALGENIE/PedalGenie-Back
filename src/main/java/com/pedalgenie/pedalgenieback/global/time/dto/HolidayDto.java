package com.pedalgenie.pedalgenieback.global.time.dto;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlRootElement(name = "item")
@Getter
@NoArgsConstructor
public class HolidayDto {

    @XmlElement(name = "dateKind")
    private String dateKind;

    @XmlElement(name = "dateName")
    private String dateName;

    @XmlElement(name = "isHoliday")
    private String isHoliday;

    @XmlElement(name = "locdate")
    private String locdate;

    @XmlElement(name = "seq")
    private int seq;
}
