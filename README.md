# Hardcore Seasons Minecraft Plugin
- Current Version: 0.0.1a
- _Requires Spigot, Paper, or Bukkit 1.20.X_
- _Release Date: TBD_

Welcome to Hardcore Seasons! This triumphant plugin takes hardcore servers in Minecraft and kicks the thrilling experience up a notch. With an exciting new layer of automation seasonRewards, this plugin is inspired by the immersive playstyle of battle royales games like PubG, Warzone, or Fortnite but with a quintessential async Minecraft twist.

## Feature Overview

Hardcore Seasons enables your server to host engaging survival games. Throughout the game, participants will amass inventories filled with crafted and scavenged goods in typical fashion. These include all inventory blocks such as chests, shulker boxes, ender chests, barrels, and more.

But here is where the fun begins: Each and every inventory that is kept by participants, down to the last diamond or scrap of dirt, is automatically safeguarded in a dedicated database. You heard it right — all loot, treasures, and materials become potential seasonRewards.

Once the survival game concludes, leaving a predetermined number of victors, the world automatically resets. All those saved inventories? They then become available for claiming as seasonRewards. Players across any connected server can unlock these abundant seasonRewards from the previous gaming season with a simple "/hcclaim" command.

## Item Distribution

To make things even more thrilling, the plugin features a fair and balanced item distribution system. Loot like DIAMONDS or DIRT that has been stacked can be configured in the pluginConfig.yml file to evenly split among participants.

But what to do when the loot count doesn't divide evenly? No worries, in those cases, the remaining items will be distributed to the top-tier participant based on survival duration and activity. It indeed adds an extra incentive for participants to strive to outlive and outlast others.

## Encouraging Hardcore Play
Hardcore Seasons is tailored to stimulate and augment the hardcore gameplay experience. A mix of competition, thrill, and lucrative seasonRewards — this plugin balances challenge with accessibility, promising a captivating experience that lures participants back, time and time again. Embrace the hardcore experience like never before! So, gear up and get ready to claim your seasonRewards with Hardcore Seasons!

# Default Config
```yaml
# Enables /season claim command for this server.
# This command will allow players to claim rewards
# from previous seasons. If you're using MySQL, then
# the rewards can be claimed on any server running this
# plugin.
seasonalServer: true # Default | true

# This is the minimum length for the season.
# Season cannot end before this length of time
# unless there are no players alive.
minSeasonLength: 14 #Default | 14

# This is the maximum length for the season.
# Season will end if it reaches this length of time.
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

# This is the interval that Hardcore Seasons
# will notify players to vote to end the season,
# or to continue the season whenever minSeasonLength
# and maxSurvivorsRemaining conditions are met.
confirmationIntervalDays: 0 # Default | 0 (Days)
confirmationIntervalHours: 24 # Default | 24 (Hours)
confirmationIntervalMinutes: 0 # Default | 0 (Minutes)

# These commands will be executed when the season ends.
# The commands will only be executed on the non-seasonal server.
endOfSeasonCommands:
  - "say Season has ended! The world will be reset and rewards will be split among all players."
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