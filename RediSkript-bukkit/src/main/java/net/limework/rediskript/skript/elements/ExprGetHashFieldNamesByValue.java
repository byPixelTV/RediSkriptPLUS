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
import java.util.Arrays;
import java.util.List;

public class ExprGetHashFieldNamesByValue extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprGetHashFieldNamesByValue.class, String.class, ExpressionType.SIMPLE, "name[s] of field with value %string% in redis (hash|value) %string%");
    }

    private Expression<String> fieldValue;
    private Expression<String> hashKey;

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
        fieldValue = (Expression<String>) exprs[0];
        hashKey = (Expression<String>) exprs[1];
        return true;
    }

    @Override
    @Nonnull
    public String toString(@Nullable Event event, boolean debug) {
        return "names of field with value " + fieldValue.toString(event, debug) + " in redis hash " + hashKey.toString(event, debug);
    }

    @Override
    protected String[] get(Event event) {
        RediSkript plugin = RediSkript.getAPI();

        String hash = hashKey.getSingle(event);
        String value = fieldValue.getSingle(event);

        if (hash == null) {
            return null;
        }
        if (value == null) {
            return null;
        }
        String[] fieldNames = plugin.getRC().getHashFieldNamesByValue(hash, value).toArray(new String[0]);
        if (fieldNames[0] != null) {
            return fieldNames;
        } else {
            return null;
        }
    }
}