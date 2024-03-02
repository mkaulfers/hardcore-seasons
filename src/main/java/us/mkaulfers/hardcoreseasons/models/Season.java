package us.mkaulfers.hardcoreseasons.models;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class Season implements Comparable<Season> {
    public int seasonId;

    public Date startDate;
    public Date endDate;

    public Season(int seasonId, Date startDate, Date endDate) {
        this.seasonId = seasonId;
        this.startDate = startDate;
        this.endDate = endDate;
    }
    @Override
    public int compareTo(@NotNull Season o) {
        return this.seasonId - o.seasonId;
    }
}
