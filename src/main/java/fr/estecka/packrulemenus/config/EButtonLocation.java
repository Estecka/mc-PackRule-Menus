package fr.estecka.packrulemenus.config;

import net.minecraft.network.chat.Component;

@Deprecated
public enum EButtonLocation
{
	NONE("none"),
	OPTIONS_HEADER("options_header"),
	OPTIONS_BODY("options_body"),
	;

	private final String name;

	private EButtonLocation(String name){
		this.name = name;
	}

	static public EButtonLocation parse(String name)
	throws IllegalArgumentException
	{
		for (EButtonLocation e : values())
			if (e.toString().equals(name))
				return e;

		throw new IllegalArgumentException("Invalid enum value:" + name);
	}

	@Override
	public String toString(){
		return this.name;
	}

	public String TranslationKey() {
		return "packrulemenus.config.buttonlocation." + this.name;
	}

	public Component TranslatableName() {
		return Component.translatable(this.TranslationKey());
	}

}
