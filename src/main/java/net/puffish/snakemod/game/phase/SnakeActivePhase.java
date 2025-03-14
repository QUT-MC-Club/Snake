package net.puffish.snakemod.game.phase;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import net.puffish.snakemod.SnakeMod;
import net.puffish.snakemod.event.SnakeEvents;
import net.puffish.snakemod.game.FoodManager;
import net.puffish.snakemod.game.ScoreboardManager;
import net.puffish.snakemod.game.SnakeManager;
import net.puffish.snakemod.game.map.SnakeMap;
import xyz.nucleoid.plasmid.api.game.GameActivity;
import xyz.nucleoid.plasmid.api.game.GameSpace;
import xyz.nucleoid.plasmid.api.game.event.GamePlayerEvents;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptor;
import xyz.nucleoid.plasmid.api.game.player.JoinAcceptorResult;

import java.util.Random;

public abstract class SnakeActivePhase extends SnakePhase {
	protected final SnakeManager snakeManager;
	protected final FoodManager foodManager;
	protected final ScoreboardManager scoreboardManager;

	protected final Random random = new Random();

	protected SnakeActivePhase(GameSpace gameSpace, ServerWorld world, SnakeMap map, SnakeManager snakeManager, FoodManager foodManager, ScoreboardManager scoreboardManager) {
		super(gameSpace, world, map);
		this.snakeManager = snakeManager;
		this.foodManager = foodManager;
		this.scoreboardManager = scoreboardManager;
	}

	protected void applyListeners(GameActivity activity) {
		activity.listen(GamePlayerEvents.ACCEPT, this::acceptPlayer);
		activity.listen(GamePlayerEvents.LEAVE, this::leavePlayer);
		activity.listen(GamePlayerEvents.ADD, this::addPlayer);
		activity.listen(GamePlayerEvents.REMOVE, this::removePlayer);
		activity.listen(SnakeEvents.TICK_START, this::tick);
	}

	protected void tick() {
		scoreboardManager.set((player, builder) -> {
			var optSnake = snakeManager.getSnake(player);
			builder.add(
					SnakeMod.createTranslatable("sidebar", "alive")
							.formatted(Formatting.BOLD, Formatting.GREEN),
					Text.literal(Integer.toString(snakeManager.getAliveCount()))
							.formatted(Formatting.WHITE)
			);
			builder.add(
					SnakeMod.createTranslatable("sidebar", "dead")
							.formatted(Formatting.BOLD, Formatting.RED),
					Text.literal(Integer.toString(snakeManager.getDeadCount()))
							.formatted(Formatting.WHITE)
			);
			optSnake.ifPresent(snake -> {
				builder.add(
						SnakeMod.createTranslatable("sidebar", "kills")
								.formatted(Formatting.BOLD, Formatting.YELLOW),
						Text.literal(Integer.toString(snake.getKills()))
								.formatted(Formatting.WHITE));
				builder.add(
						SnakeMod.createTranslatable("sidebar", "length")
								.formatted(Formatting.BOLD, Formatting.AQUA),
						Text.literal(Integer.toString(snake.getLength()))
								.formatted(Formatting.WHITE)
				);
			});
		});
	}

	private JoinAcceptorResult acceptPlayer(JoinAcceptor acceptor) {
		return acceptor.teleport(
				this.world,
				this.map.getWaitingSpawns().get(random.nextInt(map.getWaitingSpawns().size()))
		).thenRunForEach(player -> player.changeGameMode(GameMode.SPECTATOR));
	}

	protected void leavePlayer(ServerPlayerEntity player) {
		snakeManager.removePlayer(player);
	}

	protected void addPlayer(ServerPlayerEntity player) {
		scoreboardManager.addPlayer(player);
	}

	protected void removePlayer(ServerPlayerEntity player) {
		scoreboardManager.removePlayer(player);
	}
}
