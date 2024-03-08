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
# Set to true for the seasonal server.
# Set to false for the non-seasonal server.
# The non-seasonal server will be capable of receiving seasonRewards.
seasonalServer: true

# This is the minimum length for the season.
# End of season seasonRewards, world will not be reset until this number of days has elapsed.
minSeasonLength: 14 #Default is 14

# This is the maximum length for the season.
# World will be reset, participant seasonRewards will be split among all participants.
maxSeasonLength: -1 # Default is -1, which means no maximum length.

# Whenever a season is beyond it's `minSeasonLength` and there are
# less or equal to `maxSurvivorsRemaining` participants, the season will end.
# World will be reset, participant seasonRewards will be split among all participants.
maxSurvivorsRemaining: 1

# This is the minimum number of votes required to end the season.
# This only applies if there are more than 1 `maxSurvivorsRemaining` participants.
minVotesToEndSeason: 5

# This is the number of days a participant can be offline before they are considered inactive.
# Inactive participants will not be counted towards the `maxSurvivorsRemaining` count.
# Inactive participants will not receive seasonal seasonRewards.
lastLoginThreshold: 3 # In days

# This is the frequency for which the plugin will prompt participants to
# vote to end the season, or continue the season.
# Always applies, except when the last surviving participant(s) falls off due to inactivity.
confirmationIntervalDays: 0 # In days
confirmationIntervalHours: 24 # In hours
confirmationIntervalMinutes: 0 # In minutes

endOfSeasonCommands:
  - "say Season has ended! The world will be reset and seasonRewards will be split among all participants."
  - "server [playerName] survival"

# Storage Settings
# Not Recommended: SQLite
# It will require manual intervention to reset the database.
# In addition to manual intervention, to seasonReward participants, you will need to move the database file to the server
# and inform participants when it is acceptable to run the command.
storageType: "SQLite"

# Recommended: MySQL
# The entire process is fully automated, and participants will be rewarded whenever the season ends.
# Worlds will be reset automatically.
MySQL:
  host: 0.0.0.0
  port: 3306
  database: 'HCSeasons'
  username: ''
  password: ''
  # Determines the frequency for which the plugin will update the database.
  updateInterval: 5 # In minutes
```