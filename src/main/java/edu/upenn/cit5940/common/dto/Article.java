package edu.upenn.cit5940.common.dto;

public class Article {
	private String uri;
	private String title;
	private String body;
	private String date;

	public static final int EXPECTED_FIELD_COUNT = 16;

	public Article(String uri, String title, String body, String date) {
	    this.uri = uri;
	    this.title = title;
	    this.body = body;
	    this.date = date;
	}

	public String getUri() {
	    return uri;
	}

	public String getTitle() {
	    return title;
	}

	public String getBody() {
	    return body;
	}

	public String getDate() {
	    return date;
	}

    @Override
    public String toString() {
        return "Article\n" + "URI: " + uri + "\n" + "Title: " + title + "\n" + "Body: " + body + "\n" + "Date: " + date;
    }
}
