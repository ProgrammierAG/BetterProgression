package better_progression.datagen.providers;

import better_progression.items.SkillpointBottle;
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
        translationBuilder.add(Skills.SKILLS.get("speed").DESC_ID(), "Speed\n" + "Increases the players speed.");
        translationBuilder.add(Skills.SKILLS.get("attack_range").DESC_ID(), "Attack range\n" + "Increases the players attack range.");
    }
}
