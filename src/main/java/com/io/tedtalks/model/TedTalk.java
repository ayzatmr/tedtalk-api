package com.io.tedtalks.model;

import com.io.tedtalks.dto.TedTalkRequest;
import com.io.tedtalks.jooq.tables.pojos.TedTalks;
import java.time.YearMonth;

public class TedTalk extends TedTalks {

  public TedTalk() {
    super();
  }

  public TedTalk(TedTalks value) {
    super(value);
  }

  public static TedTalk of(TedTalkRequest request) {
    return of(
        request.title(),
        request.author(),
        request.date(),
        request.views(),
        request.likes(),
        request.link());
  }

  public static TedTalk of(
      String title, String author, YearMonth yearMonth, long views, long likes, String link) {

    TedTalk tedTalk = new TedTalk();
    tedTalk.setTitle(title.trim());
    tedTalk.setAuthor(author.trim());
    tedTalk.setYearValue(yearMonth.getYear());
    tedTalk.setMonthValue(yearMonth.getMonthValue());
    tedTalk.setViews(Math.max(0, views));
    tedTalk.setLikes(Math.max(0, likes));
    tedTalk.setLink(link.trim());

    return tedTalk;
  }

  public double calculateInfluence(double viewsWeight, double likesWeight) {
    return (getViews() * viewsWeight) + (getLikes() * likesWeight);
  }

  public YearMonth getYearMonth() {
    return YearMonth.of(getYearValue(), getMonthValue());
  }

  public void setYearMonth(YearMonth yearMonth) {
    setYearValue(yearMonth.getYear());
    setMonthValue(yearMonth.getMonthValue());
  }

  public void updateFrom(TedTalkRequest request) {
    if (request == null) {
      throw new IllegalArgumentException("Request must not be null");
    }

    setTitle(request.title().trim());
    setAuthor(request.author().trim());
    setYearMonth(request.date());
    setViews(request.views());
    setLikes(request.likes());
    setLink(request.link().trim());
    validate();
  }

  private void validate() {
    if (getTitle() == null || getTitle().isBlank()) {
      throw new IllegalStateException("Title is required");
    }
    if (getAuthor() == null || getAuthor().isBlank()) {
      throw new IllegalStateException("Author is required");
    }
    if (getLink() == null || getLink().isBlank()) {
      throw new IllegalStateException("Link is required");
    }
    if (getMonthValue() < 1 || getMonthValue() > 12) {
      throw new IllegalStateException("Invalid month: " + getMonthValue());
    }

    setTitle(getTitle().trim());
    setAuthor(getAuthor().trim());
    setLink(getLink().trim());
    setViews(Math.max(0, getViews()));
    setLikes(Math.max(0, getLikes()));
  }
}
