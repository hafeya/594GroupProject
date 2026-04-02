package edu.upenn.cit5940.common.dto;

public class Article {
	private String id;
    private String title;
    private String content;
    private String date;

    public Article(String id, String title, String content, String date) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Article\n" + "ID: " + id + "\n" + "Title: " + title + "\n" + "Date: " + date;
    }
}
