package us.mkaulfers.hardcoreseasons.models;

import java.util.Date;

public class Season implements Comparable<Season> {
    public int id;
    public int seasonId;
    public Date startDate;
    public Date endDate;

    public Season(int id, int seasonId, Date startDate, Date endDate) {
        this.id = id;
        this.seasonId = seasonId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Override
    public int compareTo(Season o) {
        return this.seasonId - o.seasonId;
    }
}
