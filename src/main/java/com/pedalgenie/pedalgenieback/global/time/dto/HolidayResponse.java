package com.pedalgenie.pedalgenieback.global.time.dto;

import jakarta.xml.bind.annotation.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement(name = "response")
@Getter
@NoArgsConstructor
public class HolidayResponse {

    @XmlElement(name = "header")
    private Header header;

    @XmlElement(name = "body")
    private Body body;

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "Header")
    @Getter
    @NoArgsConstructor
    public static class Header {
        @XmlElement(name = "resultCode")
        private String resultCode;

        @XmlElement(name = "resultMsg")
        private String resultMsg;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "Body")
    @Getter
    @NoArgsConstructor
    public static class Body {
        @XmlElement(name = "items")
        private Items items;

        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "Items")
        @Getter
        @NoArgsConstructor
        public static class Items {
            @XmlElement(name = "item")
            private List<HolidayDto> holidays;
        }
    }
}