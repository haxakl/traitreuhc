package taupegun;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
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
    private ArrayList<UUID> taupes = new ArrayList();
    private ArrayList<UUID> taupesClaim = new ArrayList();
    private ArrayList<UUID> taupesReveles = new ArrayList();
    private HashMap<UUID, Integer> taupesId = new HashMap();
    private int taupeId;
    private static World monde;

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

        // On récupère le monde
        monde = getServer().getWorld(getConfig().getString("world"));

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

        // On place la bordure
        monde.getWorldBorder().setSize(getConfig().getDouble("worldborder.taille"));

        // Création du spawn
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                TaupeGun.monde.getBlockAt(-15 + i, 100, -15 + j).setType(Material.STAINED_GLASS);

                TaupeGun.monde.getBlockAt(-15, 101, -15 + j).setType(Material.STAINED_GLASS);
                TaupeGun.monde.getBlockAt(-15, 102, -15 + j).setType(Material.STAINED_GLASS);
                TaupeGun.monde.getBlockAt(-15, 103, -15 + j).setType(Material.STAINED_GLASS);

                TaupeGun.monde.getBlockAt(15, 101, -15 + j).setType(Material.STAINED_GLASS);
                TaupeGun.monde.getBlockAt(15, 102, -15 + j).setType(Material.STAINED_GLASS);
                TaupeGun.monde.getBlockAt(15, 103, -15 + j).setType(Material.STAINED_GLASS);

                TaupeGun.monde.getBlockAt(-15 + i, 101, -15).setType(Material.STAINED_GLASS);
                TaupeGun.monde.getBlockAt(-15 + i, 102, -15).setType(Material.STAINED_GLASS);
                TaupeGun.monde.getBlockAt(-15 + i, 103, -15).setType(Material.STAINED_GLASS);

                TaupeGun.monde.getBlockAt(-15 + i, 101, 15).setType(Material.STAINED_GLASS);
                TaupeGun.monde.getBlockAt(-15 + i, 102, 15).setType(Material.STAINED_GLASS);
                TaupeGun.monde.getBlockAt(-15 + i, 103, 15).setType(Material.STAINED_GLASS);
            }
        }

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

            // Test la commande
            switch (cmd.getName()) {

                // Chat entre taupe
                case "t":
                    if (this.taupes.contains(player.getUniqueId())) {
                        for (UUID taupe : this.taupes) {
                            String message = StringUtils.join(args, ' ', 0, args.length);

                            Bukkit.getPlayer(taupe).sendMessage(ChatColor.GOLD + "(Taupes) " + ChatColor.RED + "<" + player.getName() + "> " + ChatColor.WHITE + message);
                        }
                    }
                    return true;

                // Chat entre taupe
                case "alives":
                    player.sendMessage(ChatColor.GREEN + " * Liste des personnages");
                    for (UUID alive : Events.getAlive()) {
                        player.sendMessage(getServer().getPlayer(alive).getName());
                    }
                    return true;

                // Lancement de la game
                case "start":
                    if (player.isOp()) {
                        startgame();
                        return true;
                    }

                    break;

                // Claim
                case "claim":

                    // On test si le joueur est une taupe
                    if (this.taupes.contains(player.getUniqueId()) && !taupesClaim.contains(player.getUniqueId())) {
                        ArrayList<ItemStack> items = new ArrayList<>();
                        items.add(new ItemStack(Material.GOLDEN_APPLE, 2));

                        ItemStack bookFireAspect = new ItemStack(Material.ENCHANTED_BOOK, 1);
                        EnchantmentStorageMeta metaFire = (EnchantmentStorageMeta) bookFireAspect.getItemMeta();
                        metaFire.addStoredEnchant(Enchantment.FIRE_ASPECT, 1, true);
                        bookFireAspect.setItemMeta(metaFire);

                        ItemStack bookFlameAspect = new ItemStack(Material.ENCHANTED_BOOK, 1);
                        EnchantmentStorageMeta metaFlame = (EnchantmentStorageMeta) bookFlameAspect.getItemMeta();
                        metaFlame.addStoredEnchant(Enchantment.ARROW_FIRE, 1, true);
                        bookFlameAspect.setItemMeta(metaFlame);

                        items.add(bookFireAspect);
                        items.add(bookFlameAspect);

                        Random rand = new Random();

                        player.getInventory().addItem(items.get(rand.nextInt(items.size())));

                        taupesClaim.add(player.getUniqueId());
                    }

                    return true;

                // La taupe se révèle
                case "reveal":
                    if (this.taupes.contains(player.getUniqueId())) {
                        if (this.taupesReveles.contains(player.getUniqueId())) {
                            player.sendMessage(ChatColor.RED
                                    + "Vous vous êtes déjà révélé !");
                        } else {
                            if (this.scoreboard.getPlayerTeam(player).getSize() == 1) {
                                this.scoreboard.getPlayerTeam(player).unregister();
                            }
                            this.taupesteam.addPlayer(player);
                            this.taupesReveles.add(player.getUniqueId());
                            for (Player online : Bukkit.getOnlinePlayers()) {
                                online.sendMessage(ChatColor.RED + player.getName()
                                        + " a révélé qu'il était une taupe !");
                                online.playSound(online.getLocation(), Sound.GHAST_SCREAM, 10.0F, -10.0F);
                            }
                        }
                        return true;
                    }
                    return true;

                // Liste des mondes
                case "worlds":
                    if (player.isOp()) {
                        // Liste des mondes
                        player.sendMessage(ChatColor.GREEN + " * Liste des mondes vue par bukkit");
                        for (World world : getServer().getWorlds()) {
                            player.sendMessage(world.getName());
                        }
                        return true;
                    }
                    break;

                // Liste des taupes
                case "taupes":

                    // test si le joueur est en spec
                    if (player.getGameMode() == GameMode.SPECTATOR) {
                        player.sendMessage(ChatColor.GREEN + " * Liste des taupes");

                        for (UUID taupe : taupes) {
                            player.sendMessage(getServer().getPlayer(taupe).getName());
                        }

                    }

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
        getConfig().addDefault("world", "lobby");
        getConfig().addDefault("lobby.X", Integer.valueOf(0));
        getConfig().addDefault("lobby.Y", Integer.valueOf(100));
        getConfig().addDefault("lobby.Z", Integer.valueOf(0));
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

        // On définit les positions
        this.l1 = new Location(monde, 500.0D, 260.0D, 500.0D);
        this.l2 = new Location(monde, 500.0D, 260.0D, -500.0D);
        this.l3 = new Location(monde, -500.0D, 260.0D, -500.0D);
        this.l4 = new Location(monde, -500.0D, 260.0D, 500.0D);
        this.l5 = new Location(monde, 50.0D, 260.0D, -50.0D);

        // Informations du monde
        monde.setGameRuleValue("doDaylightCycle", Boolean.toString(getConfig().getBoolean("options.eternalday")));
        monde.setStorm(false);
        monde.setThundering(false);
        monde.setTime(5000L);
        monde.setWeatherDuration(99999);

        // On met la bordure
        monde.getWorldBorder().setSize(getConfig().getDouble("worldborder.taille"));

        // On enlève les équipes vides
        for (Team teams : this.scoreboard.getTeams()) {
            if ((teams.getSize() == 0) && (!teams.getName().equalsIgnoreCase("Taupes"))) {
                teams.unregister();
            }
        }

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

            // On teste la team du joueur
            if (this.scoreboard.getPlayerTeam(p) == null) {
                p.setGameMode(GameMode.SPECTATOR);
                p.teleport(new Location(monde, 0, 100, 0));
            } else {
                switch (this.scoreboard.getPlayerTeam(p).getName()) {

                    // Rose
                    case "rose":
                        p.teleport(this.l1);
                        break;

                    // Jaune
                    case "jaune":
                        p.teleport(this.l2);
                        break;

                    // Violette
                    case "violette":
                        p.teleport(this.l3);
                        break;

                    // Cyan
                    case "cyan":
                        p.teleport(this.l4);
                        break;

                    // Verte
                    case "verte":
                        p.teleport(this.l5);
                        break;

                }

                // On ajoute l'anti dommage
                p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * getConfig().getInt("options.secinvulnerable"), 4));
            }

            p.setScoreboard(this.scoreboard);

            Events.addPlayer(p.getUniqueId());
        }

        // Tâches asynchrone
        this.tachesAsynchrone();
    }

    /**
     * Fire
     */
    public void finalfireworks() {
        BukkitTask fireworks = new BukkitRunnable() {
            public void run() {
                for (UUID players : Events.getAlive()) {
                    Events.summonFireWork(Bukkit.getPlayer(players));
                }
            }
        }.runTaskTimer(this, 0L, 4L);
        BukkitTask fireworks2 = new BukkitRunnable() {
            public void run() {
                Bukkit.getScheduler().cancelAllTasks();
            }
        }.runTaskLater(this, 120L);
    }

    /**
     * Retourne la liste des taupes
     *
     * @return
     */
    public ArrayList<UUID> getTaupes() {
        return taupes;
    }

    /**
     * Regroupe toutes les tâches
     */
    public void tachesAsynchrone() {

        // Réduction de la map
        BukkitTask reducMap = new BukkitRunnable() {
            public void run() {
                monde.getWorldBorder().setSize(TaupeGun.this.getConfig().getDouble("worldborder.taillemeetup"), 1200L);
                monde.getWorldBorder().setWarningDistance(2);
                monde.getWorldBorder().setWarningTime(10);
                Bukkit.broadcastMessage(ChatColor.RED + "Le Worldboarder se rétrécit, rapprochez vous du centre !");
                for (Player p : Bukkit.getOnlinePlayers()) {
                    p.sendMessage("Rapprochez-vous du centre");
                }
            }
        }.runTaskLater(this, 1200 * (getConfig().getInt("worldborder.minreduction")));

        // Révélation des taupes
        BukkitTask taupesAfter = new BukkitRunnable() {
            @Override
            public void run() {
                for (Team team : TaupeGun.this.scoreboard.getTeams()) {
                    if (team.getPlayers().size() >= 1) {
                        List<UUID> players = new ArrayList();
                        for (OfflinePlayer player2 : team.getPlayers()) {
                            players.add(player2.getUniqueId());
                        }
                        Random random = new Random();
                        int psize = random.nextInt(players.size());
                        UUID p = (UUID) players.get(psize);
                        Player p2 = Bukkit.getPlayer(p);
                        TaupeGun.this.taupes.add(p);
                        TaupeGun.this.taupeId += 1;
                        TaupeGun.this.taupesId.put(p, Integer.valueOf(TaupeGun.this.taupeId));

                        p2.sendMessage(ChatColor.RED + "-------Annonce IMPORTANTE------");
                        p2.sendMessage(ChatColor.GOLD + "Vous êtes la taupe de votre équipe !");
                        p2.sendMessage(ChatColor.GOLD + "Pour parler avec les autres taupes executez la commande /t < message>");
                        p2.sendMessage(ChatColor.GOLD + "Si vous voulez dévoiler votre vraie identité executez la commande /reveal");
                        p2.sendMessage(ChatColor.GOLD + "Votre but : " + ChatColor.DARK_RED + "Tuer les membres de votre \"équipe\"");
                        p2.sendMessage(ChatColor.RED + "-------------------------------");
                    }
                }

                Bukkit.broadcastMessage(ChatColor.RED + "Les taupes ont été révélées");
            }
        }.runTaskLater(this, 1200 * getConfig().getInt("options.settaupesafter"));

        // Timers scoreboard
        BukkitTask timersScoreboard = new BukkitRunnable() {

            // Episode
            int minutes = 20;
            int seconds = 0;

            // Meet up
            int minutesBefMeetUp = getConfig().getInt("worldborder.minreduction");
            int secondesBefMeetUp = 0;

            // Format
            NumberFormat formatter = new DecimalFormat("00");

            // Lancement du thread
            public void run() {
                if (TaupeGun.this.scoreboard.getTeams().size() <= 1) {
                    for (Team lastteam : TaupeGun.this.scoreboard.getTeams()) {
                        Bukkit.broadcastMessage("L'équipe "
                                + lastteam.getPrefix() + lastteam.getName()
                                + ChatColor.RESET + " a gagné ! ");

                        Bukkit.getScheduler().cancelAllTasks();
                        TaupeGun.this.finalfireworks();
                    }
                }

                // Reset du timer
                TaupeGun.this.scoreboard.resetScores(ChatColor.BLUE.toString() + "Next episode: " + ChatColor.GRAY.toString() + formatter.format(this.minutes) + ":" + formatter.format(this.seconds));
                TaupeGun.this.scoreboard.resetScores(ChatColor.BLUE.toString() + "Meet up: " + ChatColor.GRAY.toString() + formatter.format(this.minutesBefMeetUp) + ":" + formatter.format(this.secondesBefMeetUp));

                int teams = TaupeGun.this.scoreboard.getTeams().size();
                TaupeGun.this.scoreboard.resetScores(teams + 1 + ChatColor.GRAY.toString() + " équipes");

                // Episodes
                if (this.seconds == 0) {
                    if (this.minutes == 0) {
                        TaupeGun.this.scoreboard.resetScores(ChatColor.GRAY + "Episode " + ChatColor.WHITE + TaupeGun.this.episode);

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

                TaupeGun.this.scoreboard.getObjective(TaupeGun.this.obj.getDisplayName()).getScore(teams + ChatColor.GRAY.toString() + " équipes").setScore(-2);
                TaupeGun.this.scoreboard.getObjective(TaupeGun.this.obj.getDisplayName()).getScore(ChatColor.BLUE.toString() + "Next episode: " + ChatColor.GRAY.toString() + formatter.format(this.minutes) + ":" + formatter.format(this.seconds)).setScore(-4);

                // Meet up
                if (this.secondesBefMeetUp != 0 || this.minutesBefMeetUp != 0) {
                    if (this.secondesBefMeetUp == 0) {
                        this.minutesBefMeetUp--;
                        this.secondesBefMeetUp = 59;
                    } else {
                        this.secondesBefMeetUp--;
                    }

                    TaupeGun.this.scoreboard.getObjective(TaupeGun.this.obj.getDisplayName()).getScore(ChatColor.BLUE.toString() + "Meet up: " + ChatColor.GRAY.toString() + formatter.format(this.minutesBefMeetUp) + ":" + formatter.format(this.secondesBefMeetUp)).setScore(-5);
                }

            }
        }.runTaskTimer(this, 0L, 20L);

        // Activer le pvp
        BukkitTask activerPvp = new BukkitRunnable() {
            @Override
            public void run() {
                Events.activerPvp();
                Bukkit.broadcastMessage(ChatColor.RED + "Le pvp est maintenant actif !");
            }
        }.runTaskLater(this, 1200 * getConfig().getInt("options.pvptime"));

    }

}
