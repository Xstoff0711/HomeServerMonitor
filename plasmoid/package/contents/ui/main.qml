import QtQuick
import org.kde.plasma.plasmoid
import org.kde.plasma.components as PlasmaComponents

PlasmoidItem {
    id: root

    property var serverStatuses: []

    function loadStatuses() {
        let request = new XMLHttpRequest()

        request.onreadystatechange = function() {
            if (request.readyState === XMLHttpRequest.DONE) {
                if (request.status === 200) {
                    serverStatuses = JSON.parse(request.responseText)
                }
            }
        }

        request.open("GET", "http://localhost:8085/status")
        request.send()
    }

    Timer {
        interval: 5000
        repeat: true
        running: true
        triggeredOnStart: true

        onTriggered: root.loadStatuses()
    }

    Column {
        spacing: 8

        PlasmaComponents.Label {
            text: "HomeServer Monitor"
        }

        Repeater {
            model: root.serverStatuses

            PlasmaComponents.Label {
                required property var modelData

                text: modelData.name
                    + " | "
                    + (modelData.online ? "ONLINE" : "OFFLINE")
                    + " | "
                    + (modelData.responseTimeMs !== null
                        ? modelData.responseTimeMs + " ms"
                        : "-")
            }
        }
    }
}