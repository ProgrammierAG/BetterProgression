package better_progression.rendering;

import better_progression.Attachments;
import better_progression.BetterProgression;
import better_progression.UIs.SkillTreeUI;
import better_progression.skillTree.nodeTypes.Node;
import better_progression.skills.Skill;
import better_progression.skills.Skills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.List;

public class SkillNodeRenderer {
    public static ImageButton createButton(Node node, int x, int y, java.util.function.Consumer<Node> clickCallback) {
        node.setXPos(x);
        node.setYPos(y);

        WidgetSprites icon;
        Component tooltipTitle;
        Component tooltipDesc = Component.empty();

        Skill skill = node.getSkill();
        if (skill == null) {
            icon = new WidgetSprites(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "skillbook"));
            tooltipTitle = Component.translatable("tooltip.betterprogression.global_root").withStyle(net.minecraft.ChatFormatting.GOLD);
        } else {
            icon = new WidgetSprites(skill.iconId());
            tooltipTitle = Component.translatable(skill.id()).withStyle(net.minecraft.ChatFormatting.GOLD);
            tooltipDesc = Component.literal("\n").append(Component.translatable(skill.desc_id()).withStyle(net.minecraft.ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC));
        }

        ImageButton button = new ImageButton(x, y, 20, 20, icon, b -> clickCallback.accept(node));

        int currentPoints = Minecraft.getInstance().player.getAttachedOrElse(Attachments.SKILLPOINTS, 0);
        net.minecraft.ChatFormatting costColor = (currentPoints >= node.getCost()) ? net.minecraft.ChatFormatting.DARK_GREEN : net.minecraft.ChatFormatting.RED;

        var tooltipBuilder = tooltipTitle.copy().append(tooltipDesc);
        if (skill != null) {
            tooltipBuilder.append(Component.literal("\n\n"))
                    .append(Component.translatable("tooltip.betterprogression.cost", node.getCost()).withStyle(costColor));
        }

        button.setTooltip(Tooltip.create(tooltipBuilder));
        return button;
    }

    public static void renderLines(Node node, GuiGraphics guiGraphics, int windowX, int windowY, List<String> activeUnlocked, SkillTreeUI screen) {
        int targetX = node.getXPos() + windowX + 10;
        int targetY = node.getYPos() + windowY + 10;

        // Verbindung zur GLOBAL_ROOT
        if (node.getParents().isEmpty() && !node.getId().equals("GLOBAL_ROOT")) {
            screen.getGlobalRootNode().ifPresent(rootNode -> {
                int startX = rootNode.getXPos() + windowX + 10;
                int startY = rootNode.getYPos() + windowY + 10;
                int lineColor = activeUnlocked.contains(node.getId()) ? 0xFF55FF55 : 0xFF555555;
                screen.drawLine(guiGraphics, targetX, targetY, startX, startY, lineColor);
            });
        }

        // Reguläre Linien im Baum zeichnen (stumpf von Mitte zu Mitte)
        node.getParents().forEach(parent -> {
            int startX = parent.getXPos() + windowX + 10;
            int startY = parent.getYPos() + windowY + 10;

            int lineColor = activeUnlocked.contains(parent.getId()) && activeUnlocked.contains(node.getId()) ? 0xFF55FF55 : 0xFF555555;
            screen.drawLine(guiGraphics, targetX, targetY, startX, startY, lineColor);
        });
    }

    public static void renderBackground(Node node, ImageButton button, GuiGraphics guiGraphics, int windowX, int windowY, List<String> activeUnlocked) {
        if (button == null) return;

        int drawX = node.getXPos() + windowX;
        int drawY = node.getYPos() + windowY;

        boolean isUnlocked = activeUnlocked.contains(node.getId()) || (node.getSkill() == null);
        boolean hasRequiredParent = node.getParents().isEmpty() || node.getParents().stream().anyMatch(p -> activeUnlocked.contains(p.getId()));
        int currentPoints = Minecraft.getInstance().player.getAttachedOrElse(Attachments.SKILLPOINTS, 0);
        boolean hasEnoughPoints = currentPoints >= node.getCost();

        if (isUnlocked) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND_OBTAINED, drawX - 2, drawY - 2, 24, 24);
        } else if (!hasRequiredParent || !hasEnoughPoints) {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND_UNOBTAINABLE, drawX - 2, drawY - 2, 24, 24);
        } else {
            guiGraphics.blitSprite(RenderPipelines.GUI_TEXTURED, Skills.BUTTON_BACKGROUND_UNOBTAINED, drawX - 2, drawY - 2, 24, 24);
        }
    }
}
