package better_progression.skills;

import better_progression.BetterProgression;
import better_progression.skillLogic.SkillContext;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.GameType;

import java.util.HashMap;
import java.util.Map;

public class Skills {
    public static Map<String , Skill> SKILLS = new HashMap<>();
    public static final Identifier BUTTON_BACKGROUND_UNOBTAINED = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_unobtained");

    public static final Identifier BUTTON_BACKGROUND_UNOBTAINABLE = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_unobtainable");

    public static final Identifier BUTTON_BACKGROUND_OBTAINED = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_obtained");

    public static final Identifier BUTTON_BACKGROUND_BLOCKED = Identifier.fromNamespaceAndPath(
            BetterProgression.MOD_ID, "task_frame_blocked"
    );

    // ----- Skills -----
    public static final Skill EXAMPLE = register(Skill
            .builder("example") //the skills id
            .descriptionId("example_desc") // Sets a custom translation key for the description

            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "skillbook"/*Filename without file extension*/))
            //adds an icon from the folder:
            //"src/main/resources/assets/better_progression/textures/gui/sprites"

            .de("Beispiel Skill", "Ein Skill, der nichts tut") // Adds German name and description
            .en("Example Skill", "A skill that does nothing") // Adds English name and description

            .action((context) -> {
                context.getPlayer().setGameMode(GameType.CREATIVE);
            }) // The main action to execute every tick
            .when((context) -> context.getPlayer().isInPowderSnow) // Acts as a gatekeeper:
            // if this returns false, the action is skipped. If omitted, it defaults to 'true' (always execute).
            .elseAction((context) -> {
                context.getPlayer().setGameMode(GameType.SURVIVAL);
            }) // Executed only if the 'when' condition fails

            .onUnlock((context) -> {}) // Executed once when the skill is unlocked
            .onReset((context) -> {
                context.getPlayer().setGameMode(GameType.SURVIVAL);
            }) // Executed once when the skill is reset

            .build()); // Finalizes the builder and creates the Skill record

    public static final Skill SPEED = register(Skill
            .builder("speed")
            .descriptionId("speed_desc")
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "speed_multiplier"))
            .en("Speed", "Permanently increases your movement speed.")
            .de("Geschwindigkeit", "Erhöht deine Bewegungsgeschwindigkeit dauerhaft.")
            .onUnlock(ctx -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.MOVEMENT_SPEED);
                if (attr != null) attr.setBaseValue(0.1 + (0.01 * ctx.getSkillLevel()));
            })
            .onReset(ctx -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.MOVEMENT_SPEED);
                if (attr != null) attr.setBaseValue(0.1);
            })
            .build());

    public static final Skill ATTACK_RANGE = register(Skill
            .builder("attack_range")
            .descriptionId("attack_range_desc")
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "sword_range"))
            .en("Attack Range", "Increases your attack and interaction range.")
            .de("Angriffsreichweite", "Erhöht deine Angriffs- und Interaktionsreichweite.")
            .onUnlock(ctx -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
                if (attr != null) attr.setBaseValue(3.0 + (0.5 * ctx.getSkillLevel()));
            })
            .onReset(ctx -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
                if (attr != null) attr.setBaseValue(3.0);
            })
            .build());

    public static final Skill SAFE_FALL_DISTANCE = register(Skill
            .builder("safe_fall_distance")
            .descriptionId("safe_fall_distance_desc")

            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "safe_fall"))


            .de("Sichere Fall-Höhe", "Erhöht deine sichere Fall-Höhe")
            .en("Safe Fall Range", "highers your safe fall distance")

            .onUnlock((ctx) -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.SAFE_FALL_DISTANCE);
                if (attr != null) attr.setBaseValue(3.0 + (1.0 * ctx.getSkillLevel()));
             })

            .onReset((ctx) -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.SAFE_FALL_DISTANCE);
                if (attr != null) attr.setBaseValue(3.0);
            })

            .build());

    public static final Skill NO_HUNGER_EFFECT = register(Skill
            .builder("no_hunger_effect")
            .descriptionId("no_hunger_effect_desc")
            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "no_rotten_flesh_effect"))
            .en("Iron Stomach", "Instantly cures the hunger effect from bad food.")
            .de("Eiserner Magen", "Heilt den Hungereffekt von verdorbener Nahrung sofort.")
            .when(ctx -> ctx.getPlayer().hasEffect(MobEffects.HUNGER))
            .action(ctx -> ctx.getPlayer().removeEffect(MobEffects.HUNGER))
            .build());


    // ----- temporary -----
    // only used for testing (AI generated)
    public static final Skill MAX_HEALTH = register(Skill
            .builder("max_health")
            .descriptionId("max_health_desc")
            .icon(Identifier.fromNamespaceAndPath("minecraft", "hud/heart/full"))
            .en("Max Health", "Adds extra heart containers permanently.")
            .de("Maximale Gesundheit", "Fügt dauerhaft zusätzliche Herzen hinzu.")
            .onUnlock(ctx -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.MAX_HEALTH);
                if (attr != null) {
                    attr.setBaseValue(20.0 + (2.0 * ctx.getSkillLevel()));
                    ctx.getPlayer().heal(2.0f);
                }
            })
            .onReset(ctx -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.MAX_HEALTH);
                if (attr != null) attr.setBaseValue(20.0);
            })
            .build());

    public static final Skill FEATHER_FALLING = register(Skill
            .builder("feather_falling")
            .descriptionId("feather_falling_desc")
            .icon(Identifier.fromNamespaceAndPath("minecraft", "mob_effect/slow_falling"))
            .en("Glider", "Slows your fall automatically when falling from high places.")
            .de("Gleiter", "Verlangsamt deinen Fall automatisch bei großen Sturzhöhen.")
            .action(ctx -> ctx.getPlayer().addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 40, 0, false, false, true)))
            .when(ctx -> ctx.getPlayer().fallDistance > 3.0f)
            .build());

    public static final Skill GILLS = register(Skill
            .builder("gills")
            .descriptionId("gills_desc")
            .icon(Identifier.fromNamespaceAndPath("minecraft", "mob_effect/water_breathing"))
            .en("Gills", "Refills your oxygen supply right before you drown.")
            .de("Kiemen", "Füllt deinen Sauerstoff auf, kurz bevor du ertrinkst.")
            .action(ctx -> ctx.getPlayer().setAirSupply(Math.min(ctx.getPlayer().getMaxAirSupply(), ctx.getPlayer().getAirSupply() + 60)))
            .when(ctx -> ctx.getPlayer().getAirSupply() <= 20 && ctx.getPlayer().isEyeInFluid(FluidTags.WATER))
            .build());

    public static final Skill FIRE_IMMUNITY = register(Skill
            .builder("fire_immunity")
            .descriptionId("fire_immunity_desc")
            .icon(Identifier.fromNamespaceAndPath("minecraft", "mob_effect/fire_resistance"))
            .en("Fire Shield", "Grants fire resistance whenever you touch fire or lava.")
            .de("Feuerschild", "Gewährt Feuerresistenz, sobald du Feuer oder Lava berührst.")
            .action(ctx -> ctx.getPlayer().addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60, 0, false, false, true)))
            .when(ctx -> ctx.getPlayer().isOnFire() || ctx.getPlayer().isInLava())
            .build());

    public static final Skill STEP_ASSIST = register(Skill
            .builder("step_assist")
            .descriptionId("step_assist_desc")
            .icon(Identifier.fromNamespaceAndPath("minecraft", "mob_effect/jump_boost"))
            .en("Step Assist", "Allows you to step up full blocks without jumping.")
            .de("Schritthilfe", "Ermöglicht es dir, ganze Blöcke ohne Springen hochzugehen.")
            .onUnlock(ctx -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.STEP_HEIGHT);
                if (attr != null) attr.setBaseValue(1.0 + (0.5 * (ctx.getSkillLevel() - 1)));
            })
            .onReset(ctx -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.STEP_HEIGHT);
                if (attr != null) attr.setBaseValue(0.6);
            })
            .build());

    public static final Skill NIGHT_VISION = register(Skill
            .builder("night_vision")
            .descriptionId("night_vision_desc")
            .icon(Identifier.fromNamespaceAndPath("minecraft", "mob_effect/night_vision"))
            .en("Cave Eye", "Grants night vision automatically when you are in dark areas.")
            .de("Höhlenauge", "Gewährt automatisch Nachtsicht, wenn du dich in dunklen Bereichen aufhältst.")
            .action(ctx -> ctx.getPlayer().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 260, 0, false, false, false)))
            .when(ctx -> ctx.getPlayer().level().getMaxLocalRawBrightness(ctx.getPlayer().blockPosition()) < 4)
            .build());

    public static final Skill AQUA_SPEED = register(Skill
            .builder("aqua_speed")
            .descriptionId("aqua_speed_desc")
            .icon(Identifier.fromNamespaceAndPath("minecraft", "mob_effect/dolphins_grace"))
            .en("Ocean Glide", "Increases your swim speed significantly while submerged.")
            .de("Ozeangleiter", "Erhöht deine Schwimmgeschwindigkeit unter Wasser drastisch.")
            .action(ctx -> ctx.getPlayer().addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, 40, ctx.getSkillLevel() - 1, false, false, true)))
            .when(ctx -> ctx.getPlayer().isEyeInFluid(net.minecraft.tags.FluidTags.WATER))
            .build());

    public static final Skill KNOCKBACK_RESIST = register(Skill
            .builder("knockback_resist")
            .descriptionId("knockback_resist_desc")
            .icon(Identifier.fromNamespaceAndPath("minecraft", "mob_effect/resistance"))
            .en("Unstoppable", "Reduces the knockback you take from enemy attacks.")
            .de("Standhaft", "Verringert den Rückstoß, den du durch feindliche Angriffe erleidest.")
            .onUnlock(ctx -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                if (attr != null) attr.setBaseValue(0.2 * ctx.getSkillLevel());
            })
            .onReset(ctx -> {
                var attr = ctx.getPlayer().getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                if (attr != null) attr.setBaseValue(0.0);
            })
            .build());

    public static final Skill ALCH_REGEN = register(Skill
            .builder("alch_regen")
            .descriptionId("alch_regen_desc")
            .icon(Identifier.fromNamespaceAndPath("minecraft", "mob_effect/regeneration"))
            .en("Photosynthesis", "Slowly regenerates your health when your food bar is completely full.")
            .de("Fotosynthese", "Regeneriert langsam deine Gesundheit, wenn deine Hungerleiste komplett voll ist.")
            .action(ctx -> ctx.getPlayer().addEffect(new MobEffectInstance(MobEffects.REGENERATION, 50, ctx.getSkillLevel() - 1, false, false, true)))
            .when(ctx -> ctx.getPlayer().getFoodData().getFoodLevel() >= 20)
            .build());

    public static final Skill MINING_HASTE = register(Skill
            .builder("mining_haste")
            .descriptionId("mining_haste_desc")
            .icon(Identifier.fromNamespaceAndPath("minecraft", "mob_effect/haste"))
            .en("Haste", "Increases your mining speed permanently while holding a tool.")
            .de("Eile", "Erhöht deine Abbaugeschwindigkeit dauerhaft, während du ein Werkzeug hältst.")
            .action(ctx -> ctx.getPlayer().addEffect(new MobEffectInstance(MobEffects.HASTE, 40, ctx.getSkillLevel() - 1, false, false, true)))
            .when(ctx -> ctx.getPlayer().getMainHandItem().isCorrectToolForDrops(net.minecraft.world.level.block.Blocks.STONE.defaultBlockState()))
            .build());


    public static final Skill DEEP_BREATH = register(Skill
            .builder("deep_breath")
            .descriptionId("deep_breath_desc")
            .icon(Identifier.withDefaultNamespace("mob_effect/water_breathing"))
            .action((context) -> {
                // Füllt die Luftleiste des Spielers in jedem Tick komplett auf
                if (context.getPlayer() != null) {
                    context.getPlayer().setAirSupply(context.getPlayer().getMaxAirSupply());
                }
            })
            .when((context) -> {

                return context.getPlayer() != null && context.getPlayer().isEyeInFluid(net.minecraft.tags.FluidTags.WATER);
            })
            .elseAction((context) -> {})

            .de("Tiefer Atem", "Verhindert das Ertrinken, indem deine Luft unter Wasser gehalten wird.")
            .en("Deep Breath", "Prevents drowning by maintaining your air supply while underwater.")

            .onUnlock((context) -> {})
            .onReset((context) -> {})

            .build());

    public static final Skill CLIMBER = register(Skill
            .builder("climber") //the skills id
            .descriptionId("climber_desc") // Sets a custom translation key for the description

            .icon(Identifier.fromNamespaceAndPath(BetterProgression.MOD_ID, "skillbook"/*Filename without file extension*/))
            //adds an icon from the folder:
            //"src/main/resources/assets/better_progression/textures/gui/sprites"

            .de("Kletterer", "Lässt einen schneller Leitern und Ranken hochklettern") // Adds German name and description
            .en("Climber", "Lets you climb ladders and vines faster") // Adds English name and description

            .action((context) -> {
                net.minecraft.server.level.ServerPlayer player = context.getPlayer();

                // Nur anwenden, wenn der Spieler an einer Leiter/Ranke klettert
                if (player.onClimbable()) {
                    net.minecraft.world.phys.Vec3 velocity = player.getDeltaMovement();

                    // Verhindere, dass der Spieler beim Versuch, seitlich von der Leiter zu gehen,
                    // weiterhin automatisch hochgezogen wird. Wenn der Spieler sich deutlich
                    // horizontal bewegt, greifen wir nicht in die vertikale Bewegung ein.
                    double horizontalSpeed = Math.hypot(velocity.x, velocity.z);
                    double horizontalThreshold = 0.1; // wenn größer -> vermutlich versucht der Spieler die Leiter zu verlassen

                    if (horizontalSpeed <= horizontalThreshold) {
                        // Basis-Klettergeschwindigkeit (konstanter Wert, um exponentielles Verhalten zu vermeiden)
                        double baseClimbSpeed = 0.1;

                        // Lineare Level-Skalierung: Level 1 = 1.0×, Level 2 = 1.5×, Level 3 = 2.0× usw.
                        int level = Math.max(1, context.getSkillLevel());
                        double speedMultiplier = 1.0 + (0.5 * (level - 1));
                        double newClimbSpeed = baseClimbSpeed * speedMultiplier;

                        // Wende die Erhöhung nur an, wenn sie die aktuelle vertikale Geschwindigkeit erhöht.
                        // So wird ein fallender Spieler nicht plötzlich nach oben gezogen.
                        if (newClimbSpeed > velocity.y) {
                            player.setDeltaMovement(velocity.x, newClimbSpeed, velocity.z);
                            player.hurtMarked = true;
                        }
                    }
                }
            })

            .onUnlock((context) -> {}) // Executed once when the skill is unlocked
            .onReset((context) -> {
                context.getPlayer().setGameMode(GameType.SURVIVAL);
            }) // Executed once when the skill is reset

            .build()); // Finalizes the builder and creates the Skill record




    public static Skill register(Skill skill) {
        BetterProgression.getLogger().info("registering Skill: {}", skill.id());
        SKILLS.put(skill.id(), skill);
        return skill;
    }
    public static void initialize() {
        BetterProgression.getLogger().info("initializing Skills");
    }
}
