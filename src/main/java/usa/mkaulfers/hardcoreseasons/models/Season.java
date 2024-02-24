package usa.mkaulfers.hardcoreseasons.models;
import org.bukkit.block.Container;
import java.util.Date;

public class Season {
    public int seasonId;
    public Date startDate;
    public Date endDate;
    public boolean active;

    public Season(int seasonId, Date startDate, Date endDate, boolean active) {
        this.seasonId = seasonId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.active = active;
    }
}
