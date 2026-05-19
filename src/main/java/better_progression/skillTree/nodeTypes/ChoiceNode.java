package better_progression.skillTree.nodeTypes;

import better_progression.skills.Skill;

import java.util.HashSet;
import java.util.Set;

public class ChoiceNode extends Node{


    public ChoiceNode(String id, Skill skill1, Skill skill2, int cost) {
        super(id, skill1, cost);
    }
}
