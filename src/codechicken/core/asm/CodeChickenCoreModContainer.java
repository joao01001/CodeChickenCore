package codechicken.core.asm;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import codechicken.core.CCUpdateChecker;
import codechicken.core.featurehack.LiquidTextures;
import codechicken.core.internal.CCCEventHandler;
import codechicken.core.launch.CodeChickenCorePlugin;
import codechicken.lib.config.ConfigFile;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import cpw.mods.fml.common.*;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.versioning.ArtifactVersion;
import cpw.mods.fml.common.versioning.VersionParser;
import cpw.mods.fml.common.versioning.VersionRange;

public class CodeChickenCoreModContainer extends DummyModContainer
{
    public static ConfigFile config;

    public static void loadConfig() {
        if(config == null)
            config = new ConfigFile(new File(CodeChickenCorePlugin.minecraftDir, "config/CodeChickenCore.cfg")).setComment("CodeChickenCore configuration file.");
    }

    public CodeChickenCoreModContainer() {
        super(MetadataCollection.from(MetadataCollection.class.getResourceAsStream("/cccmod.info"), "CodeChickenCore").getMetadataForId("CodeChickenCore", null));
    }

    @Override
    public List<ArtifactVersion> getDependants() {
        LinkedList<ArtifactVersion> deps = new LinkedList<ArtifactVersion>();
        if(!getVersion().contains("$")) {
            deps.add(VersionParser.parseVersionReference("NotEnoughItems@[1.0.0,)"));
        }
        return deps;
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void preInit(FMLPreInitializationEvent event) {
        if (event.getSide().isClient())
            LiquidTextures.init();
    }

    @Subscribe
    public void init(FMLInitializationEvent event) {
        if (event.getSide().isClient()) {
            if (config.getTag("checkUpdates").getBooleanValue(true))
                CCUpdateChecker.updateCheck(getModId());
            FMLCommonHandler.instance().bus().register(new CCCEventHandler());
        }
    }

    @Override
    public VersionRange acceptableMinecraftVersionRange() {
        return VersionParser.parseRange(CodeChickenCorePlugin.mcVersion);
    }
}
