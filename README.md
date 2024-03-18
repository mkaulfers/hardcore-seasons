# Hardcore Seasons Minecraft Plugin
- Current Version: 1.4.1b _(Beta)_
- _Requires Spigot, Paper, or Bukkit 1.20.X_ 
_(Other versions "May" work, but have not been tested.)_

Welcome to Hardcore Seasons! This triumphant plugin takes hardcore servers in Minecraft and kicks the thrilling experience up a notch. With an exciting new layer of automation seasonal rewards, this plugin is inspired by the immersive playstyle of battle royales games like PubG, Warzone, or Fortnite but with a quintessential async Minecraft twist.

## Feature Overview

Hardcore Seasons enables your server to host engaging survival games. Throughout the game, participants will amass inventories filled with crafted and scavenged goods in typical fashion. These include all inventory blocks such as chests, shulker boxes, ender chests, barrels, and more.

But here is where the fun begins: Each and every inventory that is kept by participants, down to the last diamond or scrap of dirt, is automatically safeguarded in a dedicated database. You heard it right — all loot, treasures, and materials become potential seasonal rewards.

Once the survival game concludes, leaving a predetermined number of victors, the world automatically resets. All those saved inventories? They then become available for claiming as seasonal rewards. Players across any connected server can unlock these abundant seasonal rewards from the previous gaming season with a simple "/season claim" command.

## Item Distribution

To make things even more thrilling, the plugin features a fair and balanced item distribution system. Loot like DIAMONDS or DIRT that has been stacked can be configured in the pluginConfig.yml file to evenly split among participants.

But what to do when the loot count doesn't divide evenly? No worries, in those cases, the remaining items will be distributed to the top-tier participant based on survival duration and activity. It indeed adds an extra incentive for participants to strive to outlive and outlast others.

## Encouraging Hardcore Play
Hardcore Seasons is tailored to stimulate and augment the hardcore gameplay experience. A mix of competition, thrill, and lucrative seasonal rewards — this plugin balances challenge with accessibility, promising a captivating experience that lures participants back, time and time again. Embrace the hardcore experience like never before! So, gear up and get ready to claim your seasonal rewards with Hardcore Seasons!

## Technical Support? We've Got You Covered
- Wiki: [GitBook](https://app.gitbook.com/invite/p0BPImHuxjc0DaxRNvT8/YF5rgrN12WFlkv2ukwoV)
- Discord: https://discord.gg/mcET68gTGM

# Default Config
```yaml
# Enables /season claim command for this server.
# This command will allow players to claim rewards
# from previous seasons. If you're using MySQL, then
# the rewards can be claimed on any server running this
# plugin when this is enabled.
claimingEnabled: true # Default | true

# This enables the tracking of player actions and
# statistics. If you disable this, the plugin will
# not act as a seasonal server. It will only allow
# players to claim rewards from previous seasons
# if claimingEnabled is set to true. Otherwise,
# the plugin will not do anything.

# --------------------- WARNING ----------------------
# This setting is critical to the function of the plugin.
# ONLY enable it on a server that you want to be a seasonal server.
# For example, if you want your rewards to go to a survival server,
# from a hardcore server, then you should disable this setting on
# the survival server, and enable it on the hardcore server.
# This setting has destructive capabilities, and can delete
# player inventories, end-chests, and worlds while enabled.
trackingEnabled: true # Default | true

# Disabling this will delete previous worlds
# when the season ends. It will not delete the
# rewards or database entries. It's useful to
# save space on your server, as well as to
# enable the option to spectate the previous
# season's world, or convert to a non-seasonal
# world.
persistSeasonWorlds: true # Default | true

# This will unload the previous seasons' worlds
# when the season ends. It will not delete them.
# It's useful to disable if you want to allow
# players to spectate the previous season's world.
unloadPastSeasons: true # Default | true

# This is the minimum length for the season.
# SeasonCMD cannot end before this length of time
# unless there are no players alive.
minSeasonLength: 14 #Default | 14

# This is the maximum length for the season.
# SeasonCMD will end if it reaches this length of time.
# Rewards will be distributed among all players,
# based on maxSurvivorsRemaining and player activity.
maxSeasonLength: -1 # Default | -1 (Disabled)

# If the minSeasonLength is reached, and the player
# count is less than or equal to this value, it will
# trigger a vote to end the season. If the vote passes,
# the season will end and rewards will be distributed.
maxSurvivorsRemaining: 1 # Default | 1

# This is the minimum % of votes required to end the season.
# It will only apply if the maxSurvivorsRemaining condition is met.
minVotesToEndSeason: 50 # Default | 50 (%)

# The lastLoginThreshold is the number of days a player
# must be inactive before they are considered to have
# fallen off due to inactivity. If a player falls off,
# they will not be eligible for rewards, and their
# vote will not be counted.
lastLoginThreshold: 3 # Default | 3 (Days)

# Displays a message to vote to end the season
# when the maxSurvivorsRemaining condition is met,
# and when minSeasonLength is reached. Only
# the players who are still alive, and have not
# fallen off due to inactivity or not voted, will
# receive this message.
notificationInterval: 5 # Default | 5 (Minutes)

# This is the interval for which the plugin will
# reset a player's vote if they voted to continue
# the season. This is to prevent players from
# voting to continue the season, and then not
# logging in to vote again. This will reset their
# vote, and they will have to vote again.
voteResetInterval: 2 # Default | 1 (Days)

# These commands will be executed when the season ends.
# The commands will only be executed on the non-seasonal server.
endOfSeasonCommands:
  - "say SeasonCMD has ended! The world will be reset and rewards will be split among all players."
  - "server [playerName] survival"

# The storageType can be either SQLite or MySQL.
# SQLite will ignore the seasonalServer setting.
# You can manually move the db file to another server
# if you want to share the rewards across multiple servers.
storageType: "SQLite" # Default | SQLite

# If you're using MySQL, you must set the storageType to MySQL.
# The plugin will create the tables for you.
# If you're using SQLite, you can ignore these settings.
MySQL:
  host: 0.0.0.0
  port: 3306
  database: 'HCSeasons'
  username: ''
  password: ''
  # Determines the frequency for which the plugin will update the database.
  # Setting this to a lower value will only impact server performance slightly.
  # Most operations that this adjusts, are done asynchronously, and are not
  # critical to the function of the plugin.
  updateInterval: 5 # Default | 5 (Minutes)
```