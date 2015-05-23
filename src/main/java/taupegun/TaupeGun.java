package taupegun;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

/**
 * Classe principale
 *
 * @author Guillaume
 */
public class TaupeGun extends JavaPlugin {

    private ScoreboardManager scoreboard_manager;
    private Scoreboard scoreboard;
    private Objective vie;
    private Objective obj;
    private int episode = 1;

    public ArrayList<Player> team1 = new ArrayList();
    public ArrayList<Player> team2 = new ArrayList();
    public ArrayList<Player> team3 = new ArrayList();
    public ArrayList<Player> team4 = new ArrayList();
    public ArrayList<Player> team5 = new ArrayList();

    Location l1;
    Location l2;
    Location l3;
    Location l4;
    Location l5;

    public Team rose;
    public Team jaune;
    public Team violette;
    public Team cyan;
    public Team verte;
    public Team taupesteam;

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

        // Scoreboard à droite
        Bukkit.getPluginManager().registerEvents(new Events(this), this);

        if (this.scoreboard.getObjective("TaupeGun") != null) {
            this.scoreboard.getObjective("TaupeGun").unregister();
        }

        this.obj = this.scoreboard.registerNewObjective("TaupeGun", "dummy");
        this.obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        for (Team team : this.scoreboard.getTeams()) {
            team.unregister();
        }

        // Recettes personnalisés
        ShapedRecipe craft = new ShapedRecipe(new ItemStack(Material.SPECKLED_MELON));
        craft.shape(new String[]{"***", "*x*", "***"});
        craft.setIngredient('*', Material.GOLD_INGOT);
        craft.setIngredient('x', Material.MELON);
        Bukkit.addRecipe(craft);

        ShapedRecipe tetecraft = new ShapedRecipe(new ItemStack(Material.GOLDEN_APPLE));
        tetecraft.shape(new String[]{"***", "*x*", "***"});
        tetecraft.setIngredient('*', Material.GOLD_INGOT);
        tetecraft.setIngredient('x', Material.SKULL_ITEM);
        Bukkit.addRecipe(tetecraft);

        // Liste des équipes
        this.rose = this.scoreboard.registerNewTeam("rose");
        this.jaune = this.scoreboard.registerNewTeam("jaune");
        this.violette = this.scoreboard.registerNewTeam("violette");
        this.cyan = this.scoreboard.registerNewTeam("cyan");
        this.verte = this.scoreboard.registerNewTeam("verte");
        this.rose.setPrefix(ChatColor.LIGHT_PURPLE.toString());
        this.jaune.setPrefix(ChatColor.YELLOW.toString());
        this.violette.setPrefix(ChatColor.DARK_PURPLE.toString());
        this.cyan.setPrefix(ChatColor.DARK_AQUA.toString());
        this.verte.setPrefix(ChatColor.GREEN.toString());
        this.verte.setSuffix(ChatColor.WHITE.toString());
        this.rose.setSuffix(ChatColor.WHITE.toString());
        this.jaune.setSuffix(ChatColor.WHITE.toString());
        this.violette.setSuffix(ChatColor.WHITE.toString());
        this.cyan.setSuffix(ChatColor.WHITE.toString());
        this.rose.setDisplayName(this.rose.getPrefix() + this.rose.getDisplayName() + this.rose.getSuffix());
        this.verte.setDisplayName(this.verte.getPrefix() + this.verte.getDisplayName() + this.verte.getSuffix());
        this.jaune.setDisplayName(this.jaune.getPrefix() + this.jaune.getDisplayName() + this.jaune.getSuffix());
        this.violette.setDisplayName(this.violette.getPrefix() + this.violette.getDisplayName() + this.violette.getSuffix());
        this.cyan.setDisplayName(this.cyan.getPrefix() + this.cyan.getDisplayName() + this.cyan.getSuffix());

        this.taupesteam = this.scoreboard.registerNewTeam("Taupes");
        this.taupesteam.setPrefix(ChatColor.RED.toString());
        this.taupesteam.setSuffix(ChatColor.WHITE.toString());
        this.taupesteam.setDisplayName(this.taupesteam.getPrefix() + this.taupesteam.getDisplayName() + this.taupesteam.getSuffix());

