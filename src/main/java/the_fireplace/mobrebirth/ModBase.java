package the_fireplace.mobrebirth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import the_fireplace.mobrebirth.config.ConfigValues;
import the_fireplace.mobrebirth.config.MobRebirthOnConfigChanged;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.FMLLog;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = ModBase.MODID, name = ModBase.MODNAME, version = ModBase.VERSION, acceptedMinecraftVersions="1.7.2,1.7.10", canBeDeactivated = true, guiFactory = "the_fireplace.mobrebirth.config.MobRebirthGuiFactory", dependencies="required-after:fireplacecore@[1.0.3.0,)")
public class ModBase {
	@Instance(ModBase.MODID)
	public static ModBase instance;
	public static final String MODID = "mobrebirth";
	public static final String MODNAME = "Mob Rebirth";
	public static final String VERSION = "1.1.0.2";
	
	private static int updateNotification;
	private static String releaseVersion;
	private static String prereleaseVersion;
	private static final String downloadURL = "http://goo.gl/esuQuX";
	
	
	public static Configuration file;
	
	public static Property SPAWNMOBCHANCE_PROPERTY;
	public static Property SPAWNMOB_PROPERTY;
	public static Property NATURALREBIRTH_PROPERTY;
	public static Property SPAWNANIMALS_PROPERTY;
	
	
	public static void syncConfig(){
		ConfigValues.SPAWNMOBCHANCE = SPAWNMOBCHANCE_PROPERTY.getDouble();
		ConfigValues.SPAWNMOB = SPAWNMOB_PROPERTY.getBoolean();
		ConfigValues.NATURALREBIRTH = NATURALREBIRTH_PROPERTY.getBoolean();
		ConfigValues.SPAWNANIMALS = SPAWNANIMALS_PROPERTY.getBoolean();
		if(file.hasChanged()){
	        file.save();
		}
	}
	
	@EventHandler
	public void PreInit(FMLPreInitializationEvent event) {
		file = new Configuration(event.getSuggestedConfigurationFile());
		file.load();
		SPAWNMOBCHANCE_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNMOBCHANCE_NAME, ConfigValues.SPAWNMOBCHANCE_DEFAULT);
		SPAWNMOB_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNMOB_NAME, ConfigValues.SPAWNMOB_DEFAULT);
		NATURALREBIRTH_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.NATURALREBIRTH_NAME, ConfigValues.NATURALREBIRTH_DEFAULT);
		SPAWNANIMALS_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNANIMALS_NAME, ConfigValues.SPAWNANIMALS_DEFAULT);
		syncConfig();
		retriveCurrentVersions();
	}
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(instance);
		FMLCommonHandler.instance().bus().register(new MobRebirthOnConfigChanged());
		MinecraftForge.EVENT_BUS.register(new MobRebirthHandler());
	}
	/**
	 * This method is client side called when a player joins the game. Both for
	 * a server or a single player world.
	 */
	public static void onPlayerJoinClient(EntityPlayer player,
			ClientConnectedToServerEvent event) {
		if (!prereleaseVersion.equals("")
				&& !releaseVersion.equals("")) {
			switch (updateNotification) {
			case 0:
				if (isHigherVersion(VERSION, releaseVersion) && isHigherVersion(prereleaseVersion, releaseVersion)) {
					sendToPlayer(
							player,
							"§6A new version of "+MODNAME+" is available!\n§l§c========== §4"
									+ releaseVersion
									+ "§c ==========\n"
									+ "§6Download it at §e" + downloadURL + "§6!");
				}else if(isHigherVersion(VERSION, prereleaseVersion)){
					sendToPlayer(
							player,
							"§6A new version of "+MODNAME+" is available!\n§l§c========== §4"
									+ prereleaseVersion
									+ "§c ==========\n"
									+ "§6Download it at §e" + downloadURL + "§6!");
				}

				break;
			case 1:
				if (isHigherVersion(VERSION, releaseVersion)) {
					sendToPlayer(
							player,
							"§6A new version of "+MODNAME+" is available!\n§l§c========== §4"
									+ releaseVersion
									+ "§c ==========\n"
									+ "§6Download it at §e" + downloadURL + "§6!");
				}
				break;
			case 2:
				
				break;
			}
		}
	}
	
	/**
	 * Sends a chat message to the current player. Only works client side
	 * 
	 * @param message
	 *            the message to be sent
	 */
	private static void sendToPlayer(EntityPlayer player, String message) {
		String[] lines = message.split("\n");

		for (String line : lines)
			((ICommandSender) player)
					.addChatMessage(new ChatComponentText(line));
	}

	/**
	 * Checks if the new version is higher than the current one
	 * 
	 * @param currentVersion
	 *            The version which is considered current
	 * @param newVersion
	 *            The version which is considered new
	 * @return Whether the new version is higher than the current one or not
	 */
	private static boolean isHigherVersion(String currentVersion,
			String newVersion) {
		final int[] _current = splitVersion(currentVersion);
		final int[] _new = splitVersion(newVersion);

		return (_current[0] < _new[0])
				|| ((_current[0] == _new[0]) && (_current[1] < _new[1]))
				|| ((_current[0] == _new[0]) && (_current[1] == _new[1]) && (_current[2] < _new[2]))
				|| ((_current[0] == _new[0]) && (_current[1] == _new[1]) && (_current[2] == _new[2]) && (_current[3] < _new[3]));
	}

	/**
	 * Splits a version in its number components (Format ".\d+\.\d+\.\d+.*" )
	 * 
	 * @param Version
	 *            The version to be splitted (Format is important!
	 * @return The numeric version components as an integer array
	 */
	private static int[] splitVersion(String Version) {
		final String[] tmp = Version.split("\\.");
		final int size = tmp.length;
		final int out[] = new int[size];

		for (int i = 0; i < size; i++) {
			out[i] = Integer.parseInt(tmp[i]);
		}

		return out;
	}

	/**
	 * Retrieves what the latest version is from Dropbox
	 */
	private static void retriveCurrentVersions() {
		try {
			releaseVersion = get_content(new URL(
					"https://dl.dropboxusercontent.com/s/xpf1swir6n9rx3c/release.version?dl=0")
					.openConnection());

			prereleaseVersion = get_content(new URL(
					"https://dl.dropboxusercontent.com/s/x4a9lubkolghoge/prerelease.version?dl=0")
					.openConnection());

		} catch (final MalformedURLException e) {
			System.out.println("Malformed URL Exception");
			releaseVersion = "";
			prereleaseVersion = "";
		} catch (final IOException e) {
			System.out.println("IO Exception");
			releaseVersion = "";
			prereleaseVersion = "";
		}
	}

	private static String get_content(URLConnection con) throws IOException {
		String output = "";

		if (con != null) {
			final BufferedReader br = new BufferedReader(new InputStreamReader(
					con.getInputStream()));

			String input;

			while ((input = br.readLine()) != null) {
				output = output + input;
			}
			br.close();
		}

		return output;
	}
}
