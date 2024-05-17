package net.limework.rediskript.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import net.limework.rediskript.RediSkript;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Event;

public class EffDeleteRedisHash extends Effect {
    static {
        Skript.registerEffect(EffDeleteRedisHash.class, "delete redis (value|hash) %string%");
    }

    private Expression<String> hashKey;


    @Override
    protected void execute(Event event) {

        RediSkript plugin = RediSkript.getAPI();

        String hash = this.hashKey.getSingle(event);
        if (hash == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cRedis hash key was empty. Please check your code."));
            return;
        }
        plugin.getRC().deleteHash(hash);
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "delete redis hash " + hashKey.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] expressions, final int matchedPattern, final Kleenean isDelayed, final SkriptParser.ParseResult parser) {
        this.hashKey = (Expression<String>) expressions[0];
        return true;
    }

}