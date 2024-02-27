# Hardcore Seasons
Current Version: 1.0.0

## Description
This plugin adds automation and rewards to hardcore, or limited-life servers in Minecraft. It creates the possibility of transferring "
Inventories" from one server, directly to another. The goal of this plugin is to encourage more players to try Hardcore, and 
takes some inspiration from PubG or similar games where only one can become the victor.

Players will be able to join the server, and play as normal. Any inventory blocks that are placed, such as `chests`, `shulkers`, `enderchests`, 
`barrels`, etc. will be automatically saved to a database. When all players have died except the configured number of survivors, the server will
automatically reset the world, and player inventories will be available for "Reward Claiming". Any players on a connected server, regardless of 
game mode, will be able to use the command `/hcclaim` to claim the rewards from the previous season. The rewards that will be claimed are the 
entirety of all inventories that were saved, from all players who participated in the season. 

Stacked items, such as `DIAMOND` or `DIRT` for example, will be evenly split, between multiple survivors if it's configured in the 
`pluginConfig.yml` file. In such cases where the number of items cannot be evenly split, the remainder will be distributed to the first survivor
in the list, based on survival time, and activity time. 

### Default Config
```yaml
# Set to true for the seasonal server.
# Set to false for the non-seasonal server.
# The non-seasonal server will be capable of receiving rewards.
seasonalServer: true

# This is the minimum length for the season.
# End of season rewards, world will not be reset until this number of days has elapsed.
minSeasonLength: 14 #Default is 14

# This is the maximum length for the season.
# World will be reset, player rewards will be split among all players.
maxSeasonLength: -1 # Default is -1, which means no maximum length.

# Whenever a season is beyond it's `minSeasonLength` and there are
# less or equal to `maxSurvivorsRemaining` players, the season will end.
# World will be reset, player rewards will be split among all players.
maxSurvivorsRemaining: 1

# This is the minimum number of votes required to end the season.
# This only applies if there are more than 1 `maxSurvivorsRemaining` players.
minVotesToEndSeason: 5

# This is the number of days a player can be offline before they are considered inactive.
# Inactive players will not be counted towards the `maxSurvivorsRemaining` count.
# Inactive players will not receive seasonal rewards.
lastLoginThreshold: 3 # In days

# This is the frequency for which the plugin will prompt players to
# vote to end the season, or continue the season.
# Always applies, except when the last surviving player(s) falls off due to inactivity.
confirmationIntervalDays: 0 # In days
confirmationIntervalHours: 24 # In hours
confirmationIntervalMinutes: 0 # In minutes

endOfSeasonCommands:
  - "say Season has ended! The world will be reset and rewards will be split among all players."
  - "server [playerName] survival"

# Storage Settings
# Not Recommended: SQLite
# It will require manual intervention to reset the database.
# In addition to manual intervention, to reward players, you will need to move the database file to the server
# and inform players when it is acceptable to run the command.
storageType: "SQLite"

# Recommended: MySQL
# The entire process is fully automated, and players will be rewarded whenever the season ends.
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