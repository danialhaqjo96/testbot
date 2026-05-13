package com.teknotasvir;

public class MusicInfoModel {
    private String title;
    private String artist;
    private String album;
    private String genre;
    private String year;
    public MusicInfoModel(String title, String artist, String album, String genre, String year) {
        this.title = title;
        this.artist = artist;
        this.album = album;
        this.genre = genre;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("عنوان : ");
        sb.append(title);
        sb.append("\n");

        sb.append("خواننده : ");
        sb.append(artist);
        sb.append("\n");

        sb.append("آلبوم : ");
        sb.append(album);
        sb.append("\n");

        sb.append("ژانر : ");
        sb.append(genre);
        sb.append("\n");

        sb.append("سال : ");
        sb.append(year);
        sb.append("\n");

        return sb.toString();
    }
}
