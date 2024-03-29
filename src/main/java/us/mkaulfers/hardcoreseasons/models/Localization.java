package us.mkaulfers.hardcoreseasons.models;

import org.bukkit.ChatColor;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.enums.LocalizationKey;

import java.util.ArrayList;
import java.util.List;

public class Localization {
    HardcoreSeasons plugin;

    public final String configVersion;

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
    public final String inventoryFull;

    // Selecting SeasonCMD GUI
    public final String loadingSeasons;
    public final String selectSeasonTitle;
    public final String seasonItemName;
    public final String seasonPrevious;
    public final String seasonNext;
    public final String seasonClose;
    public final String seasonPageCounter;

    // Player Join Messages
    public final String haveDied;

    // Vote Messages
    public final String cannotVote;
    public final String requestingVoteTop;
    public final String requestingVoteBottom;
    public final String voteContinueSuccess;
    public final String voteEndSuccess;
    public final String voteFail;

    // Player Death Messages
    public final String deathMessage;

    // SeasonCMD Messages
    public final String seasonEnding;
    public final String seasonGenerating;

    public final String playerResurrected;

    public final List<String> seasonInfo;

    public Localization(
            HardcoreSeasons plugin,
            String configversion,
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
            String inventoryFull,
            String loadingSeasons,
            String selectSeasonTitle,
            String seasonItemName,
            String seasonPrevious,
            String seasonNext,
            String seasonClose,
            String seasonPageCounter,
            String haveDied,
            String cannotVote,
            String requestingVoteTop,
            String requestingVoteBottom,
            String voteContinueSuccess,
            String voteEndSuccess,
            String voteFail,
            String deathMessage,
            String seasonEnding,
            String seasonGenerating,
            String playerResurrected,
            List<String> seasonInfo
    ) {
        this.plugin = plugin;
        this.configVersion = configversion;
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
        this.inventoryFull = inventoryFull;
        this.loadingSeasons = loadingSeasons;
        this.selectSeasonTitle = selectSeasonTitle;
        this.seasonItemName = seasonItemName;
        this.seasonPrevious = seasonPrevious;
        this.seasonNext = seasonNext;
        this.seasonClose = seasonClose;
        this.seasonPageCounter = seasonPageCounter;
        this.haveDied = haveDied;
        this.cannotVote = cannotVote;
        this.requestingVoteTop = requestingVoteTop;
        this.requestingVoteBottom = requestingVoteBottom;
        this.voteContinueSuccess = voteContinueSuccess;
        this.voteEndSuccess = voteEndSuccess;
        this.voteFail = voteFail;
        this.deathMessage = deathMessage;
        this.seasonEnding = seasonEnding;
        this.seasonGenerating = seasonGenerating;
        this.playerResurrected = playerResurrected;
        this.seasonInfo = seasonInfo;
    }

    public String getLocalized(LocalizationKey key) {
        return switch (key) {
            case CONFIG_RELOADED -> translateAlternateColorCodes(configReloaded);
            case MUST_BE_A_PLAYER -> translateAlternateColorCodes(mustBeAPlayer);
            case NO_PERMISSION -> translateAlternateColorCodes(noPermission);
            case INVALID_COMMAND -> translateAlternateColorCodes(invalidCommand);
            case LOADING_REWARDS -> translateAlternateColorCodes(loadingRewards);
            case REWARD_GO_BACK -> translateAlternateColorCodes(rewardGoBack);
            case REWARD_PREVIOUS -> translateAlternateColorCodes(rewardPrevious);
            case REWARD_PAGE -> translateAlternateColorCodes(rewardPage);
            case REWARD_NEXT -> translateAlternateColorCodes(rewardNext);
            case REWARD_CLOSE -> translateAlternateColorCodes(rewardClose);
            case REWARD_PAGE_COUNTER -> translateAlternateColorCodes(rewardPageCounter);
            case INVENTORY_FULL -> translateAlternateColorCodes(inventoryFull);
            case LOADING_SEASONS -> translateAlternateColorCodes(loadingSeasons);
            case SELECT_SEASON_TITLE -> translateAlternateColorCodes(selectSeasonTitle);
            case SEASON_ITEM_NAME -> translateAlternateColorCodes(seasonItemName);
            case SEASON_PREVIOUS -> translateAlternateColorCodes(seasonPrevious);
            case SEASON_NEXT -> translateAlternateColorCodes(seasonNext);
            case SEASON_CLOSE -> translateAlternateColorCodes(seasonClose);
            case SEASON_PAGE_COUNTER -> translateAlternateColorCodes(seasonPageCounter);
            case HAVE_DIED -> translateAlternateColorCodes(haveDied);
            case CANNOT_VOTE -> translateAlternateColorCodes(cannotVote);
            case REQUESTING_VOTE_TOP -> translateAlternateColorCodes(requestingVoteTop);
            case REQUESTING_VOTE_BOTTOM -> translateAlternateColorCodes(requestingVoteBottom);
            case VOTE_CONTINUE_SUCCESS -> translateAlternateColorCodes(voteContinueSuccess);
            case VOTE_END_SUCCESS -> translateAlternateColorCodes(voteEndSuccess);
            case VOTE_FAIL -> translateAlternateColorCodes(voteFail);
            case DEATH_MESSAGE -> translateAlternateColorCodes(deathMessage);
            case SEASON_ENDING -> translateAlternateColorCodes(seasonEnding);
            case SEASON_GENERATING -> translateAlternateColorCodes(seasonGenerating);
            case PLAYER_RESURRECTED -> translateAlternateColorCodes(playerResurrected);
            case SEASON_INFO -> String.join("\n", translateAlternateColorCodes(seasonInfo));
        };
    }

    private String translateAlternateColorCodes(String text) {
        String internalPlaceholderParsed = plugin.placeholderManager.getPlaceholderValue(text);
        return ChatColor.translateAlternateColorCodes('&', internalPlaceholderParsed);
    }

    private List<String> translateAlternateColorCodes(List<String> text) {
        List<String> translated = new ArrayList<>();

        for (String line : text) {
            translated.add(translateAlternateColorCodes(line));
        }

        return translated;
    }
}

