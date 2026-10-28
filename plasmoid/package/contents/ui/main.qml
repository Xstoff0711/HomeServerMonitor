import QtQuick
import org.kde.plasma.plasmoid
import org.kde.plasma.components as PlasmaComponents
import org.kde.plasma.core as PlasmaCore
import org.kde.kirigami as Kirigami

PlasmoidItem {
    id: root

    property var serverStatuses: []
    property bool addFormVisible: false

    preferredRepresentation: fullRepresentation

    // Plasma soll KEINEN eigenen großen Hintergrund zeichnen.
    Plasmoid.backgroundHints: PlasmaCore.Types.NoBackground

    function loadStatuses() {
        let request = new XMLHttpRequest()

        request.onreadystatechange = function() {
            if (request.readyState === XMLHttpRequest.DONE
                    && request.status === 200) {
                root.serverStatuses = JSON.parse(request.responseText)
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

    fullRepresentation: Item {
        id: popup

        implicitWidth: 420
        implicitHeight: card.height

        /*
         * Unser EIGENER Hintergrund.
         * Seine Höhe hängt ausschließlich vom Inhalt ab.
         */
        Rectangle {
            id: card

            width: 420
            height: contentColumn.implicitHeight

            radius: 6

            color: Kirigami.Theme.backgroundColor
            border.color: Kirigami.Theme.separatorColor
            border.width: 1

            Column {
                id: contentColumn

                width: parent.width

                padding: 16
                spacing: 8

                readonly property real usableWidth:
                    width - leftPadding - rightPadding

                PlasmaComponents.Label {
                    width: contentColumn.usableWidth

                    text: "HomeServer Monitor"
                    font.bold: true
                }

                Repeater {
                    model: root.serverStatuses

                    Row {
                        required property var modelData

                        width: contentColumn.usableWidth
                        spacing: 8

                        Rectangle {
                            width: 10
                            height: 10
                            radius: 5

                            anchors.verticalCenter: parent.verticalCenter

                            color: modelData.online
                                ? Kirigami.Theme.positiveTextColor
                                : Kirigami.Theme.negativeTextColor
                        }

                        PlasmaComponents.Label {
                            width: 210

                            text: modelData.name
                        }

                        PlasmaComponents.Label {
                            width: 70

                            text: modelData.online
                                ? "ONLINE"
                                : "OFFLINE"
                        }

                        PlasmaComponents.Label {
                            width: 65

                            horizontalAlignment: Text.AlignRight

                            text: modelData.responseTimeMs !== null
                                ? modelData.responseTimeMs + " ms"
                                : "-"
                        }
                    }
                }

                PlasmaComponents.Button {
                    width: contentColumn.usableWidth

                    text: root.addFormVisible
                        ? "Abbrechen"
                        : "+ Server hinzufügen"

                    onClicked: {
                        root.addFormVisible =
                            !root.addFormVisible
                    }
                }

                Column {
                    id: addServerForm

                    visible: root.addFormVisible

                    width: contentColumn.usableWidth
                    spacing: 8

                    PlasmaComponents.TextField {
                        id: nameField

                        width: parent.width
                        placeholderText: "Name"
                    }

                    PlasmaComponents.TextField {
                        id: hostField

                        width: parent.width
                        placeholderText: "IP / Hostname"
                    }

                    PlasmaComponents.TextField {
                        id: portField

                        width: parent.width
                        placeholderText: "Port"

                        inputMethodHints: Qt.ImhDigitsOnly

                        validator: IntValidator {
                            bottom: 1
                            top: 65535
                        }
                    }

                    PlasmaComponents.ComboBox {
                        id: checkTypeField

                        width: parent.width

                        model: [
                            "HTTP",
                            "TCP"
                        ]
                    }

                    PlasmaComponents.TextField {
                        id: pathField

                        visible:
                            checkTypeField.currentText === "HTTP"

                        width: parent.width

                        placeholderText: "HTTP-Pfad"
                        text: "/"
                    }

                    PlasmaComponents.Button {
                        width: parent.width
                        text: "Hinzufügen"

                        onClicked: {
                            console.log(
                                "Server:",
                                nameField.text,
                                hostField.text,
                                portField.text,
                                checkTypeField.currentText,
                                pathField.text
                            )
                        }
                    }
                }
            }
        }
    }
}