        // Liste des mondes
        System.out.println(" * Liste des mondes vue par bukkit");
        for (World world : getServer().getWorlds()) {
            System.out.println(world.getName());
        }

        // On test si le monde existe
        if (getServer().getWorld(getConfig().getString("world")) == null) {
            System.out.println("Le monde n'existe pas : " + getConfig().getString("world"));
            return;
        }

        // On place la bordure
        getServer().getWorld(getConfig().getString("world")).getWorldBorder().setSize(getConfig().getDouble("worldborder.taille"));

        // On appelle le constructeur de la classe parente
        super.onEnable();
    }

    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        // On teste si c'est un joueur qui envoie la commande
        if ((sender instanceof Player)) {
            Player player = (Player) sender;

            switch (cmd.getName()) {

                // Chat entre taupe
                case "t":

                    return true;

                // Lancement de la game
                case "start":
                    if (player.isOp()) {
                        startgame();
                        return true;
                    }

                    break;

                // La taupe se révèle
                case "reveal":

                    return true;

                // Liste des mondes
                case "worlds":

                    if (player.isOp()) {

                        // Liste des mondes
                        player.sendMessage(ChatColor.GREEN + " * Liste des mondes vue par bukkit");
                        for (World world : getServer().getWorlds()) {
                            player.sendMessage(ChatColor.GREEN + world.getName());
                        }

                        return true;
                    }

                    break;

                // Liste des taupes
                case "taupes":

                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        player.sendMessage(ChatColor.GREEN + " * Liste des taupes");

                    }

                    this.choixTaupes();

                    return true;
            }
        }

        // La commande n'a pas été trouvée
        return false;
    }

    /**
     * Configuration par défaut s'il y en a pas
     */
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

        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    /**
     * Retourne le scoreboard
     *
     * @return
     */
    public Scoreboard getScoreBoard() {
        return this.scoreboard;
    }

    /**
     * Start une game
     */
    public void startgame() {
        Events.lancement();

        // On récupère le monde
        World monde = Bukkit.getWorld(getConfig().getString("world"));

        // On définit les positions
        this.l1 = new Location(monde, 500.0D, 260.0D, 500.0D);
        this.l2 = new Location(monde, 500.0D, 260.0D, -500.0D);
        this.l3 = new Location(monde, -500.0D, 260.0D, -500.0D);
        this.l4 = new Location(monde, -500.0D, 260.0D, 500.0D);
        this.l5 = new Location(monde, 0.0D, 260.0D, 0.0D);

        // Informations du monde
        monde.setGameRuleValue("doDaylightCycle", Boolean.valueOf(getConfig().getBoolean("options.eternalday")).toString());
        monde.setStorm(false);
        monde.setThundering(false);
        monde.setTime(5000L);
        monde.setWeatherDuration(99999);

        // On choisit les taupes
        this.choixTaupes();

        // On crée le scoreboard
        this.scoreboard.getObjective(this.obj.getDisplayName()).getScore(ChatColor.GRAY + "Episode " + ChatColor.WHITE + this.episode).setScore(0);
        this.scoreboard.getObjective(this.obj.getDisplayName()).getScore(this.scoreboard.getTeams().size() + ChatColor.GRAY.toString() + " équipes").setScore(-2);
        this.scoreboard.getObjective(this.obj.getDisplayName()).getScore(" ").setScore(-3);

        // On téléporte les joueurs
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.getInventory().clear();
            p.setGameMode(GameMode.SURVIVAL);
            p.setHealth(20.0D);
            p.setFoodLevel(40);
            p.setLevel(0);
            if (this.scoreboard.getPlayerTeam(p).getName().equals("rose")) {
                p.teleport(this.l1);
            } else if (this.scoreboard.getPlayerTeam(p).getName().equals("jaune")) {
                p.teleport(this.l2);
            } else if (this.scoreboard.getPlayerTeam(p).getName().equals("violette")) {
                p.teleport(this.l3);
            } else if (this.scoreboard.getPlayerTeam(p).getName().equals("cyan")) {
                p.teleport(this.l4);
            } else if (this.scoreboard.getPlayerTeam(p).getName().equals("verte")) {
                p.teleport(this.l5);
            }
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * getConfig().getInt("options.secinvulnerable"), 4));
        }

        new BukkitRunnable() {
            int minutes = 20;
            int seconds = 0;
            
            int minutesBefMeetUp = 60;
            int secondesBefMeetUp = 0;

            public void run() {
                
                NumberFormat formatter = new DecimalFormat("00");
                String minute = formatter.format(this.minutes);
                String second = formatter.format(this.seconds);
                TaupeGun.this.scoreboard.resetScores(minute + ":" + second);
                if (this.seconds == 0) {
                    if (this.minutes == 0) {
                        TaupeGun.this.scoreboard.resetScores(ChatColor.GRAY + "Episode "
                                + ChatColor.WHITE + TaupeGun.this.episode);

                        TaupeGun.this.episode += 1;
                        Bukkit.broadcastMessage(ChatColor.AQUA + "------------- Episode " + TaupeGun.this.episode + " -------------");

                        TaupeGun.this.scoreboard.getObjective(TaupeGun.this.obj.getDisplayName()).getScore(ChatColor.GRAY + "Episode " + ChatColor.WHITE + TaupeGun.this.episode).setScore(0);

                        this.seconds = 59;
                        this.minutes = 19;
                    } else {
                        this.seconds = 59;
                        this.minutes -= 1;
                    }
                } else {
                    this.seconds -= 1;
                }
                
                if(this.secondesBefMeetUp == 0) {
                    this.minutesBefMeetUp--;
                    this.secondesBefMeetUp = 59;
                } else {
                    this.secondesBefMeetUp--;
                }
                int teams = TaupeGun.this.scoreboard.getTeams().size();
                TaupeGun.this.scoreboard.resetScores(teams + 1 + ChatColor.GRAY.toString() + " équipes");
                TaupeGun.this.scoreboard.getObjective(TaupeGun.this.obj.getDisplayName()).getScore(teams + ChatColor.GRAY.toString() + " équipes").setScore(-2);

                Object formatter2 = new DecimalFormat("00");
                String minute2 = ((NumberFormat) formatter2).format(this.minutes);
                String second2 = ((NumberFormat) formatter2).format(this.seconds);
                String minute3 = ((NumberFormat) formatter2).format(this.minutesBefMeetUp);
                String second3 = ((NumberFormat) formatter2).format(this.secondesBefMeetUp);
                TaupeGun.this.scoreboard.getObjective(TaupeGun.this.obj.getDisplayName()).getScore(minute2 + ":" + second2).setScore(-4);
                TaupeGun.this.scoreboard.getObjective(TaupeGun.this.obj.getDisplayName()).getScore(minute3 + ":" + second3).setScore(-5);
            }
        }.runTaskTimer(this, 0L, 20L);

    }

    /**
     * Choix des taupes
     */
    public void choixTaupes() {

        // Liste des joueurs et des teams
        List<UUID> players = new ArrayList();
        List<Team> teamstoshuffle = new ArrayList();

        // On parcours les teams
        for (Team teams : this.scoreboard.getTeams()) {
            if (!teams.getName().equals("Taupes") && teams.getSize() > 0) {
                teamstoshuffle.add(teams);
            }
        }

        // On shuffle les collections
        Collections.shuffle(teamstoshuffle);
        Collections.shuffle(players);

        // On parcourt les teams
        Random rand = new Random();
        for (Team team : teamstoshuffle) {
            Object[] tmp = team.getPlayers().toArray();
            Player player = (Player) tmp[rand.nextInt(team.getSize())];

            player.sendMessage(ChatColor.RED + "-------Annonce IMPORTANTE------");
            player.sendMessage(ChatColor.GOLD + "Vous êtes la taupe de votre équipe !");
            player.sendMessage(ChatColor.GOLD + "Pour parler avec les autres taupes executez la commande /t < message>");
            player.sendMessage(ChatColor.GOLD + "Si vous voulez dévoiler votre vraie identité executez la commande /reveal");
            player.sendMessage(ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les membres de votre \"équipe\"");
            player.sendMessage(ChatColor.RED + "-------------------------------");
        }
    }

}
