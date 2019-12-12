package the_fireplace.mobrebirth.fabric;

import com.google.gson.*;

import java.io.*;

public class Config {
    public static final File configFile = new File("config/mobrebirth.json");
    //General config
    public static boolean allowBosses = false;
    public static boolean allowSlimes = true;
    public static boolean allowAnimals = true;
    public static double rebirthChance = 0.1;
    public static double multiMobChance = 0.01;
    public static String multiMobMode = "continuous";
    public static int multiMobCount = 1;
    public static boolean rebornAsEggs = false;
    public static boolean rebirthFromNonPlayer = true;
    public static boolean damageFromSunlight = true;
    public static boolean vanillaMobsOnly = false;
    public static boolean rebirthInClaimedLand = false;

    public static void load() {
        JsonParser jsonParser = new JsonParser();
        try {
            Object obj = jsonParser.parse(new FileReader(configFile));
            if(obj instanceof JsonObject) {
                JsonObject jsonObject = (JsonObject) obj;
                allowBosses = getJsonPrimitive(jsonObject, "allowBosses").getAsBoolean();
                allowSlimes = getJsonPrimitive(jsonObject, "allowSlimes").getAsBoolean();
                allowAnimals = getJsonPrimitive(jsonObject, "allowAnimals").getAsBoolean();
                rebirthChance = getJsonPrimitive(jsonObject, "rebirthChance").getAsDouble();
                rebirthChance = rangeDouble(rebirthChance, 0, 1);
                multiMobChance = getJsonPrimitive(jsonObject, "multiMobChance").getAsDouble();
                multiMobChance = rangeDouble(multiMobChance, 0, 1);
                multiMobMode = getJsonPrimitive(jsonObject, "multiMobMode").getAsString();
                multiMobCount = getJsonPrimitive(jsonObject, "multiMobCount").getAsInt();
                multiMobCount = rangeInt(multiMobCount, 0, Integer.MAX_VALUE);
                rebornAsEggs = getJsonPrimitive(jsonObject, "rebornAsEggs").getAsBoolean();
                rebirthFromNonPlayer = getJsonPrimitive(jsonObject, "rebirthFromNonPlayer").getAsBoolean();
                damageFromSunlight = getJsonPrimitive(jsonObject, "damageFromSunlight").getAsBoolean();
                vanillaMobsOnly = getJsonPrimitive(jsonObject, "vanillaMobsOnly").getAsBoolean();
                rebirthInClaimedLand = getJsonPrimitive(jsonObject, "rebirthInClaimedLand").getAsBoolean();
            }
        } catch (FileNotFoundException e) {
            create();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static double rangeDouble(double input, double min, double max) {
        return Math.min(max, Math.max(input, min));
    }

    private static int rangeInt(int input, int min, int max) {
        return Math.min(max, Math.max(input, min));
    }

    private static JsonPrimitive getJsonPrimitive(JsonObject obj, String key) {
        return obj.getAsJsonObject(key).get("value").getAsJsonPrimitive();
    }

    private static JsonObject createObject(JsonPrimitive value, String comment) {
        JsonObject obj = new JsonObject();
        obj.add("value", value);
        obj.addProperty("_comment", comment);
        return obj;
    }

    private static void create() {
        JsonObject obj = new JsonObject();
        obj.add("allowBosses", createObject(new JsonPrimitive(allowBosses), "Sets whether or not bosses such as the Wither and Ender Dragon can be reborn because of this mod."));
        obj.add("allowSlimes", createObject(new JsonPrimitive(allowSlimes), "Sets whether or not Slimes can be reborn because of this mod."));
        obj.add("allowAnimals", createObject(new JsonPrimitive(allowAnimals), "Sets whether or not Animals can be reborn because of this mod."));
        obj.add("rebirthChance", createObject(new JsonPrimitive(rebirthChance), "Chance for a mob to be reborn."));
        obj.add("multiMobChance", createObject(new JsonPrimitive(multiMobChance), "Chance for multiple mobs to be reborn. Exact implementation varies based on multiMobMode."));
        obj.add("multiMobMode", createObject(new JsonPrimitive(multiMobMode), "Options are 'continuous', 'per-mob', or 'all'.\\n'Continuous' applies the chance to each extra mob, and stops when one doesn't spawn\\n'Per-Mob' applies the chance to each extra mob\\n'All' applies the chance once."));
        obj.add("multiMobCount", createObject(new JsonPrimitive(multiMobCount), "The number of extra mobs to be reborn. Set to 0 to disable extra mobs being reborn."));
        obj.add("rebornAsEggs", createObject(new JsonPrimitive(rebornAsEggs), "If true, mobs will drop eggs instead of respawning when they are reborn."));
        obj.add("rebirthFromNonPlayer", createObject(new JsonPrimitive(rebirthFromNonPlayer), "If true, mobs can be reborn from any kind of death, not just being killed by a player."));
        obj.add("damageFromSunlight", createObject(new JsonPrimitive(damageFromSunlight), "Should the undead be damaged when burning in the sunlight? Disabling fixes the daytime apocalypse created by certain configurations."));
        obj.add("vanillaMobsOnly", createObject(new JsonPrimitive(vanillaMobsOnly), "Set to true to prevent any mobs or animals not in vanilla Minecraft from being reborn\\nMeant to temporarily fix issues with modded mobs, should they arise\\nAnything that makes you need to use this should be reported."));
        obj.add("rebirthInClaimedLand", createObject(new JsonPrimitive(rebirthInClaimedLand), "Allow mobs to be reborn in claimed land. This option only takes effect with Clans installed."));

        try {
            FileWriter file = new FileWriter(configFile);
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(obj);
            file.write(json);
            file.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
