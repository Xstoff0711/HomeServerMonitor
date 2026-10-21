package de.xstoff.homeserver;

import java.nio.file.Path;

public final class ConfigPaths {

    private ConfigPaths() {
    }

    public static Path getServersConfigPath() {
        String xdgConfigHome = System.getenv("XDG_CONFIG_HOME");

        Path configHome;

        if (xdgConfigHome != null && !xdgConfigHome.isBlank()) {
            configHome = Path.of(xdgConfigHome);
        } else {
            configHome = Path.of(
                    System.getProperty("user.home"),
                    ".config"
            );
        }

        return configHome
                .resolve("homeserver-monitor")
                .resolve("servers.txt");
    }
}