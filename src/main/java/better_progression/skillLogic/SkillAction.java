package better_progression.skillLogic;

@FunctionalInterface
public interface SkillAction {
    void accept(SkillContext context);
}

