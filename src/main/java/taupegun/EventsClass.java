package taupegun;

import org.bukkit.event.Listener;

/**
 * Class events
 * @author Guillaume
 */
public class EventsClass implements Listener {

    static TaupeGun plugin;

    /**
     * Constructeur
     * @param taupeGun 
     */
    public EventsClass(TaupeGun taupeGun) {
        plugin = taupeGun;
    }

}