package com.jyrion.server.model.command;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = PlayerCommand.Play.class, name = "play"),
        @JsonSubTypes.Type(value = PlayerCommand.Pause.class, name = "pause"),
        @JsonSubTypes.Type(value = PlayerCommand.TogglePause.class, name = "togglePause"),
        @JsonSubTypes.Type(value = PlayerCommand.Stop.class, name = "stop"),
        @JsonSubTypes.Type(value = PlayerCommand.SetVolume.class, name = "setVolume"),
        @JsonSubTypes.Type(value = PlayerCommand.Mute.class, name = "mute"),
        @JsonSubTypes.Type(value = PlayerCommand.Seek.class, name = "seek"),
        @JsonSubTypes.Type(value = PlayerCommand.QueueClear.class, name = "queueClear"),
        @JsonSubTypes.Type(value = PlayerCommand.QueueJump.class, name = "queueJump"),
        @JsonSubTypes.Type(value = PlayerCommand.QueueAdd.class, name = "queueAdd"),
})
public sealed interface PlayerCommand
        permits PlayerCommand.Play, PlayerCommand.Pause, PlayerCommand.TogglePause,
                PlayerCommand.Stop, PlayerCommand.SetVolume, PlayerCommand.Mute,
                PlayerCommand.Seek, PlayerCommand.QueueClear, PlayerCommand.QueueJump,
                PlayerCommand.QueueAdd {

    record Play() implements PlayerCommand {}

    record Pause() implements PlayerCommand {}

    record TogglePause() implements PlayerCommand {}

    record Stop() implements PlayerCommand {}

    record SetVolume(int volume) implements PlayerCommand {}

    record Mute(boolean muted) implements PlayerCommand {}

    record Seek(long positionMs) implements PlayerCommand {}

    record QueueClear() implements PlayerCommand {}

    record QueueJump(int index) implements PlayerCommand {}

    record QueueAdd(String uri) implements PlayerCommand {}
}
