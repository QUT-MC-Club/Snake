package net.puffish.snakemod.config;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import xyz.nucleoid.plasmid.api.game.common.config.WaitingLobbyConfig;

public record SnakeConfig(WaitingLobbyConfig players, MapConfig map) {
	public static final MapCodec<SnakeConfig> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
			WaitingLobbyConfig.CODEC.fieldOf("players").forGetter(config -> config.players),
			MapConfig.CODEC.fieldOf("map").forGetter(SnakeConfig::map)
	).apply(instance, SnakeConfig::new));
}
