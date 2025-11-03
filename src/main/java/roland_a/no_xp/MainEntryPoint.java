package roland_a.no_xp;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MainEntryPoint implements ModInitializer {
	public static final String MOD_ID = MainEntryPoint.class.getPackage().getName().split("\\.")[1];

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {}
}
