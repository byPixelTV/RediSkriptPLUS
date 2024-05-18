package net.limework.rediskript.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import net.limework.rediskript.RediSkript;
import org.bukkit.event.Event;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExprGetRedisString extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprGetRedisString.class, String.class, ExpressionType.SIMPLE, "[value of] redis string %string%");
    }

    private Expression<String> stringName;

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        stringName = (Expression<String>) exprs[0];
        return true;
    }

    @Override
    @Nonnull
    public String toString(@Nullable Event event, boolean debug) {
        return "value of " + stringName.toString(event, debug);
    }

    @Override
    protected String[] get(Event event) {
        RediSkript plugin = RediSkript.getAPI();

        String name = stringName.getSingle(event);

        if (name == null) {
            return null;
        }
        String stringValue = plugin.getRC().getString(name);
        if (stringValue != null) {
            return new String[]{stringValue};
        } else {
            return null;
        }
    }
}