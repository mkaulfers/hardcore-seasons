package us.mkaulfers.hardcoreseasons.enums;

public enum LocalizationKey {
    CONFIG_RELOADED("configReloaded"),
    MUST_BE_A_PLAYER("mustBeAPlayer"),
    NO_PERMISSION("noPermission"),
    INVALID_COMMAND("invalidCommand"),
    LOADING_REWARDS("loadingRewards"),
    REWARD_GO_BACK("rewardGoBack"),
    REWARD_PREVIOUS("rewardPrevious"),
    REWARD_PAGE("rewardPage"),
    REWARD_NEXT("rewardNext"),
    REWARD_CLOSE("rewardClose"),
    REWARD_PAGE_COUNTER("rewardPageCounter"),
    INVENTORY_FULL("inventoryFull"),
    LOADING_SEASONS("loadingSeasons"),
    SELECT_SEASON_TITLE("selectSeasonTitle"),
    SEASON_ITEM_NAME("seasonItemName"),
    SEASON_PREVIOUS("seasonPrevious"),
    SEASON_NEXT("seasonNext"),
    SEASON_CLOSE("seasonClose"),
    SEASON_PAGE_COUNTER("seasonPageCounter"),
    HAVE_DIED("haveDied"),
    CANNOT_VOTE("cannotVote"),
    REQUESTING_VOTE_TOP("requestingVoteTop"),
    REQUESTING_VOTE_BOTTOM("requestingVoteBottom"),
    VOTE_CONTINUE_SUCCESS("voteContinueSuccess"),
    VOTE_END_SUCCESS("voteEndSuccess"),
    VOTE_FAIL("voteFail"),
    DEATH_MESSAGE("deathMessage"),
    SEASON_ENDING("seasonEnding"),
    SEASON_GENERATING("seasonGenerating"),
    PLAYER_RESURRECTED("playerResurrected"),
    SEASON_INFO("seasonInfo");

    private final String key;

    LocalizationKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

}
