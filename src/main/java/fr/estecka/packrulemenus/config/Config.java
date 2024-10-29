package fr.estecka.packrulemenus.config;

import java.util.HashMap;
import java.util.Map;
import fr.estecka.packrulemenus.config.ConfigIO.Property;

public class Config
extends ConfigIO.AFixedCoded
{
	public EButtonLocation buttonLocation = EButtonLocation.OPTIONS_HEADER;
	public boolean datapackConfirmation = true;

	@Override
	public Map<String, Property<?>> GetProperties(){
		return new HashMap<>(){{
			put("button.location", new Property<EButtonLocation>(()->buttonLocation, e->buttonLocation=e, EButtonLocation::parse, EButtonLocation::toString));
			put("datapack.askConfirmation", Property.Boolean(()->datapackConfirmation, b->datapackConfirmation=b));
		}};
	}

}
