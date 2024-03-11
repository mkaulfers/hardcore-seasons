package us.mkaulfers.hardcoreseasons.models;

import java.sql.Timestamp;

public class Season implements Comparable<Season> {
    public int id;
    public int seasonId;
    public Timestamp startDate;
    public Timestamp softEndDate;
    public Timestamp hardEndDate;

    public Season(int id, int seasonId, Timestamp startDate, Timestamp softEndDate, Timestamp hardEndDate) {
        this.id = id;
        this.seasonId = seasonId;
        this.startDate = startDate;
        this.softEndDate = softEndDate;
        this.hardEndDate = hardEndDate;
    }
    @Override
    public int compareTo(Season o) {
        return this.seasonId - o.seasonId;
    }
}
