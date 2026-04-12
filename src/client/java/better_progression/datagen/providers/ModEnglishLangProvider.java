package better_progression.datagen.providers;

import better_progression.items.SkillpointBottle;
import better_progression.skills.Skill;
import better_progression.skills.Skills;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

import static better_progression.items.ModItems.SKILLPOINT_BOTTLE;

public class ModEnglishLangProvider extends FabricLanguageProvider {
    public static final String LANGUAGE_CODE = "en_us";

    public ModEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, LANGUAGE_CODE, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(SKILLPOINT_BOTTLE, SkillpointBottle.ENGLISH_NAME);

        for (Skill skill : Skills.SKILLS) {
            translationBuilder.add(skill.getName(), skill.ENGLISH_NAME());

            translationBuilder.add(skill.getName() + ".desc", skill.ENGLISH_DECRIPTION());
        }
    }
}
