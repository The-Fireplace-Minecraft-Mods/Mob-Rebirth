package the_fireplace.mobrebirth;

import com.google.common.collect.Maps;
import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.commons.lang3.tuple.Pair;
import the_fireplace.mobrebirth.compat.clans.ClansCompat;
import the_fireplace.mobrebirth.compat.clans.ClansCompatDummy;
import the_fireplace.mobrebirth.compat.clans.IClansCompat;

import java.util.Map;

/**
 * @author The_Fireplace
 */
@SuppressWarnings("WeakerAccess")
@Mod(MobRebirth.MODID)
public class MobRebirth {
	public static final String MODID = "mobrebirth";
	public static Map<EntityType<?>, SpawnEggItem> spawnEggs = Maps.newHashMap();
	public static IClansCompat clansCompat = new ClansCompatDummy();

	public MobRebirth() {
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, cfg.SERVER_SPEC);
		MinecraftForge.EVENT_BUS.register(new CommonEvents());
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::loadComplete);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::serverConfig);
	}

	public void serverConfig(ModConfig.ModConfigEvent event) {
		if (event.getConfig().getType() == ModConfig.Type.SERVER)
			cfg.load();
	}

	public void loadComplete(FMLLoadCompleteEvent event) {
		for(SpawnEggItem egg: SpawnEggItem.getEggs())
			spawnEggs.put(egg.getType(null), egg);
		if(ModList.get().isLoaded("clans"))
			clansCompat = new ClansCompat();
	}

	public static class cfg {
		public static final ServerConfig SERVER;
		public static final ForgeConfigSpec SERVER_SPEC;

		static {
			final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
			SERVER_SPEC = specPair.getRight();
			SERVER = specPair.getLeft();
		}

		//General clan config
		public static boolean allowBosses;
		public static boolean allowSlimes;
		public static boolean allowAnimals;
		public static double rebirthChance;
		public static double multiMobChance;
		public static String multiMobMode;
		public static int multiMobCount;
		public static boolean rebornAsEggs;
		public static boolean rebirthFromNonPlayer;
		public static boolean damageFromSunlight;
		public static boolean vanillaMobsOnly;
		public static boolean rebirthInClaimedLand;

		public static void load() {
			allowBosses = SERVER.allowBosses.get();
			allowSlimes = SERVER.allowSlimes.get();
			allowAnimals = SERVER.allowAnimals.get();
			rebirthChance = SERVER.rebirthChance.get();
			multiMobChance = SERVER.multiMobChance.get();
			multiMobMode = SERVER.multiMobMode.get();
			multiMobCount = SERVER.multiMobCount.get();
			rebornAsEggs = SERVER.rebornAsEggs.get();
			rebirthFromNonPlayer = SERVER.rebirthFromNonPlayer.get();
			damageFromSunlight = SERVER.damageFromSunlight.get();
			vanillaMobsOnly = SERVER.vanillaMobsOnly.get();
			rebirthInClaimedLand = SERVER.rebirthInClaimedLand.get();
		}

		public static class ServerConfig {
			//General clan config
			public ForgeConfigSpec.BooleanValue allowBosses;
			public ForgeConfigSpec.BooleanValue allowSlimes;
			public ForgeConfigSpec.BooleanValue allowAnimals;
			public ForgeConfigSpec.DoubleValue rebirthChance;
			public ForgeConfigSpec.DoubleValue multiMobChance;
			public ForgeConfigSpec.IntValue multiMobCount;
			public ForgeConfigSpec.BooleanValue rebornAsEggs;
			public ForgeConfigSpec.ConfigValue<String> multiMobMode;
			public ForgeConfigSpec.BooleanValue rebirthFromNonPlayer;
			public ForgeConfigSpec.BooleanValue damageFromSunlight;
			public ForgeConfigSpec.BooleanValue vanillaMobsOnly;
			public ForgeConfigSpec.BooleanValue rebirthInClaimedLand;

			ServerConfig(ForgeConfigSpec.Builder builder) {
				builder.push("general");
				allowBosses = builder
						.comment("Sets whether or not bosses such as the Wither and Ender Dragon can be reborn because of this mod.")
						.translation("Allow Boss Rebirth")
						.define("allowBosses", false);
				allowSlimes = builder
						.comment("Sets whether or not Slimes can be reborn because of this mod.")
						.translation("Allow Slime Rebirth")
						.define("allowSlimes", true);
				allowAnimals = builder
						.comment("Sets whether or not Animals can be reborn because of this mod.")
						.translation("Allow Animal Rebirth")
						.define("allowAnimals", true);
				rebirthChance = builder
						.comment("Chance for a mob to be reborn.")
						.translation("Mob Rebirth Chance")
						.defineInRange("rebirthChance", 0.1, 0, 1);
				multiMobChance = builder
						.comment("Chance for multiple mobs to be reborn. Exact implementation varies based on multiMobMode.")
						.translation("Multi Mob Rebirth Chance")
						.defineInRange("multiMobChance", 0.01, 0, 1);
				multiMobMode = builder
						.comment("Options are 'continuous', 'per-mob', or 'all'.\\n'Continuous' applies the chance to each extra mob, and stops when one doesn't spawn\\n'Per-Mob' applies the chance to each extra mob\\n'All' applies the chance once.")
						.translation("Multi Mob Mode")
						.define("multiMobMode", "continuous", string -> string != null && (string.equals("continuous") || string.equals("all") || string.equals("per-mob")));
				multiMobCount = builder
						.comment("The number of extra mobs to be reborn. Set to 0 to disable extra mobs being reborn.")
						.translation("Multi Mob Count")
						.defineInRange("multiMobCount", 1, 0, Integer.MAX_VALUE);
				rebornAsEggs = builder
						.comment("If true, mobs will drop eggs instead of respawning when they are reborn.")
						.translation("Mobs Reborn As Eggs")
						.define("rebornAsEggs", false);
				rebirthFromNonPlayer = builder
						.comment("If true, mobs can be reborn from any kind of death, not just being killed by a player.")
						.translation("Rebirth From Non-Player Death")
						.define("rebirthFromNonPlayer", true);
				damageFromSunlight = builder
						.comment("Should the undead be damaged when burning in the sunlight? Disabling fixes the daytime apocalypse created by certain configurations.")
						.translation("Undead Sunlight Damage")
						.define("damageFromSunlight", true);
				vanillaMobsOnly = builder
						.comment("Set to true to prevent any mobs or animals not in vanilla Minecraft from being reborn\\nMeant to temporarily fix issues with modded mobs, should they arise\\nAnything that makes you need to use this should be reported.")
						.translation("Rebirth on Vanilla Mobs Only")
						.define("vanillaMobsOnly", false);
				rebirthInClaimedLand = builder
						.comment("Allow mobs to be reborn in claimed land. This option only takes effect with Clans installed.")
						.translation("Rebirth in Claimed Land")
						.define("rebirthInClaimedLand", false);
				builder.pop();
			}
		}
	}
}
