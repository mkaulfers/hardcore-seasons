package us.mkaulfers.hardcoreseasons.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import java.sql.Timestamp;

@DatabaseTable(tableName = "seasons")
public class Season {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "season_id")
    private int seasonId;

    @DatabaseField(columnName = "start_date")
    private Timestamp startDate;

    @DatabaseField(columnName = "soft_end_date")
    private Timestamp softEndDate;

    @DatabaseField(columnName = "hard_end_date")
    private Timestamp hardEndDate;

    public Season() {}

    public int getId() {
        return id;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public Timestamp getStartDate() {
        return startDate;
    }

    public Timestamp getSoftEndDate() {
        return softEndDate;
    }

    public Timestamp getHardEndDate() {
        return hardEndDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }

    public void setStartDate(Timestamp startDate) {
        this.startDate = startDate;
    }

    public void setSoftEndDate(Timestamp softEndDate) {
        this.softEndDate = softEndDate;
    }

    public void setHardEndDate(Timestamp hardEndDate) {
        this.hardEndDate = hardEndDate;
    }
}
