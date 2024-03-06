package us.mkaulfers.hardcoreseasons.models;

import java.util.Date;

public class Season implements Comparable<Season> {
    public int id;
    public int seasonId;
    public Date startDate;
    public Date softEndDate;
    public Date hardEndDate;

    public Season(int id, int seasonId, Date startDate, Date softEndDate, Date hardEndDate) {
        this.id = id;
        this.seasonId = seasonId;
        this.startDate = startDate;
        this.hardEndDate = hardEndDate;
    }
    @Override
    public int compareTo(Season o) {
        return this.seasonId - o.seasonId;
    }
}
