//package com.pedalgenie.pedalgenieback.domain.datetime;
//
//import com.pedalgenie.pedalgenieback.domain.rent.ServiceZone;
//
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.time.ZonedDateTime;
//import java.util.TimeZone;
//
//public class TimeZoneUtils {
//
//    public static final TimeZone UTC = TimeZone.getTimeZone("UTC");
//
//    // 특정 (로컬) 시간을 주어진 ServiceZone 시간대로 변환
//    public static LocalDateTime convertTo(final LocalDateTime dateTime, final ServiceZone serviceZone){
//        return dateTime.atZone(UTC.toZoneId())
//                .withZoneSameInstant(ZoneId.of(serviceZone.getTimeZone()))
//                .toLocalDateTime();
//    }
//
//    // 지정된 ZoneDateTime 을 UTC 시간으로 변환
//    public static LocalDateTime convertToUTC(final ZonedDateTime zonedDateTime){
//        return zonedDateTime.withZoneSameInstant(UTC.toZoneId()).toLocalDateTime();
//    }
//}
