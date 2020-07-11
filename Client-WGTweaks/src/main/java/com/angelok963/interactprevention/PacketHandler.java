package com.angelok963.interactprevention;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.FMLNetworkEvent;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;

public class PacketHandler {

	private static boolean canBreak = false;
	private static boolean canPlace = false;
	private static boolean canUse = false;
	private static boolean canInteract = false;
	private static boolean canChestAcces = false;
	private static boolean canAll = false;
	private static int x = 10;
	private static int y = 10;
	private static String regions = "";

	@SubscribeEvent
	public void onUpdate(FMLNetworkEvent.ClientCustomPacketEvent e) {

		ByteBuf buf = e.packet.payload();

		if (!e.packet.channel().equals("wg_tweaks"))
			return;

		PacketHandler.canBreak = buf.readBoolean();
		PacketHandler.canPlace = buf.readBoolean();
		PacketHandler.canUse = buf.readBoolean();
		PacketHandler.canInteract = buf.readBoolean();
		PacketHandler.canChestAcces = buf.readBoolean();
		PacketHandler.canAll = buf.readBoolean();
		PacketHandler.x = buf.readInt();
		PacketHandler.y = buf.readInt();
		ByteBufUtils.readUTF8String(buf);
		PacketHandler.regions = ByteBufUtils.readUTF8String(buf);
		
	}

	@SubscribeEvent
	public void writeRegions(RenderGameOverlayEvent.Post  e) {

		String list[] = regions.split("\n");
		
		for(int z = 0; z<list.length; z++)
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(list[z],
				(e.resolution.getScaledWidth() / 2) - x, 
				e.resolution.getScaledHeight() - (y + z*10),
				0xFFFFFF);
	}

	@SubscribeEvent
	public void onSelected(DrawBlockHighlightEvent e) {

		if (canInteract || canAll)
			return;// Можно всё

		MovingObjectPosition t = e.target;

		Block b = e.player.getEntityWorld().getBlock(t.blockX, t.blockY, t.blockZ);

		if (isChest(b)) {
			if (!canChestAcces)
				e.setCanceled(true);
			else
				return;
		}

		if (isUsable(b)) {
			if (!canUse)
				e.setCanceled(true);
			else
				return;
		}

		if (!canPlace)
			e.setCanceled(true);

	}

	@SubscribeEvent
	public void onBreak(PlayerEvent.BreakSpeed e) {

		if (!canBreak && !canInteract && !canAll)
			e.setCanceled(true);

	}

	

	@SubscribeEvent
	public void onUse(PlayerInteractEvent e) {

		if (canInteract || canAll)
			return; // При интеракте можно кликать чем угодно

		ItemStack i = e.entityPlayer.getHeldItem();
		
		if(i != null)
	    if(i.getItem() instanceof ItemFood) return;//Зачем запрещать жрать? ...
	  
		
		Block b = e.entityPlayer.getEntityWorld().getBlock(e.x, e.y, e.z);

		if (isChest(b)) {
			if (!canChestAcces)
				e.setCanceled(true);
			else
				return;
		}

		if (isUsable(b)) {
			if (!canUse)
				e.setCanceled(true);
			else
				return;
		}

		if (!canPlace)
			e.setCanceled(true);

	}

	public boolean isUsable(Block b) {

		String m = b.getUnlocalizedName();

		if (m.equals("tile.enderChest") || m.equals("tile.workbench") || m.equals("tile.doorWood")
				|| m.equals("tile.anvil") || m.equals("tile.lever") || m.equals("tile.button"))
			return true;
		return false;
	}

	public boolean isChest(Block b) {

		String m = b.getUnlocalizedName();

		if (m.equals("tile.chestTrap") || m.equals("tile.chest"))
			return true;
		return false;
	}
}
