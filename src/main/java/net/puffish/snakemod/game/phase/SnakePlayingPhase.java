package net.puffish.snakemod.game.phase;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.puffish.snakemod.SnakeMod;
import net.puffish.snakemod.game.FoodManager;
import net.puffish.snakemod.game.ScoreboardManager;
import net.puffish.snakemod.game.SnakeManager;
import net.puffish.snakemod.game.map.SnakeMap;
import xyz.nucleoid.plasmid.api.game.GameSpace;

public class SnakePlayingPhase extends SnakeActivePhase {
	private final int minAliveCount;

	public SnakePlayingPhase(GameSpace gameSpace, ServerWorld world, SnakeMap map, SnakeManager snakeManager, FoodManager foodManager, ScoreboardManager scoreboardManager, int minAliveCount) {
		super(gameSpace, world, map, snakeManager, foodManager, scoreboardManager);
		this.minAliveCount = minAliveCount;
	}

	public static SnakePlayingPhase create(GameSpace gameSpace, SnakeActivePhase oldPhase) {
		return new SnakePlayingPhase(
				gameSpace,
				oldPhase.world,
				oldPhase.map,
				oldPhase.snakeManager,
				oldPhase.foodManager,
				oldPhase.scoreboardManager,
				oldPhase.snakeManager.getCount() == 1 ? 1 : 2
		);
	}

	public static void open(SnakeActivePhase oldPhase) {
		oldPhase.gameSpace.setActivity(activity -> {
			var phase = SnakePlayingPhase.create(activity.getGameSpace(), oldPhase);

			phase.applyRules(activity);
			phase.applyListeners(activity);
		});
	}

	protected void tick() {
		foodManager.tick(snakeManager.getAliveSnakes());
		snakeManager.tickPlaying(this::eliminate);

		super.tick();

		if (snakeManager.getAliveCount() < minAliveCount) {
			SnakeEndingPhase.open(this);
		}
	}

	private void eliminate(ServerPlayerEntity killer, ServerPlayerEntity player) {
		if (killer == player) {
			gameSpace.getPlayers().sendMessage(
					SnakeMod.createTranslatable(
							"text",
							"eliminated",
							Text.empty().formatted(Formatting.WHITE).append(player.getDisplayName())
					).formatted(Formatting.DARK_AQUA)
			);
		} else {
			gameSpace.getPlayers().sendMessage(
					SnakeMod.createTranslatable(
							"text",
							"eliminated.by",
							Text.empty().formatted(Formatting.WHITE).append(player.getDisplayName()),
							Text.empty().formatted(Formatting.WHITE).append(killer.getDisplayName())
					).formatted(Formatting.DARK_AQUA)
			);
		}
	}
}
