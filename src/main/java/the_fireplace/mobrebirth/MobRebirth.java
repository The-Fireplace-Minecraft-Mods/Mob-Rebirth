package the_fireplace.mobrebirth;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import the_fireplace.fireplacecore.FireCoreBaseFile;
import the_fireplace.mobrebirth.config.ConfigValues;
import the_fireplace.mobrebirth.event.FMLEvents;
import the_fireplace.mobrebirth.event.ForgeEvents;
import the_fireplace.mobrebirth.gui.RebirthChanceSlider;
/**
 * 
 * @author The_Fireplace
 *
 */
@Mod(modid = MobRebirth.MODID, name = MobRebirth.MODNAME, version = MobRebirth.VERSION, acceptedMinecraftVersions="1.8", canBeDeactivated = true, guiFactory = "the_fireplace.mobrebirth.config.MobRebirthGuiFactory")
public class MobRebirth {
	@Instance(MobRebirth.MODID)
	public static MobRebirth instance;
	public static final String MODID = "mobrebirth";
	public static final String MODNAME = "Mob Rebirth";
	public static final String VERSION = "2.2.0.2";

	private static int updateNotification;
	private static String releaseVersion;
	private static String prereleaseVersion;
	private static final String downloadURL = "http://goo.gl/EQw3Ha";
	//For Dynious's Version Checker
	public static NBTTagCompound update = new NBTTagCompound();

	public static Configuration file;

	public static Property SPAWNMOBCHANCE_PROPERTY;
	public static Property SPAWNMOB_PROPERTY;
	public static Property NATURALREBIRTH_PROPERTY;
	public static Property SPAWNANIMALS_PROPERTY;
	public static Property EXTRAMOBCOUNT_PROPERTY;
	public static Property MULTIMOBCHANCE_PROPERTY;
	public static Property MULTIMOBMODE_PROPERTY;
	public static Property SUNLIGHTAPOCALYPSEFIX_PROPERTY;
	public static Property VANILLAONLY_PROPERTY;
	public static Property ALLOWBOSSES_PROPERTY;
	public static Property ALLOWSLIMES_PROPERTY;

	public static void syncConfig(){
		ConfigValues.SPAWNMOBCHANCE = SPAWNMOBCHANCE_PROPERTY.getDouble();
		ConfigValues.SPAWNMOB = SPAWNMOB_PROPERTY.getBoolean();
		ConfigValues.NATURALREBIRTH = NATURALREBIRTH_PROPERTY.getBoolean();
		ConfigValues.SPAWNANIMALS = SPAWNANIMALS_PROPERTY.getBoolean();
		ConfigValues.EXTRAMOBCOUNT = EXTRAMOBCOUNT_PROPERTY.getInt();
		ConfigValues.MULTIMOBCHANCE = MULTIMOBCHANCE_PROPERTY.getDouble();
		ConfigValues.MULTIMOBMODE = MULTIMOBMODE_PROPERTY.getString();
		ConfigValues.SUNLIGHTAPOCALYPSEFIX = SUNLIGHTAPOCALYPSEFIX_PROPERTY.getBoolean();
		ConfigValues.ALLOWBOSSES = ALLOWBOSSES_PROPERTY.getBoolean();
		ConfigValues.VANILLAONLY = VANILLAONLY_PROPERTY.getBoolean();
		ConfigValues.ALLOWSLIMES = ALLOWSLIMES_PROPERTY.getBoolean();
		if(file.hasChanged()){
			file.save();
		}
	}

