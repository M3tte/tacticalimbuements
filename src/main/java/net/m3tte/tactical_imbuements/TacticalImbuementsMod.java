/*
 *    MCreator note:
 *
 *    If you lock base mod element files, you can edit this file and it won't get overwritten.
 *    If you change your modid or package, you need to apply these changes to this file MANUALLY.
 *
 *    Settings in @Mod annotation WON'T be changed in case of the base mod element
 *    files lock too, so you need to set them manually here in such case.
 *
 *    If you do not lock base mod element files in Workspace settings, this file
 *    will be REGENERATED on each build.
 *
 */
package net.m3tte.tactical_imbuements;

import net.m3tte.tactical_imbuements.EpicFight.ImbuementAnims;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsMobEffects;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import net.m3tte.tactical_imbuements.init.TacticalImbuementsModParticleTypes;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsModItems;
import net.m3tte.tactical_imbuements.init.TacticalImbuementsModEntities;

import java.util.function.Supplier;
import java.util.function.Function;
import java.util.function.BiConsumer;

@Mod("tactical_imbuements")
public class TacticalImbuementsMod {
	public static final Logger LOGGER = LogManager.getLogger(TacticalImbuementsMod.class);
	public static final String MODID = "tactical_imbuements";
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel PACKET_HANDLER = NetworkRegistry.newSimpleChannel(new ResourceLocation(MODID, MODID), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	private static int messageID = 0;

	public TacticalImbuementsMod() {

		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

		TacticalImbuementsModItems.REGISTRY.register(bus);
		TacticalImbuementsModEntities.REGISTRY.register(bus);
		TacticalImbuementsMobEffects.REGISTRY.register(bus);
		bus.addListener(ImbuementAnims::registerAnimations);
		bus.addListener(this::addModItems);
		TacticalImbuementsModParticleTypes.REGISTRY.register(bus);
	}

	public static <T> void addNetworkMessage(Class<T> messageType, BiConsumer<T, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, T> decoder, BiConsumer<T, Supplier<NetworkEvent.Context>> messageConsumer) {
		PACKET_HANDLER.registerMessage(messageID, messageType, encoder, decoder, messageConsumer);
		messageID++;
	}

	public void addModItems(BuildCreativeModeTabContentsEvent event) {
		if (event.getTabKey() == CreativeModeTabs.COMBAT) {
			event.accept(TacticalImbuementsModItems.FLASK_OF_FLAME::get);
			event.accept(TacticalImbuementsModItems.FLASK_OF_ICE::get);
			event.accept(TacticalImbuementsModItems.FLASK_OF_SPARKS::get);
			event.accept(TacticalImbuementsModItems.FLASK_OF_VENOM::get);
		}

		if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
			event.accept(TacticalImbuementsModItems.FLASK::get);
		}
	}
}
