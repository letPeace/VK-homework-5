package utils;

import org.jetbrains.annotations.NotNull;

public record Pair<V1, V2>(@NotNull V1 value1, @NotNull V2 value2) { }
