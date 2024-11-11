package net.puffish.snakemod;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.puffish.snakemod.config.SnakeConfig;
import net.puffish.snakemod.event.SnakeEvents;
import net.puffish.snakemod.game.phase.SnakeWaitingPhase;
import xyz.nucleoid.plasmid.api.game.GameCloseReason;
import xyz.nucleoid.plasmid.api.game.GameType;
import xyz.nucleoid.plasmid.api.game.GameSpaceManager;

public class SnakeMod implements ModInitializer {
	public static final String ID = "snake";

	@Override
	public void onInitialize() {
		ServerTickEvents.START_WORLD_TICK.register(world -> {
			var game = GameSpaceManager.get().byWorld(world);
			if (game != null) {
				try {
					game.getBehavior().propagatingInvoker(SnakeEvents.TICK_START).onTickStart();
				} catch (Throwable t) {
					game.close(GameCloseReason.ERRORED);
				}
			}
		});

		GameType.register(
				createIdentifier("snake"),
				SnakeConfig.CODEC,
				SnakeWaitingPhase::open
		);
	}

	public static Identifier createIdentifier(String path) {
		return Identifier.of(ID, path);
	}

	public static MutableText createTranslatable(String type, String path, Object... args) {
		return Text.translatable(Util.createTranslationKey(type, createIdentifier(path)), args);
	}
}
