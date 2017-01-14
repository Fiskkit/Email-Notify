package com.fiskkit.model;

import static humanize.Humanize.wordWrap;

/**
 * Created by joshuaellinger on 3/30/15.
 */
public class SentenceComment {
    private int id;
    private String body;
    private String bodyIncipit;
    private User user;
    private int fiskId;
    private int articleId;
    private int wordCount;
    private int respectCount;
    private String commentUrl;

    /**
     * Constructor
     */
    public SentenceComment() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
        this.bodyIncipit = wordWrap(body, 175);
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getFiskId() {
        return fiskId;
    }

    public void setFiskId(int fiskId) {
        this.fiskId = fiskId;
    }

    public int getArticleId() {
        return articleId;
    }

    public void setArticleId(int articleId) {
        this.articleId = articleId;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public int getRespectCount() {
        return respectCount;
    }

    public void setRespectCount(int respectCount) {
        this.respectCount = respectCount;
    }

    public void setCommentUrl(String article_id) {
        this.commentUrl = "http://fiskkit.com/articles/" + article_id + "/fisk/" + fiskId;
    }
}
