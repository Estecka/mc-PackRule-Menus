package fr.estecka.packrulemenus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.world.level.DataPackConfig;

@Mixin(MinecraftServer.class)
public interface IMinecraftServerMixin 
{
	@Invoker static	public DataPackConfig callGetSelectedPacks(PackRepository manager, boolean allowEnabling){ throw new AssertionError(); }
}
