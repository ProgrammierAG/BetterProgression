package better_progression.datagen.providers;

import better_progression.Items.SkillpointBottle;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

import static better_progression.Items.ModItems.SKILLPOINT_BOTTLE;

public class ModEnglishLangProvider extends FabricLanguageProvider {
    public static final String LANGUAGE_CODE = "en_us";

    public ModEnglishLangProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, LANGUAGE_CODE, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider holderLookup, TranslationBuilder translationBuilder) {
        translationBuilder.add(SKILLPOINT_BOTTLE, SkillpointBottle.ENGLISH_NAME);
    }
}
