package com.angelok963.interactprevention;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid = "WorldGuardTweaks", name = "World Guard Tweaks", version = "1.0")
public class Main {
	public static FMLEventChannel channel;

	@EventHandler
	public void init(FMLInitializationEvent event) {
		PacketHandler handler = new PacketHandler();
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("wg_tweaks");
		channel.register(handler);
		MinecraftForge.EVENT_BUS.register(new PacketHandler());

	}

}
