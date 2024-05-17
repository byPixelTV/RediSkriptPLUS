package net.limework.rediskript.skript.elements;

import ch.njol.skript.lang.SkriptParser;
import net.limework.rediskript.RediSkript;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

import javax.annotation.Nullable;
import java.util.List;

public class ExprGetRedisList extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprGetRedisList.class, String.class, ExpressionType.SIMPLE, "redis (array|list) %string%");
    }

    private Expression<String> redisListKey;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        redisListKey = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "redis list " + redisListKey.toString(event, debug);
    }

    @Override
    @Nullable
    protected String[] get(Event event) {
        RediSkript plugin = RediSkript.getAPI();

        String redisListName = redisListKey.getSingle(event);
        if (redisListName != null) {
            return plugin.getRC().getList(redisListName).toArray(new String[0]);
        }
        return null;
    }
}