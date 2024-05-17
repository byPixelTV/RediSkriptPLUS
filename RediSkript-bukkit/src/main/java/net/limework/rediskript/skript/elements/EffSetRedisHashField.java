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

public class EffSetRedisHashField extends Effect {
    static {
        Skript.registerEffect(EffSetRedisHashField.class, "set field %string% to %string% in redis (hash|value) %string%");
    }


    private Expression<String> fieldName;
    private Expression<String> fieldValue;
    private Expression<String> hashKey;


    @Override
    protected void execute(Event event) {

        RediSkript plugin = RediSkript.getAPI();

        String name = this.fieldName.getSingle(event);
        String value = this.fieldValue.getSingle(event);
        String hash = this.hashKey.getSingle(event);
        if (hash == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cRedis hash key was empty. Please check your code."));
            return;
        }
        if (value == null) {
            Bukkit.getLogger().warning(ChatColor.translateAlternateColorCodes('&', "&2[&aRediSkript&a] &cValue was empty. Please check your code."));
            return;
        }
        plugin.getRC().setHashField(hash, name, value);
    }

    @Override
    public String toString(Event event, boolean debug) {
        return "set field " + fieldName.toString(event, debug) + " to " + fieldValue.toString(event, debug) + " in redis hash " + hashKey.toString(event, debug);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(final Expression<?>[] expressions, final int matchedPattern, final Kleenean isDelayed, final SkriptParser.ParseResult parser) {
        this.fieldName = (Expression<String>) expressions[0];
        this.fieldValue = (Expression<String>) expressions[1];
        this.hashKey = (Expression<String>) expressions[2];
        return true;
    }

}