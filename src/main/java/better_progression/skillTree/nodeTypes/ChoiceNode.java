package better_progression.skillTree.nodeTypes;

import better_progression.rendering.RenderCommand;
import better_progression.skills.Skill;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ChoiceNode extends Node{
    private final Skill skillA;
    private final Skill skillB;
    private final int costA;
    private final int costB;

    // IDs für die beiden Hälften, um sie im Spielstand (UNLOCKED_SKILLS) getrennt zu speichern
    private final String idA;
    private final String idB;

    public ChoiceNode(String baseId, Skill skillA, int costA, Skill skillB, int costB) {
        // Die Super-Klasse bekommt null als Skill, da dieses Objekt zwei Skills hält
        super(baseId, null, 0);
        this.skillA = skillA;
        this.skillB = skillB;
        this.costA = costA;
        this.costB = costB;

        // Eindeutige IDs für die Hälften generieren
        this.idA = baseId + "_left";
        this.idB = baseId + "_right";
    }

    public int getUnlockState(List<String> activeUnlocked) {
        if (activeUnlocked.contains(this.idA)) return 1;
        if (activeUnlocked.contains(this.idB)) return 2;
        return 0;
    }

    @Override
    public void generateRenderCommands(List<RenderCommand> commandList, int windowX, int windowY, List<String> activeUnlocked, int currentPoints, Node globalRoot) {
        int myX = this.getXPos() + windowX;
        int myY = this.getYPos() + windowY;

        if (this.getParents().isEmpty() && globalRoot != null) {
            int state = getUnlockState(activeUnlocked);
            int lineColor = (state > 0) ? 0xFF55FF55 : 0xFF555555;
            commandList.add(new RenderCommand.Line(myX + 20, myY + 10, globalRoot,  lineColor));
        }

        for (Node parent : this.getParents()) {
            int state = getUnlockState(activeUnlocked);
            int lineColor = 0xFF555555;
            // Wenn der Parent freigeschaltet ist und ich selbst (eine der Hälften) auch, wird die Linie grün
            boolean parentUnlocked = activeUnlocked.contains(parent.getId()) || (parent.getSkill() == null);
            if (parentUnlocked && state > 0) {
                lineColor = 0xFF55FF55;
            } else if (Node.isBlockedByChoice(parent, activeUnlocked)) {
                lineColor = 0xFF880000; // Weg dorthin ist blockiert
            }
            commandList.add(new RenderCommand.Line(myX + 20, myY + 10, parent, lineColor));
        }

        // 2. Ein einzelnes ChoiceBackground-Kommando für das gesamte 2-in-1 Paar in die Queue werfen
        commandList.add(new RenderCommand.ChoiceBackground(this, myX, myY));
    }

    // Getters
    public Skill getSkillA() { return skillA; }
    public Skill getSkillB() { return skillB; }
    public int getCostA() { return costA; }
    public int getCostB() { return costB; }
    public String getIdA() { return idA; }
    public String getIdB() { return idB; }
}
