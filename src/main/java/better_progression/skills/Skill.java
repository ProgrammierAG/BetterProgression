package better_progression.skills;

import better_progression.BetterProgression;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public record Skill(
        String id,
        String desc_id,
        Identifier iconId,
        Map<String, Map<String, String>> translations,
        List<TickAction> tickActions,
        List<BiConsumer<ServerPlayer, Integer>> unlockActions,
        List<BiConsumer<ServerPlayer, Integer>> resetActions
) {
    public record TickAction(
            BiPredicate<ServerPlayer, Integer> condition,
            BiConsumer<ServerPlayer, Integer> action,
            BiConsumer<ServerPlayer, Integer> elseAction
    ) {}

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public void tick(ServerPlayer p, int l) {
        tickActions.forEach(a -> {
            if (a.condition().test(p, l)) a.action().accept(p, l);
            else if (a.elseAction() != null) a.elseAction().accept(p, l);
        });
    }

    public void unlock(ServerPlayer p, int l) {
        unlockActions.forEach(a -> a.accept(p, l));
    }

    public void reset(ServerPlayer p, int l) {
        resetActions.forEach(a -> a.accept(p, l));
    }

    public static class Builder {
        private final String id;
        private String descId;
        private Identifier iconId;
        private final Map<String, Map<String, String>> translations = new HashMap<>();
        private final List<TickAction> tickActions = new ArrayList<>();
        private final List<BiConsumer<ServerPlayer, Integer>> unlockActions = new ArrayList<>();
        private final List<BiConsumer<ServerPlayer, Integer>> resetActions = new ArrayList<>();

        private BiConsumer<ServerPlayer, Integer> currentTickAction;
        private BiConsumer<ServerPlayer, Integer> currentElseAction;
        private BiPredicate<ServerPlayer, Integer> currentCondition;

        public Builder(String id) {
            this.id = id;
            this.descId = id + "_desc";
            this.iconId = Identifier.withDefaultNamespace("textures/missingno");
        }

        public Builder icon(Identifier iconId) {
            this.iconId = iconId; // Manually set a specific icon identifier
            return this;
        }

        public Builder descriptionId(String descriptionId) {
            this.descId = descriptionId;
            return this;
        }

        // ----- Translation -----
        public Builder translateName(String lang, String text) {
            addTranslation(lang, id, text);
            return this;
        }

        public Builder translateDesc(String lang, String text) {
            addTranslation(lang, this.descId, text);
            return this;
        }

        private void addTranslation(String lang, String key, String text) {
            this.translations.computeIfAbsent(lang, k -> new HashMap<>()).put(key, text);
        }

        public Builder de(String name, String desc) { return translateName("de_de", name).translateDesc("de_de", desc); }
        public Builder en(String name, String desc) { return translateName("en_us", name).translateDesc("en_us", desc); }

        // ----- Logic -----
        public Builder action(BiConsumer<ServerPlayer, Integer> action) {
            saveCurrentTick();
            this.currentTickAction = action;
            this.currentCondition = (p, l) -> true;
            this.currentElseAction = null;
            return this;
        }

        public Builder when(BiPredicate<ServerPlayer, Integer> condition) {
            this.currentCondition = condition;
            return this;
        }

        public Builder elseAction(BiConsumer<ServerPlayer, Integer> elseAction) {
            this.currentElseAction = elseAction;
            return this;
        }

        public Builder onUnlock(BiConsumer<ServerPlayer, Integer> action) {
            saveCurrentTick();
            unlockActions.add(action);
            return this;
        }

        public Builder onReset(BiConsumer<ServerPlayer, Integer> action) {
            saveCurrentTick();
            resetActions.add(action);
            return this;
        }

        private void saveCurrentTick() {
            if (currentTickAction != null) {
                tickActions.add(new TickAction(currentCondition, currentTickAction, currentElseAction));
                currentTickAction = null;
            }
        }

        public Skill build() {
            saveCurrentTick();
            return new Skill(id, descId,iconId , Map.copyOf(translations),
                    List.copyOf(tickActions), List.copyOf(unlockActions), List.copyOf(resetActions));
        }
    }
}
