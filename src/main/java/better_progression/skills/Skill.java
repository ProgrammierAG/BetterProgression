package better_progression.skills;

import better_progression.skillLogic.SkillAction;
import better_progression.skillLogic.SkillCondition;
import better_progression.skillLogic.SkillContext;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record Skill(
        String id,
        String desc_id,
        Identifier iconId,
        Map<String, Map<String, String>> translations,
        List<TickAction> tickActions,
        List<SkillAction> unlockActions,
        List<SkillAction> resetActions
) {
    public record TickAction(
            SkillCondition condition,
            SkillAction action,
            SkillAction elseAction
    ) {}

    public static Builder builder(String id) {
        return new Builder(id);
    }

    public void tick(SkillContext context) {
        tickActions.forEach(a -> {
            if (a.condition().test(context)) a.action().accept(context);
            else if (a.elseAction() != null) a.elseAction().accept(context);
        });
    }

    public void unlock(SkillContext context) {
        unlockActions.forEach(a -> a.accept(context));
    }

    public void reset(SkillContext context) {
        resetActions.forEach(a -> a.accept(context));
    }

    public static class Builder {
        private final String id;
        private String descId;
        private Identifier iconId;
        private final Map<String, Map<String, String>> translations = new HashMap<>();
        private final List<TickAction> tickActions = new ArrayList<>();
        private final List<SkillAction> unlockActions = new ArrayList<>();
        private final List<SkillAction> resetActions = new ArrayList<>();

        private SkillAction currentTickAction;
        private SkillAction currentElseAction;
        private SkillCondition currentCondition;

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
        public Builder action(SkillAction action) {
            saveCurrentTick();
            this.currentTickAction = action;
            this.currentCondition = (ctx) -> true;
            this.currentElseAction = null;
            return this;
        }

        public Builder when(SkillCondition condition) {
            this.currentCondition = condition;
            return this;
        }

        public Builder elseAction(SkillAction elseAction) {
            this.currentElseAction = elseAction;
            return this;
        }

        public Builder onUnlock(SkillAction action) {
            saveCurrentTick();
            unlockActions.add(action);
            return this;
        }

        public Builder onReset(SkillAction action) {
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
