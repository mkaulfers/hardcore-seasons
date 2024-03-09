package us.mkaulfers.hardcoreseasons.models;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

public class Localization {

    // System Messages
    public final String configReloaded;
    public final String mustBeAPlayer;
    public final String noPermission;
    public final String invalidCommand;

    // Redeeming Rewards GUI
    public final String loadingRewards;
    public final String rewardGoBack;
    public final String rewardPrevious;
    public final String rewardPage;
    public final String rewardNext;
    public final String rewardClose;
    public final String rewardPageCounter;

    // Selecting Season GUI
    public final String loadingSeasons;
    public final String selectSeasonTitle;
    public final String seasonItemName;
    public final String seasonPrevious;
    public final String seasonNext;
    public final String seasonClose;
    public final String seasonPageCounter;

    // Player Join Messages
    public final String haveDied;
    public final String requestingVoteTop;
    public final String requestingVoteBottom;

    // Player Death Messages
    public final String deathMessage;

    public Localization(
            String configReloaded,
            String mustBeAPlayer,
            String noPermission,
            String invalidCommand,
            String loadingRewards,
            String rewardGoBack,
            String rewardPrevious,
            String rewardPage,
            String rewardNext,
            String rewardClose,
            String rewardPageCounter,
            String loadingSeasons,
            String selectSeasonTitle,
            String seasonItemName,
            String seasonPrevious,
            String seasonNext,
            String seasonClose,
            String seasonPageCounter,
            String haveDied,
            String requestingVoteTop,
            String requestingVoteBottom,
            String deathMessage
    ) {

        this.configReloaded = configReloaded;
        this.mustBeAPlayer = mustBeAPlayer;
        this.noPermission = noPermission;
        this.invalidCommand = invalidCommand;

        this.loadingRewards = loadingRewards;
        this.rewardGoBack = rewardGoBack;
        this.rewardPrevious = rewardPrevious;
        this.rewardPage = rewardPage;
        this.rewardNext = rewardNext;
        this.rewardClose = rewardClose;
        this.rewardPageCounter = rewardPageCounter;

        this.loadingSeasons = loadingSeasons;
        this.selectSeasonTitle = selectSeasonTitle;
        this.seasonItemName = seasonItemName;
        this.seasonPrevious = seasonPrevious;
        this.seasonNext = seasonNext;
        this.seasonClose = seasonClose;
        this.seasonPageCounter = seasonPageCounter;

        this.haveDied = haveDied;
        this.requestingVoteTop = requestingVoteTop;
        this.requestingVoteBottom = requestingVoteBottom;

        this.deathMessage = deathMessage;
    }

    public String getLocalized(LocalizationKey key) {
        return switch (key) {
            case CONFIG_RELOADED -> unTranslateAlternateColorCodes(configReloaded);
            case MUST_BE_A_PLAYER -> unTranslateAlternateColorCodes(mustBeAPlayer);
            case NO_PERMISSION -> unTranslateAlternateColorCodes(noPermission);
            case INVALID_COMMAND -> unTranslateAlternateColorCodes(invalidCommand);
            case LOADING_REWARDS -> unTranslateAlternateColorCodes(loadingRewards);
            case REWARD_GO_BACK -> unTranslateAlternateColorCodes(rewardGoBack);
            case REWARD_PREVIOUS -> unTranslateAlternateColorCodes(rewardPrevious);
            case REWARD_PAGE -> unTranslateAlternateColorCodes(rewardPage);
            case REWARD_NEXT -> unTranslateAlternateColorCodes(rewardNext);
            case REWARD_CLOSE -> unTranslateAlternateColorCodes(rewardClose);
            case REWARD_PAGE_COUNTER -> unTranslateAlternateColorCodes(rewardPageCounter);
            case LOADING_SEASONS -> unTranslateAlternateColorCodes(loadingSeasons);
            case SELECT_SEASON_TITLE -> unTranslateAlternateColorCodes(selectSeasonTitle);
            case SEASON_ITEM_NAME -> unTranslateAlternateColorCodes(seasonItemName);
            case SEASON_PREVIOUS -> unTranslateAlternateColorCodes(seasonPrevious);
            case SEASON_NEXT -> unTranslateAlternateColorCodes(seasonNext);
            case SEASON_CLOSE -> unTranslateAlternateColorCodes(seasonClose);
            case SEASON_PAGE_COUNTER -> unTranslateAlternateColorCodes(seasonPageCounter);
            case HAVE_DIED -> translateAlternateColorCodes(haveDied);
            case REQUESTING_VOTE_TOP -> unTranslateAlternateColorCodes(requestingVoteTop);
            case REQUESTING_VOTE_BOTTOM -> unTranslateAlternateColorCodes(requestingVoteBottom);
            case DEATH_MESSAGE -> deathMessage;
        };
    }

    private static String translateAlternateColorCodes(String text) {
        String translated = ChatColor.translateAlternateColorCodes('&', text);
        Bukkit.getLogger().info("Translated: " + translated);
        return translated;
    }

    private static String unTranslateAlternateColorCodes(String text) {
        char[] array = text.toCharArray();
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] == ChatColor.COLOR_CHAR && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(array[i + 1]) != -1) {
                array[i] = '&';
                array[i + 1] = Character.toLowerCase(array[i + 1]);
            }
        }
        return new String(array);
    }
}

