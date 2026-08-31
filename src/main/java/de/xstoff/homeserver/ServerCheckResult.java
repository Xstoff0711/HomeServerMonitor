package de.xstoff.homeserver;

import java.util.OptionalLong;

public record ServerCheckResult(boolean online, OptionalLong responseTimeNanos) {

}
