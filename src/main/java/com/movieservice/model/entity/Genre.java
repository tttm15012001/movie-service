package com.movieservice.model.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Genre {
    ACTION_ADVENTURE(10759, "ACTION_ADVENTURE"),
    ANIMATION(16, "ANIMATION"),
    COMEDY(35, "COMEDY"),
    CRIME(80, "CRIME"),
    DOCUMENTARY(99, "DOCUMENTARY"),
    DRAMA(18, "DRAMA"),
    FAMILY(10751, "FAMILY"),
    KIDS(10762, "KIDS"),
    MYSTERY(9648, "MYSTERY"),
    NEWS(10763, "NEWS"),
    REALITY(10764, "REALITY"),
    SCI_FI_FANTASY(10765, "SCI_FI_FANTASY"),
    SOAP(10766, "SOAP"),
    TALK(10767, "TALK"),
    WAR_POLITICS(10768, "WAR_POLITICS"),
    WESTERN(37, "WESTERN");

    private final int id;
    private final String code;

    Genre(int id, String code) {
        this.id = id;
        this.code = code;
    }

    public static Genre fromId(int id) {
        return Arrays.stream(values())
                .filter(g -> g.id == id)
                .findFirst()
                .orElse(null);
    }

    public static String getGenreCodeFromId(int id) {
        Genre genre = fromId(id);
        return genre != null ? genre.getCode() : "Unknown";
    }
}
