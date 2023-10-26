package tk.estecka.packrulemenus.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import net.minecraft.resource.DataPackSettings;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;

@Mixin(MinecraftServer.class)
public interface IMinecraftServerMixin 
{
	@Invoker static	public DataPackSettings callCreateDataPackSettings(ResourcePackManager manager){ throw new AssertionError(); }
}