	@EventHandler
	public void PreInit(FMLPreInitializationEvent event) {
		file = new Configuration(event.getSuggestedConfigurationFile());
		file.load();
		SPAWNMOBCHANCE_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNMOBCHANCE_NAME, ConfigValues.SPAWNMOBCHANCE_DEFAULT);
		SPAWNMOBCHANCE_PROPERTY.comment = StatCollector.translateToLocal("mrb2.tooltip");
		SPAWNMOB_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNMOB_NAME, ConfigValues.SPAWNMOB_DEFAULT);
		SPAWNMOB_PROPERTY.comment = StatCollector.translateToLocal("mrb1.tooltip");
		NATURALREBIRTH_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.NATURALREBIRTH_NAME, ConfigValues.NATURALREBIRTH_DEFAULT);
		NATURALREBIRTH_PROPERTY.comment = StatCollector.translateToLocal("mrb5.tooltip");
		SPAWNANIMALS_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SPAWNANIMALS_NAME, ConfigValues.SPAWNANIMALS_DEFAULT);
		SPAWNANIMALS_PROPERTY.comment = StatCollector.translateToLocal("mrb3.tooltip");
		EXTRAMOBCOUNT_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.EXTRAMOBCOUNT_NAME, ConfigValues.EXTRAMOBCOUNT_DEFAULT);
		EXTRAMOBCOUNT_PROPERTY.comment = StatCollector.translateToLocal("mrb4.tooltip");
		MULTIMOBCHANCE_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.MULTIMOBCHANCE_NAME, ConfigValues.MULTIMOBCHANCE_DEFAULT);
		MULTIMOBCHANCE_PROPERTY.comment = StatCollector.translateToLocal("mrb6.tooltip");
		MULTIMOBMODE_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.MULTIMOBMODE_NAME, ConfigValues.MULTIMOBMODE_DEFAULT);
		MULTIMOBMODE_PROPERTY.comment = StatCollector.translateToLocal("mrb7.tooltip");
		SUNLIGHTAPOCALYPSEFIX_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.SUNLIGHTAPOCALYPSEFIX_NAME, ConfigValues.SUNLIGHTAPOCALYPSEFIX_DEFAULT);
		SUNLIGHTAPOCALYPSEFIX_PROPERTY.comment = StatCollector.translateToLocal("solar_apocalypse_fix.tooltip");
		ALLOWBOSSES_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.ALLOWBOSSES_NAME, ConfigValues.ALLOWBOSSES_DEFAULT);
		ALLOWBOSSES_PROPERTY.comment = StatCollector.translateToLocal("allowbosses.tooltip");
		VANILLAONLY_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.VANILLAONLY_NAME, ConfigValues.VANILLAONLY_DEFAULT);
		VANILLAONLY_PROPERTY.comment = StatCollector.translateToLocal("vanillaonly.tooltip");
		ALLOWSLIMES_PROPERTY = file.get(Configuration.CATEGORY_GENERAL, ConfigValues.ALLOWSLIMES_NAME, ConfigValues.ALLOWSLIMES_DEFAULT);
		ALLOWSLIMES_PROPERTY.comment = StatCollector.translateToLocal("allowslimes.tooltip");
		if(event.getSide().isClient())
			SPAWNMOBCHANCE_PROPERTY.setConfigEntryClass(RebirthChanceSlider.class);
		SPAWNMOBCHANCE_PROPERTY.setMaxValue(1.0);
		SPAWNMOBCHANCE_PROPERTY.setMinValue(0.0);
		if(event.getSide().isClient())
			MULTIMOBCHANCE_PROPERTY.setConfigEntryClass(RebirthChanceSlider.class);
		MULTIMOBCHANCE_PROPERTY.setMaxValue(1.0);
		MULTIMOBCHANCE_PROPERTY.setMinValue(0.0);

		syncConfig();
		retriveCurrentVersions();
		FireCoreBaseFile.instance.addUpdateInfo(update, this.MODNAME, this.VERSION, this.prereleaseVersion, this.releaseVersion, this.downloadURL, this.MODID);
	}
	@EventHandler
	public void Init(FMLInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(new FMLEvents());
		MinecraftForge.EVENT_BUS.register(new ForgeEvents());
	}
	/**
	 * This method is client side called when a player joins the game. Both for
	 * a server or a single player world.
	 */
	public static void onPlayerJoinClient(EntityPlayer player,
			ClientConnectedToServerEvent event) {
		updateNotification=FireCoreBaseFile.instance.getUpdateNotification();
		if (!prereleaseVersion.equals("")
				&& !releaseVersion.equals("")) {
			switch (updateNotification) {
			case 0:
				if (FireCoreBaseFile.isHigherVersion(VERSION, releaseVersion) && FireCoreBaseFile.isHigherVersion(prereleaseVersion, releaseVersion)) {
					FireCoreBaseFile.sendClientUpdateNotification(player, MODNAME, releaseVersion, downloadURL);
				}else if(FireCoreBaseFile.isHigherVersion(VERSION, prereleaseVersion)){
					FireCoreBaseFile.sendClientUpdateNotification(player, MODNAME, prereleaseVersion, downloadURL);
				}

				break;
			case 1:
				if (FireCoreBaseFile.isHigherVersion(VERSION, releaseVersion)) {
					FireCoreBaseFile.sendClientUpdateNotification(player, MODNAME, releaseVersion, downloadURL);
				}
				break;
			case 2:

				break;
			}
		}
	}

	/**
	 * Retrieves what the latest version is from Dropbox
	 */
	private static void retriveCurrentVersions() {
		try {
			releaseVersion = FireCoreBaseFile.get_content(new URL(
					"https://dl.dropboxusercontent.com/s/xpf1swir6n9rx3c/release.version?dl=0")
			.openConnection());

			prereleaseVersion = FireCoreBaseFile.get_content(new URL(
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
}
