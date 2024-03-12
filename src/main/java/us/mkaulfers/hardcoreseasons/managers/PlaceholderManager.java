package us.mkaulfers.hardcoreseasons.managers;

import us.mkaulfers.hardcoreseasons.enums.InternalPlaceholder;

public class PlaceholderManager {
    public int phCurrentSeason;
    public int phNextSeason;
    public int phRewardCurrentPage;
    public int phRewardNextPage;
    public int phSeasonSelectCurrentPage;
    public int phSeasonSelectNextPage;
    public int pastSeasonNum;

    public PlaceholderManager() {
        phCurrentSeason = 0;
        phNextSeason = 0;
        phRewardCurrentPage = 0;
        phRewardNextPage = 0;
        phSeasonSelectCurrentPage = 0;
        phSeasonSelectNextPage = 0;
        pastSeasonNum = 0;
    }

    // Should replace the placeholder that has the format {placeholder} with the value
    public String getPlaceholderValue(String message) {
        message = message.replace("{current_season}", String.valueOf(phCurrentSeason));
        message = message.replace("{next_season}", String.valueOf(phNextSeason));
        message = message.replace("{reward_select_current_page}", String.valueOf(phRewardCurrentPage));
        message = message.replace("{reward_select_total_pages}", String.valueOf(phRewardNextPage));
        message = message.replace("{season_select_current_page}", String.valueOf(phSeasonSelectCurrentPage));
        message = message.replace("{season_select_total_pages}", String.valueOf(phSeasonSelectNextPage));
        message = message.replace("{past_season_number}", String.valueOf(pastSeasonNum));
        return message;
    }

    public void setPlaceholderValue(InternalPlaceholder placeholder, String value) {
        switch (placeholder) {
            case CURRENT_SEASON:
                phCurrentSeason = Integer.parseInt(value);
                break;
            case NEXT_SEASON:
                phNextSeason = Integer.parseInt(value);
                break;
            case REWARD_SELECT_CURRENT_PAGE:
                phRewardCurrentPage = Integer.parseInt(value);
                break;
            case REWARD_SELECT_TOTAL_PAGES:
                phRewardNextPage = Integer.parseInt(value);
                break;
            case SEASON_SELECT_CURRENT_PAGE:
                phSeasonSelectCurrentPage = Integer.parseInt(value);
                break;
            case SEASON_SELECT_TOTAL_PAGES:
                phSeasonSelectNextPage = Integer.parseInt(value);
                break;
            case PAST_SEASON_NUMBER:
                pastSeasonNum = Integer.parseInt(value);
                break;
        }
    }
}
