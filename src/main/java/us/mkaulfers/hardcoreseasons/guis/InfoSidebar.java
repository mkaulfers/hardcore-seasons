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

        Bukkit.getScheduler().runTaskTimer(plugin, this::tick, 0, 100);
    }

    public void tick() {
        plugin.db.generatePlaceholderStats();

        List<String> lines = plugin.configManager.localization.seasonInfo;
        List<String> parsedLines = new ArrayList<>();

        for (String line : lines) {
            String parsedLine = plugin.placeholderManager.getPlaceholderValue(line);
            parsedLines.add(ChatColor.translateAlternateColorCodes('&', parsedLine));
        }

        SidebarComponent.Builder builder = SidebarComponent.builder();
        SidebarComponent title = SidebarComponent.staticLine(Component.text(parsedLines.get(0)));

        for (int i = 1; i < parsedLines.size(); i++) {
            String currentLine = parsedLines.get(i);
            builder.addStaticLine(Component.text(currentLine));
        }

        SidebarComponent component = builder.build();
        this.layout = new ComponentSidebarLayout(title, component);
        layout.apply(sidebar);
    }
}
