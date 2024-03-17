package us.mkaulfers.hardcoreseasons.managers;

import java.sql.Timestamp;

public class PlaceholderManager {
    public int currentSeason;
    public int nextSeason;
    public int rewardCurrentPage;
    public int rewardNextPage;
    public int seasonSelectCurrentPage;
    public int seasonSelectTotalPages;
    public int pastSeasonNum;
    public String resurrectedPlayerName;
    public String seasonStartDate;
    public String seasonVoteStartDate;
    public String seasonEndDate;
    public int playersActiveCount;
    public int playersAliveCount;
    public int playersDeadCount;
    public int inventoriesCount;
    public int inventoriesItemsCount;
    public int enderChestsCount;
    public int enderChestsItemsCount;
    public int containersCount;
    public int containersItemsCount;

    public PlaceholderManager() {
        currentSeason = 0;
        nextSeason = 0;
        rewardCurrentPage = 0;
        rewardNextPage = 0;
        seasonSelectCurrentPage = 0;
        seasonSelectTotalPages = 0;
        pastSeasonNum = 0;
        resurrectedPlayerName = "";
        seasonStartDate = "--";
        seasonVoteStartDate = "--";
        seasonEndDate = "--";
        playersActiveCount = 0;
        playersAliveCount = 0;
        playersDeadCount = 0;
        inventoriesCount = 0;
        inventoriesItemsCount = 0;
        enderChestsCount = 0;
        enderChestsItemsCount = 0;
        containersCount = 0;
        containersItemsCount = 0;
    }

    // Should replace the placeholder that has the format {placeholder} with the value
    public String getPlaceholderValue(String message) {
        message = message.replace("{current_season}", String.valueOf(currentSeason));
        message = message.replace("{next_season}", String.valueOf(nextSeason));
        message = message.replace("{reward_select_current_page}", String.valueOf(rewardCurrentPage));
        message = message.replace("{reward_select_total_pages}", String.valueOf(rewardNextPage));
        message = message.replace("{season_select_current_page}", String.valueOf(seasonSelectCurrentPage));
        message = message.replace("{season_select_total_pages}", String.valueOf(seasonSelectTotalPages));
        message = message.replace("{past_season_number}", String.valueOf(pastSeasonNum));
        message = message.replace("{resurrected_player_name}", resurrectedPlayerName);
        message = message.replace("{season_start_date}", seasonStartDate);
        message = message.replace("{season_vote_start_date}", seasonVoteStartDate);
        message = message.replace("{season_end_date}", seasonEndDate);
        message = message.replace("{players_active_count}", String.valueOf(playersActiveCount));
        message = message.replace("{players_alive_count}", String.valueOf(playersAliveCount));
        message = message.replace("{players_dead_count}", String.valueOf(playersDeadCount));
        message = message.replace("{inventories_count}", String.valueOf(inventoriesCount));
        message = message.replace("{inventories_items_count}", String.valueOf(inventoriesItemsCount));
        message = message.replace("{ender_chests_count}", String.valueOf(enderChestsCount));
        message = message.replace("{ender_chests_items_count}", String.valueOf(enderChestsItemsCount));
        message = message.replace("{containers_count}", String.valueOf(containersCount));
        message = message.replace("{containers_items_count}", String.valueOf(containersItemsCount));
        return message;
    }
}
