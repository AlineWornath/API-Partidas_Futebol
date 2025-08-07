package com.neocamp.soccer_matches.utils;

import java.util.UUID;

public class UuidUtils {

    public static UUID parseUuid(String uuidStr, String fieldName) {
        try{
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(fieldName + " is not a valid UUID: " + uuidStr, e);
        }
    }
}
