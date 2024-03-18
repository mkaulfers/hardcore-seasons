package us.mkaulfers.hardcoreseasons.guis;

import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.ComponentSidebarLayout;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.NotNull;
import us.mkaulfers.hardcoreseasons.HardcoreSeasons;
import us.mkaulfers.hardcoreseasons.models.Season;
import us.mkaulfers.hardcoreseasons.utils.ConversionUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class InfoSidebar {
    private final HardcoreSeasons plugin;
    public final Sidebar sidebar;
    private ComponentSidebarLayout layout;

    public InfoSidebar(@NotNull HardcoreSeasons plugin) {
        this.plugin = plugin;

        try {
            ScoreboardLibrary scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
            this.sidebar = scoreboardLibrary.createSidebar();
        } catch (NoPacketAdapterAvailableException e) {
            throw new RuntimeException(e);
        }

        // Initialize layout with empty content, as real content will be added in tick()
        this.layout = new ComponentSidebarLayout(SidebarComponent.staticLine(Component.text("")), SidebarComponent.builder().build());
        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 0, 40);
    }

    public void tick() {
        plugin.db.generatePlaceholderStats();

        List<String> lines = plugin.configManager.localization.seasonInfo;
        List<Component> parsedLines = new ArrayList<>();

        for (String line : lines) {
            String parsedLine = plugin.placeholderManager.getPlaceholderValue(line);
            // Directly translate color codes and replace countdown placeholders
            parsedLine = ChatColor.translateAlternateColorCodes('&', replaceCountdownPlaceholders(parsedLine));
            parsedLines.add(Component.text(parsedLine));
        }

        SidebarComponent.Builder builder = SidebarComponent.builder();
        // Adding title as the first component
        SidebarComponent title = SidebarComponent.staticLine(parsedLines.get(0));

        // Add remaining lines as static lines
        parsedLines.subList(1, parsedLines.size()).forEach(builder::addStaticLine);

        SidebarComponent component = builder.build();
        // Use the title and component as required for your layout
        this.layout = new ComponentSidebarLayout(title, component);

        layout.apply(sidebar);
    }

    private String replaceCountdownPlaceholders(String line) {
        if (line.contains("{voting_countdown}")) {
            line = line.replace("{voting_countdown}", getVotingCountdownText());
        }
        if (line.contains("{ending_countdown}")) {
            line = line.replace("{ending_countdown}", getEndingCountdownText());
        }
        return line;
    }

    private String getVotingCountdownText() {
        Season season = plugin.activeSeason;
        Timestamp softEndDate = season.getSoftEndDate();
        long timeLeft = softEndDate.getTime() - System.currentTimeMillis();
        return formatTimeLeft(timeLeft);
    }

    private String getEndingCountdownText() {
        if (plugin.configManager.config.maxSeasonLength <= -1) {
            return "--";
        } else {
            Timestamp nextVote = new Timestamp(System.currentTimeMillis() + ConversionUtils.daysToMillis(plugin.configManager.config.maxSeasonLength));
            long timeLeft = nextVote.getTime() - System.currentTimeMillis();
            return formatTimeLeft(timeLeft);
        }
    }

    private String formatTimeLeft(long timeLeft) {
        if (timeLeft < 0) {
            return "0d 0h 0m 0s"; // Handle case where the countdown has completed
        }
//        long seconds = timeLeft / 1000 % 60;
        long minutes = timeLeft / (1000 * 60) % 60;
        long hours = timeLeft / (1000 * 60 * 60) % 24;
        long days = timeLeft / (1000 * 60 * 60 * 24);
        return days + "d " + hours + "h " + minutes + "m ";
    }
}
