package com.fiskkit.model;

import static humanize.Humanize.metricPrefix;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joshuaellinger on 3/30/15.
 */
public class Article {
    private String id;
    private String articleTitle;
    private String author;
    private String publisher;
    private List<Respect> respectList;
    private List<Fisk> fiskList;
    private SentenceComment topComment;
    private int fiskCount = 0;
    private String fiskCountHumanized;
    private int fiskCountOffset = -3;
    private String fiskCountOffsetHumanized;
    private int newRespectCount = 0;
    private String newRespectCountHumanized;
    private int newRespectCountOffset = -3;
    private String newRespectCountOffsetHumanized;

    /**
     * Constructor.
     */
    public Article() {
        this.respectList = new ArrayList<Respect>();
        this.fiskList = new ArrayList<Fisk>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return articleTitle;
    }

    public void setTitle(String title) {
        this.articleTitle = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public List<Respect> getRespectList() {
        return respectList;
    }

    public void setRespectList(List<Respect> respectList) {
        this.respectList = respectList;
    }

    public void addRespect(Respect respect) {
        respectList.add(respect);
        setNewRespectCount(getNewRespectCount() + 1);
    }

    public List<Fisk> getFiskList() {
        return fiskList;
    }

    public void setFiskList(List<Fisk> fiskList) {
        this.fiskList = fiskList;
        setFiskCount(fiskList.size());
    }

    public void addFisk(Fisk fisk) {
        fiskList.add(fisk);
        setFiskCount(getFiskCount()+1);
    }

    public SentenceComment getTopComment() {
        return topComment;
    }

    public void setTopComment(SentenceComment topComment) {
        this.topComment = topComment;
    }

    public void setFiskCount(int fiskCount) {
        this.fiskCount = fiskCount;
        this.fiskCountHumanized = metricPrefix(this.fiskCount);
        this.fiskCountOffset = fiskCount - 3;
        this.fiskCountOffsetHumanized = metricPrefix(fiskCountOffset);
    }

    public int getFiskCount() {
        return fiskCount;
    }

    public String getFiskCountHumanized() {
        return fiskCountHumanized;
    }

    public int getFiskCountOffset() {
        return fiskCountOffset;
    }

    public void setNewRespectCount(int newRespectCount) {
        this.newRespectCount = newRespectCount;
        this.newRespectCountHumanized = metricPrefix(this.newRespectCount);
        this.newRespectCountOffset = newRespectCount - 3;
        this.newRespectCountOffsetHumanized = metricPrefix(newRespectCountOffset);
    }

    public int getNewRespectCount() {
        return newRespectCount;
    }

    public String getNewRespectCountHumanized() {
        return newRespectCountHumanized;
    }

    public int getNewRespectCountOffset() {
        return newRespectCountOffset;
    }
}
