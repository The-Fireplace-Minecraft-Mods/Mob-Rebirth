package the_fireplace.mobrebirth.common;

import com.google.common.collect.Maps;
import net.minecraft.util.ResourceLocation;

import java.util.Map;

/**
 *
 * @author The_Fireplace
 *
 */
public class ConfigValues {
	//Mob controls
	public static final boolean ALLOWBOSSES_DEFAULT = false;
	public static boolean ALLOWBOSSES;

	public static final boolean ALLOWSLIMES_DEFAULT = true;
	public static boolean ALLOWSLIMES;

	public static final boolean ANIMALREBIRTH_DEFAULT = false;
	public static boolean ANIMALREBIRTH;
	//Chance Controls
	public static final double REBIRTHCHANCE_DEFAULT = 0.25;
	public static double REBIRTHCHANCE;
	public static Map<ResourceLocation, Double> REBIRTHCHANCEMAP = Maps.newHashMap();

	public static final double MULTIMOBCHANCE_DEFAULT = 0.05;
	public static double MULTIMOBCHANCE;
	public static Map<ResourceLocation, Double> MULTIMOBCHANCEMAP = Maps.newHashMap();
	//Behavior Controls
	public static final boolean DROPEGG_DEFAULT = false;
	public static boolean DROPEGG;
	public static Map<ResourceLocation, Boolean> DROPEGGMAP = Maps.newHashMap();

	public static final int EXTRAMOBCOUNT_DEFAULT = 0;
	public static int EXTRAMOBCOUNT;
	public static Map<ResourceLocation, Integer> EXTRAMOBCOUNTMAP = Maps.newHashMap();

	public static final String MULTIMOBMODE_DEFAULT = "continuous";
	public static String MULTIMOBMODE;

	public static final boolean REBIRTHFROMNONPLAYER_DEFAULT = false;
	public static boolean REBIRTHFROMNONPLAYER;
	public static Map<ResourceLocation, Boolean> REBIRTHFROMNONPLAYERMAP = Maps.newHashMap();

	public static final boolean DAMAGEFROMSUNLIGHT_DEFAULT = true;
	public static boolean DAMAGEFROMSUNLIGHT;
	//Debug Controls
	public static final boolean VANILLAONLY_DEFAULT = false;
	public static boolean VANILLAONLY;
	//General
	public static final String[] CUSTOMENTITIES_DEFAULT = new String[]{};
	public static String[] CUSTOMENTITIES;

	public static final String ALLOWBOSSES_NAME = "allowbosses";
	public static final String ALLOWSLIMES_NAME = "allowslimes";
	public static final String ANIMALREBIRTH_NAME = "animalrebirth";

	public static final String REBIRTHCHANCE_NAME = "rebirthchance";
	public static final String MULTIMOBCHANCE_NAME = "multimobchance";

	public static final String DAMAGEFROMSUNLIGHT_NAME = "damagefromsunlight";
	public static final String DROPEGG_NAME = "dropegg";
	public static final String EXTRAMOBCOUNT_NAME = "extramobcount";
	public static final String MULTIMOBMODE_NAME = "multimobmode";
	public static final String REBIRTHFROMNONPLAYER_NAME = "rebirthfromnonplayer";

	public static final String VANILLAONLY_NAME = "vanillaonly";

	public static final String CUSTOMENTITIES_NAME = "customentities";
}