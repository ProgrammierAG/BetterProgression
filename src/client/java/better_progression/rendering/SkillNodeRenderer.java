package better_progression.rendering;

import better_progression.Attachments;
import better_progression.BetterProgression;
import better_progression.UIs.SkillTreeUI;
import better_progression.skillTree.nodeTypes.ChoiceNode;
import better_progression.skillTree.nodeTypes.Node;
import better_progression.skills.Skill;
import better_progression.skills.Skills;
import net.minecraft.ChatFormatting;
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
            tooltipTitle = Component.translatable(skill.id()).withStyle(ChatFormatting.GOLD);
            tooltipDesc = Component.literal("\n").append(Component.translatable(skill.desc_id()).withStyle(net.minecraft.ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC));
        }

        ImageButton button = new ImageButton(x, y, 20, 20, icon, b -> clickCallback.accept(node));

        int currentPoints = Minecraft.getInstance().player.getAttachedOrElse(Attachments.SKILLPOINTS, 0);
        ChatFormatting costColor = (currentPoints >= node.getCost()) ? ChatFormatting.DARK_GREEN : net.minecraft.ChatFormatting.RED;

        var tooltipBuilder = tooltipTitle.copy().append(tooltipDesc);
        if (skill != null) {
            tooltipBuilder.append(Component.literal("\n\n"))
                    .append(Component.translatable("tooltip.betterprogression.cost", node.getCost()).withStyle(costColor));
        }

        button.setTooltip(Tooltip.create(tooltipBuilder));
        return button;
    }

    public static ImageButton createButtonForChoiceHalf(ChoiceNode choiceNode, int x, int y, boolean isLeftHalf, java.util.function.Consumer<Node> clickCallback) {
        Skill skill = isLeftHalf ? choiceNode.getSkillA() : choiceNode.getSkillB();
        int cost = isLeftHalf ? choiceNode.getCostA() : choiceNode.getCostB();
        String uniqueHalfId = isLeftHalf ? choiceNode.getIdA() : choiceNode.getIdB();

        WidgetSprites icon;
        Component tooltipTitle;
        Component tooltipDesc = Component.empty();

        if (skill == null) {
            icon = new WidgetSprites(Identifier.fromNamespaceAndPath("minecraft", "textures/missingno"));
            tooltipTitle = Component.literal("Empty Slot").withStyle(ChatFormatting.RED);
        } else {
            icon = new WidgetSprites(skill.iconId());
            tooltipTitle = Component.translatable(skill.id()).withStyle(ChatFormatting.GOLD);
            tooltipDesc = Component.literal("\n").append(Component.translatable(skill.desc_id()).withStyle(net.minecraft.ChatFormatting.GRAY, net.minecraft.ChatFormatting.ITALIC));
        }

        Node virtualHalfNode = new Node(uniqueHalfId, skill, cost);
        virtualHalfNode.setXPos(x);
        virtualHalfNode.setYPos(y);

        ImageButton button = new ImageButton(x, y, 20, 20, icon, b -> clickCallback.accept(virtualHalfNode));

        int currentPoints = Minecraft.getInstance().player.getAttachedOrElse(Attachments.SKILLPOINTS, 0);
        net.minecraft.ChatFormatting costColor = (currentPoints >= cost) ? net.minecraft.ChatFormatting.DARK_GREEN : net.minecraft.ChatFormatting.RED;

        var tooltipBuilder = tooltipTitle.copy().append(tooltipDesc);
        if (skill != null) {
            tooltipBuilder.append(Component.literal("\n\n"))
                    .append(Component.translatable("tooltip.betterprogression.cost", cost).withStyle(costColor));
        }

        button.setTooltip(Tooltip.create(tooltipBuilder));
        return button;
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
