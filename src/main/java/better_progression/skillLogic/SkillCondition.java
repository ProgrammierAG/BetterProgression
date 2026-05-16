package better_progression.skillLogic;

@FunctionalInterface
public interface SkillCondition {
    boolean test(SkillContext context);
}
