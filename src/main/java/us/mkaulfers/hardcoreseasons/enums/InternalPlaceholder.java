package us.mkaulfers.hardcoreseasons.enums;

public enum InternalPlaceholder {
    CURRENT_SEASON("current_season"),
    NEXT_SEASON("next_season"),
    REWARD_SELECT_CURRENT_PAGE("reward_select_current_page"),
    REWARD_SELECT_TOTAL_PAGES("reward_select_total_pages"),
    SEASON_SELECT_CURRENT_PAGE("season_select_current_page"),
    SEASON_SELECT_TOTAL_PAGES("season_select_total_pages"),
    PAST_SEASON_NUMBER("past_season_number");

    private final String placeholder;

    InternalPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getPlaceholder() {
        return placeholder;
    }
}
