package com.neocamp.soccer_matches.testUtils;

import java.time.format.DateTimeFormatter;

public class DateFormatter {

    public static final DateTimeFormatter CLUB_CREATION_DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy");
    public static final DateTimeFormatter MATCH_DATE_TIME = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
}
