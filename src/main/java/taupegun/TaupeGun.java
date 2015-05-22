package taupegun;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;

/**
 * Classe principale
 *
 * @author Guillaume
 */
public class TaupeGun extends JavaPlugin {

    private ScoreboardManager scoreboard_manager;
    private Scoreboard scoreboard;
    private Objective vie;

    /**
     * Initialisation du plugin
     */
    @Override
    public void onEnable() {
        System.out.println("+------------- Plugin Taupe Gun -------------+");
        this.scoreboard_manager = Bukkit.getScoreboardManager();
        this.scoreboard = this.scoreboard_manager.getMainScoreboard();

        // On récupère la configuration du plugin
        this.recupConfig();
        
        // Test si les coeurs sont affichés sur le scoreboard
        if (this.scoreboard.getObjective("Vie") == null) {
            this.vie = this.scoreboard.registerNewObjective("Vie", "health");
            this.vie.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        }
        
        // On test si le monde existe
        if(getServer().getWorld(getConfig().getString("world")) == null) {
            System.out.println("Le monde n'existe pas : " + getConfig().getString("world"));
            return;
        }
        
        // On place la bordure
        getServer().getWorld(getConfig().getString("world")).getWorldBorder().setSize(getConfig().getDouble("worldborder.size"));
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return false;
    }

    public void recupConfig() {
        getConfig().addDefault("lobby.world", "lobby");
        getConfig().addDefault("lobby.X", Integer.valueOf(0));
        getConfig().addDefault("lobby.Y", Integer.valueOf(100));
        getConfig().addDefault("lobby.Z", Integer.valueOf(0));
        getConfig().addDefault("world", "world");
        getConfig().addDefault("world_nether", "world_nether");
        getConfig().addDefault("world_end", "world_end");
        getConfig().addDefault("worldborder.taille", Integer.valueOf(1500));
        getConfig().addDefault("worldborder.taillemeetup", Integer.valueOf(100));
        getConfig().addDefault("worldborder.minreduction", Integer.valueOf(60));
        getConfig().addDefault("potions.glowstoneautorise", Boolean.valueOf(false));
        getConfig().addDefault("potions.regeneration", Boolean.valueOf(false));
        getConfig().addDefault("potions.strength", Boolean.valueOf(false));
        getConfig().addDefault("options.secinvulnerable", Integer.valueOf(30));
        getConfig().addDefault("options.eternalday", Boolean.valueOf(true));
        getConfig().addDefault("options.pvptime", Integer.valueOf(20));
        getConfig().addDefault("options.playersperteam", Integer.valueOf(4));
        getConfig().addDefault("options.settaupesafter", Integer.valueOf(20));
        getConfig().addDefault("options.minplayers", Integer.valueOf(20));
        getConfig().addDefault("options.cooldown", Boolean.valueOf(false));

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

}
