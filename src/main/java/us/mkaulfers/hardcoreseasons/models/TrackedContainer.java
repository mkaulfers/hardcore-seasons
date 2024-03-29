package us.mkaulfers.hardcoreseasons.models;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import us.mkaulfers.hardcoreseasons.utils.InventoryUtils;

@DatabaseTable(tableName = "tracked_containers")
public class TrackedContainer {
    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "season_id")
    private int seasonId;

    @DatabaseField(columnName = "pos_x")
    private int posX;

    @DatabaseField(columnName = "pos_y")
    private int posY;

    @DatabaseField(columnName = "pos_z")
    private int posZ;

    @DatabaseField(columnName = "world")
    private String world;

    @DatabaseField(columnName = "type")
    private String type;

    @DatabaseField(dataType = DataType.LONG_STRING, columnName = "contents")
    private String contents;

    public TrackedContainer() {}

    public int getId() {
        return id;
    }

    public int getSeasonId() {
        return seasonId;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public int getPosZ() {
        return posZ;
    }

    public String getWorld() {
        return world;
    }

    public String getType() {
        return type;
    }

    public String getContents() {
        return contents;
    }

    public int getContentsCount() {
        return InventoryUtils.countOfItemStacksInBase64(contents);
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSeasonId(int seasonId) {
        this.seasonId = seasonId;
    }

    public void setPosX(int posX) {
        this.posX = posX;
    }

    public void setPosY(int posY) {
        this.posY = posY;
    }

    public void setPosZ(int posZ) {
        this.posZ = posZ;
    }

    public void setWorld(String world) {
        this.world = world;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
