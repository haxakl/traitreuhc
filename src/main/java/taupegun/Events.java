package taupegun;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Class events
 *
 * @author Guillaume
 */
public class Events implements Listener {

    private static TaupeGun plugin;
    private static boolean gameStarted = false;
    private static ArrayList<UUID> alive = new ArrayList();

    private FileConfiguration config;

    /**
     * Constructeur
     *
     * @param taupeGun
     */
    public Events(TaupeGun taupeGun) {
        plugin = taupeGun;
        config = plugin.getConfig();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {

        // Récupération du joueur
        Player p = e.getPlayer();

        // Test si la game est lancée
        if (!gameStarted) {

            // Récupération du lobby
            String lobby = config.get("lobby.world").toString();
            plugin.getServer().createWorld(new WorldCreator(lobby));
            p.teleport(new Location(Bukkit.getWorld(lobby), config.getInt("lobby.X"), config.getInt("lobby.Y"), config.getInt("lobby.Z")));

            // On clear l'inventaire et on ajoute le drapeau
            p.getInventory().clear();
            p.getInventory().setItem(0, new ItemStack(Material.BANNER, 1));
            p.setGameMode(GameMode.ADVENTURE);

            // Donne la bannière aux joueur
            ItemMeta meta1 = p.getInventory().getItem(0).getItemMeta();
            meta1.setDisplayName(ChatColor.GOLD + "Choisir son équipe");
            p.getInventory().getItem(0).setItemMeta(meta1);

            // Message de join
            e.setJoinMessage(ChatColor.BLUE + p.getName() + ChatColor.YELLOW + " a rejoint la partie  " + ChatColor.GRAY + "(" + ChatColor.YELLOW + Bukkit.getOnlinePlayers().size() + "/" + Bukkit.getMaxPlayers() + ChatColor.GRAY + ")");
        } // Sinon test si le joueur est en vie
        else if (!alive.contains(p.getUniqueId())) {
            e.setJoinMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + p.getName() + " a rejoint la partie.");
            p.setGameMode(GameMode.SPECTATOR);
            p.teleport(new Location(p.getWorld(), 0.0D, 0.0D, 0.0D));
        } // Sinon le joueur est mis en spectateur
        else {
            p.setDisplayName(plugin.getScoreBoard().getPlayerTeam(p).getPrefix() + p.getName() + plugin.getScoreBoard().getPlayerTeam(p).getSuffix());
        }
    }

    @EventHandler
    public void Regen(EntityRegainHealthEvent e) {
        if (e.getRegainReason().equals(EntityRegainHealthEvent.RegainReason.SATIATED)) {
            e.setCancelled(true);
        }
    }

    /**
     * Pas de dommage dans le lobby
     *
     * @param e
     */
    @EventHandler
    public void CancelPVP(EntityDamageEvent e) {
        if (e.getEntity().getWorld().equals(Bukkit.getWorld(config.get("lobby.world").toString()))) {
            e.setCancelled(true);
        }
    }

