package better_progression.mixin.client;

import better_progression.BetterProgression;
import better_progression.UIs.SkillTreeUI;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractContainerScreen<InventoryMenu> {

    ImageButton skillTree;

    public InventoryScreenMixin(InventoryMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void addCustomButton(CallbackInfo ci) {
        int x = this.leftPos + 126;
        int y = this.height / 2 - 22;

        WidgetSprites iconSprite = new WidgetSprites(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "inventory_button"),
                Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "inventory_button_hovered"));
        skillTree = new ImageButton(
                x, y, 20, 18,
                iconSprite,
                button -> {
                    if (this.minecraft != null) {
                        this.minecraft.setScreen(new SkillTreeUI());
                    }
                }
        );
        this.addRenderableWidget(skillTree);
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void updateButtonPosition(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick, CallbackInfo ci) {
        if (this.skillTree != null) {

            this.skillTree.setX(this.leftPos + 126);

            this.skillTree.setY(this.height / 2 - 22);
        }
    }
}
