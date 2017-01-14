package com.fiskkit.model;

/**
 * Created by joshuaellinger on 3/30/15.
 */
public class Respect {
    int id;
    String articleId;
    int fiskId;
    User user;
    User author;
    SentenceComment sentenceComment;

    public Respect() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getArticleId() {
        return articleId;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public int getFiskId() {
        return fiskId;
    }

    public void setFiskId(int fiskId) {
        this.fiskId = fiskId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public SentenceComment getSentenceComment() {
        return sentenceComment;
    }

    public void setSentenceComment(SentenceComment sentenceComment) {
        this.sentenceComment = sentenceComment;
    }
}