    /**
     * Eviter que le joueur jette son drapeau de choix des équipes
     *
     * @param e
     */
    @EventHandler
    public void CancelDrop(PlayerDropItemEvent e) {
        Player p = e.getPlayer();
        if (p.getWorld().equals(Bukkit.getWorld(config.get("lobby.world").toString()))) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void Options(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        Action a = e.getAction();
        if (((a.equals(Action.RIGHT_CLICK_AIR)) || (a.equals(Action.RIGHT_CLICK_BLOCK))) && (p.getItemInHand().getType() == Material.BANNER)) {
            if (p.getItemInHand().getItemMeta().getDisplayName().equals(ChatColor.GOLD + "Choisir son équipe")) {
                e.setCancelled(true);

                openTeamInv(p);
            }
        }
    }

    @EventHandler
    public void ChoiceTeam(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if ((e.getInventory().getName().equals(ChatColor.GOLD + " Choisir son équipe")) && (e.getCurrentItem().getType() == Material.BANNER)) {
            BannerMeta banner = (BannerMeta) e.getCurrentItem().getItemMeta();

            try {
                plugin.team1.remove(p);
                plugin.team2.remove(p);
                plugin.team3.remove(p);
                plugin.team4.remove(p);
                plugin.team5.remove(p);
            } catch (Exception localException) {

            }

            // Test la couleur du drapeau
            switch (banner.getBaseColor()) {

                case PINK:
                    plugin.team1.add(p);
                    p.sendMessage(plugin.rose.getPrefix() + "" + ChatColor.RESET + " Vous avez rejoint l'équipe Rose");
                    plugin.rose.addPlayer(p);
                    break;

                case YELLOW:
                    plugin.team2.add(p);
                    p.sendMessage(plugin.jaune.getPrefix() + "" + ChatColor.RESET + " Vous avez rejoint l'équipe Jaune");
                    plugin.jaune.addPlayer(p);
                    break;

                case PURPLE:
                    plugin.team3.add(p);
                    p.sendMessage(plugin.violette.getPrefix() + "" + ChatColor.RESET + " Vous avez rejoint l'équipe Violette");
                    plugin.violette.addPlayer(p);
                    break;

                case CYAN:
                    plugin.team4.add(p);
                    p.sendMessage(plugin.cyan.getPrefix() + "" + ChatColor.RESET + " Vous avez rejoint l'équipe Cyan");
                    plugin.cyan.addPlayer(p);
                    break;

                case GREEN:
                    plugin.team5.add(p);
                    p.sendMessage(plugin.verte.getPrefix() + "" + ChatColor.RESET + " Vous avez rejoint l'équipe Verte");
                    plugin.verte.addPlayer(p);
                    break;

            }

            p.setDisplayName(plugin.getScoreBoard().getPlayerTeam(p).getPrefix() + p.getName() + plugin.getScoreBoard().getPlayerTeam(p).getSuffix());
            e.setCancelled(true);
            openTeamInv(p);
        }
    }

    /**
     * Ajoute un item dans l'inventaire des équipes
     *
     * @param inv
     * @param ccolor
     * @param color
     * @param Name
     * @param slot
     */
    public static void addItem(Inventory inv, ChatColor ccolor, DyeColor color, String Name, int slot) {
        ItemStack team = new ItemStack(Material.BANNER);
        BannerMeta meta = (BannerMeta) team.getItemMeta();
        meta.setDisplayName(ccolor + Name);
        if (color.equals(DyeColor.PINK)) {
            List<String> lore = new ArrayList();
            for (OfflinePlayer pl : plugin.getScoreBoard().getTeam("rose").getPlayers()) {
                lore.add(ChatColor.LIGHT_PURPLE + "- " + pl.getName());
            }
            meta.setLore(lore);
        } else if (color.equals(DyeColor.YELLOW)) {
            List<String> lore = new ArrayList();
            for (OfflinePlayer pl : plugin.getScoreBoard().getTeam("jaune").getPlayers()) {
                lore.add(ChatColor.YELLOW + "- " + pl.getName());
            }
            meta.setLore(lore);
        } else if (color.equals(DyeColor.PURPLE)) {
            List<String> lore = new ArrayList();
            for (OfflinePlayer pl : plugin.getScoreBoard().getTeam("violette").getPlayers()) {
                lore.add(ChatColor.DARK_PURPLE + "- " + pl.getName());
            }
            meta.setLore(lore);
        } else if (color.equals(DyeColor.CYAN)) {
            List<String> lore = new ArrayList();
            for (OfflinePlayer pl : plugin.getScoreBoard().getTeam("cyan").getPlayers()) {
                lore.add(ChatColor.AQUA + "- " + pl.getName());
            }
            meta.setLore(lore);
        } else if (color.equals(DyeColor.GREEN)) {
            List<String> lore = new ArrayList();
            for (OfflinePlayer pl : plugin.getScoreBoard().getTeam("verte").getPlayers()) {
                lore.add(ChatColor.GREEN + "- " + pl.getName());
            }
            meta.setLore(lore);
        }
        meta.setBaseColor(color);

        team.setItemMeta(meta);
        inv.setItem(slot, team);
    }

    /**
     * Inventaire pour le choix des équipes
     *
     * @param p
     */
    public static void openTeamInv(Player p) {
        Inventory inv = Bukkit.createInventory(p, 9, ChatColor.GOLD + " Choisir son équipe");

        addItem(inv, ChatColor.LIGHT_PURPLE, DyeColor.PINK, "Equipe Rose", 0);
        addItem(inv, ChatColor.YELLOW, DyeColor.YELLOW, "Equipe Jaune", 1);
        addItem(inv, ChatColor.DARK_PURPLE, DyeColor.PURPLE, "Equipe Violette", 2);
        addItem(inv, ChatColor.AQUA, DyeColor.CYAN, "Equipe Cyan", 3);
        addItem(inv, ChatColor.GREEN, DyeColor.GREEN, "Equipe Verte", 4);

        p.openInventory(inv);
    }

}
