package com.apps.dilip_pashi.letsgo;

/**
 * Created by Dilip_pashi on 14-12-2016.
 */

public class DiaryModel {
    private String diaryTitle;
    private String diaryDesc;
    private String diaryDate;
    private String diaryLocation;
    private String diaryImage;
    private String diaryVideo;

    public DiaryModel(String diaryTitle, String diaryDesc, String diaryDate, String diaryLocation, String diaryImage, String diaryVideo) {
        this.diaryTitle = diaryTitle;
        this.diaryDesc = diaryDesc;
        this.diaryDate = diaryDate;
        this.diaryLocation = diaryLocation;
        this.diaryImage = diaryImage;
        this.diaryVideo = diaryVideo;
    }

    public DiaryModel() {
    }

    public String getDiaryTitle() {
        return diaryTitle;
    }

    public void setDiaryTitle(String diaryTitle) {
        this.diaryTitle = diaryTitle;
    }

    public String getDiaryDesc() {
        return diaryDesc;
    }

    public void setDiaryDesc(String diaryDesc) {
        this.diaryDesc = diaryDesc;
    }

    public String getDiaryDate() {
        return diaryDate;
    }

    public void setDiaryDate(String diaryDate) {
        this.diaryDate = diaryDate;
    }

    public String getDiaryLocation() {
        return diaryLocation;
    }

    public void setDiaryLocation(String diaryLocation) {
        this.diaryLocation = diaryLocation;
    }

    public String getDiaryImage() {
        return diaryImage;
    }

    public void setDiaryImage(String diaryImage) {
        this.diaryImage = diaryImage;
    }

    public String getDiaryVideo() {
        return diaryVideo;
    }

    public void setDiaryVideo(String diaryVideo) {
        this.diaryVideo = diaryVideo;
    }
}
