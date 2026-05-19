package better_progression.datagen.providers;

import better_progression.BetterProgression;
import better_progression.skillTree.nodeTypes.Node;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

import java.util.Objects;

import static better_progression.skillTree.SkillTree.REGISTRY;

public class ModSkillTranslationHandler {
    public static void generateSkillTranslations(FabricLanguageProvider.TranslationBuilder translationBuilder, String langCode) {
        if (langCode.equals("de_de")) {
            translationBuilder.add("message.betterprogression.blocked", "Dieser Pfad ist durch eine andere Wahl blockiert!");
            translationBuilder.add("message.betterprogression.requires_parent", "Du musst zuerst die vorherigen Skills freischalten!");
            translationBuilder.add("message.betterprogression.unlocked_success", "Fähigkeit freigeschaltet!");
            translationBuilder.add("tooltip.betterprogression.global_root", "Ursprung des Wissens");
            translationBuilder.add("tooltip.betterprogression.cost", "Kosten: %d Punkte");
        } else if (langCode.equals("en_us")) {
            translationBuilder.add("message.betterprogression.blocked", "This path is blocked by another choice!");
            translationBuilder.add("message.betterprogression.requires_parent", "You must unlock the previous skills first!");
            translationBuilder.add("message.betterprogression.unlocked_success", "Skill unlocked!");
            translationBuilder.add("tooltip.betterprogression.global_root", "Origin of Knowledge");
            translationBuilder.add("tooltip.betterprogression.cost", "Cost: %d points");
        }

        REGISTRY.values().stream()
                .flatMap(tree -> tree.getNodes().values().stream())
                .map(Node::getSkill)
                .filter(Objects::nonNull)
                .distinct()
                .forEach(skill -> {
                    if (skill.translations().containsKey(langCode)) {
                        skill.translations().get(langCode).forEach(translationBuilder::add);
                    } else {
                        BetterProgression.getLogger().warn("Missing translations for language '{}' in Skill ID: {}", langCode, skill.id());
                        translationBuilder.add(skill.id(), skill.id());
                        translationBuilder.add(skill.desc_id(), "No description available (" + langCode + ").");
                    }
                });
    }
}
