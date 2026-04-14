package better_progression.skills;

import better_progression.skillLogic.SkillTickLogic;

public record Skill(String NAME_ID, String DESC_ID, SkillTickLogic onTick) {
}
