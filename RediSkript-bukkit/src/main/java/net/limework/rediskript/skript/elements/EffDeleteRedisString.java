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

public class EffDeleteRedisString extends Effect {

    static {
        Skript.registerEffect(EffDeleteRedisString.class, "delete redis string %string%");
    }


    private Expression<String> stringName;


    @Override
    protected void execute(Event event) {

        RediSkript plugin = RediSkript.getAPI();

        String name = this.stringName.getSingle(event);
        if (name == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cRedis string name was empty. Please check your code."));
            return;
        }
        plugin.getRC().deleteString(name);
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "delete redis string " + stringName.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] expressions, final int matchedPattern, final Kleenean isDelayed, final SkriptParser.ParseResult parser) {
        this.stringName = (Expression<String>) expressions[0];
        return true;
    }

}