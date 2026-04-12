package better_progression.datagen.providers;

import better_progression.items.SkillpointBottle;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

import static better_progression.items.ModItems.SKILLPOINT_BOTTLE;

public class ModGermanLangProvider extends FabricLanguageProvider {
    public static final String LANGUAGE_CODE = "de_de";

    public ModGermanLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, LANGUAGE_CODE, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(SKILLPOINT_BOTTLE, SkillpointBottle.GERMAN_NAME);
    }
}