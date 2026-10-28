# HomeServer Monitor

HomeServer Monitor is a small Java-based monitoring tool for home servers and homelab services.

It checks configured services via HTTP or TCP, measures their response time, and exposes the current status through a local JSON API.

A KDE Plasma 6 widget displays these results directly on the desktop and updates them automatically.

Typical services can be:

- Jellyfin
- Nextcloud
- Pi-hole
- other self-hosted services

The project is intended as a lightweight way to see at a glance whether important home server services are online and how quickly they respond.

## Current features

- HTTP and TCP health checks
- custom HTTP paths
- response-time measurement
- parallel server checks
- scheduled monitoring
- local JSON API
- KDE Plasma 6 widget
- automatic status refresh
- external Linux/XDG configuration

The project is currently under active development.