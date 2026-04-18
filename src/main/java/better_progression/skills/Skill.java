package better_progression.skills;

import better_progression.skillLogic.SkillTickLogic;
import net.minecraft.resources.Identifier;

public record Skill(String NAME_ID, String DESC_ID,
                    SkillTickLogic onUnlock, SkillTickLogic onTick, SkillTickLogic onReset, Identifier icon) {
}
