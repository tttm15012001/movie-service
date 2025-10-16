package com.ryanmovie.model.entity;

public enum Genre {
    ACTION("Hành động"),
    ADVENTURE("Phiêu lưu"),
    COMEDY("Hài kịch"),
    DRAMA("Tâm lý"),
    FAMILY("Gia đình"),
    FANTASY("Giả tưởng"),
    HORROR("Kinh dị"),
    MYSTERY("Bí ẩn"),
    ROMANCE("Tình cảm"),
    SCI_FI("Khoa học viễn tưởng"),
    THRILLER("Giật gân"),
    ANIMATION("Hoạt hình"),
    DOCUMENTARY("Tài liệu"),
    MUSIC("Âm nhạc"),
    WAR("Chiến tranh"),
    CRIME("Tội phạm"),
    HISTORICAL("Lịch sử");

    private final String vietnameseName;

    Genre(String vietnameseName) {
        this.vietnameseName = vietnameseName;
    }

    public String getVietnameseName() {
        return vietnameseName;
    }
}
