import QtQuick
import org.kde.kirigami as Kirigami
import org.kde.plasma.components as PlasmaComponents
import org.kde.plasma.core as PlasmaCore
import org.kde.plasma.plasmoid

PlasmoidItem {
    id: root

    property var serverStatuses: []
    property bool addFormVisible: false

    function loadStatuses() {
        let request = new XMLHttpRequest();
        request.onreadystatechange = function() {
            if (request.readyState === XMLHttpRequest.DONE && request.status === 200)
                root.serverStatuses = JSON.parse(request.responseText);

        };
        request.open("GET", "http://localhost:8085/status?t=" + Date.now());
        request.send();
    }

    function deleteServer(server) {
        let request = new XMLHttpRequest();
        let params = "name=" + encodeURIComponent(server.name) + "&host=" + encodeURIComponent(server.host) + "&port=" + encodeURIComponent(server.port) + "&checkType=" + encodeURIComponent(server.checkType) + "&path=" + encodeURIComponent(server.path);
        let url = "http://localhost:8085/servers?" + params;
        console.log("Lösche Server:", server.name);
        request.onreadystatechange = function() {
            if (request.readyState !== XMLHttpRequest.DONE)
                return ;

            console.log("DELETE Antwort:", request.status, request.responseText);
            if (request.status === 200) {
                console.log("Server erfolgreich gelöscht");
                root.loadStatuses();
            } else {
                console.log("Server konnte nicht gelöscht werden:", request.status, request.responseText);
            }
        };
        request.open("DELETE", url);
        request.send();
    }

    preferredRepresentation: fullRepresentation
    // Plasma soll KEINEN eigenen großen Hintergrund zeichnen.
    Plasmoid.backgroundHints: PlasmaCore.Types.NoBackground

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

                readonly property real usableWidth: width - leftPadding - rightPadding

                width: parent.width
                padding: 16
                spacing: 8

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
                            color: modelData.online ? Kirigami.Theme.positiveTextColor : Kirigami.Theme.negativeTextColor
                        }

                        PlasmaComponents.Label {
                            width: 170
                            text: modelData.name
                        }

                        PlasmaComponents.Label {
                            width: 70
                            text: modelData.online ? "ONLINE" : "OFFLINE"
                        }

                        PlasmaComponents.Label {
                            width: 65
                            horizontalAlignment: Text.AlignRight
                            text: modelData.responseTimeMs !== null ? modelData.responseTimeMs + " ms" : "-"
                        }

                        PlasmaComponents.Button {
                            text: "🗑"
                            onClicked: {
                                root.deleteServer(modelData);
                            }
                        }

                    }

                }

                PlasmaComponents.Button {
                    width: contentColumn.usableWidth
                    text: root.addFormVisible ? "Abbrechen" : "+ Server hinzufügen"
                    onClicked: {
                        root.addFormVisible = !root.addFormVisible;
                    }
                }

                Column {
                    id: addServerForm

                    function submitServer() {
                        let request = new XMLHttpRequest();
                        request.onreadystatechange = function() {
                            if (request.readyState !== XMLHttpRequest.DONE)
                                return ;

                            if (request.status === 201) {
                                console.log("Server erfolgreich hinzugefügt");
                                nameField.clear();
                                hostField.clear();
                                portField.clear();
                                checkTypeField.currentIndex = 0;
                                pathField.text = "/";
                                root.addFormVisible = false;
                                root.loadStatuses();
                            } else {
                                console.log("Server konnte nicht hinzugefügt werden:", request.status, request.responseText);
                            }
                        };
                        request.open("POST", "http://localhost:8085/servers");
                        request.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                        let body = "name=" + encodeURIComponent(nameField.text.trim()) + "&host=" + encodeURIComponent(hostField.text.trim()) + "&port=" + encodeURIComponent(portField.text.trim()) + "&checkType=" + encodeURIComponent(checkTypeField.currentText);
                        if (checkTypeField.currentText === "HTTP")
                            body += "&path=" + encodeURIComponent(pathField.text.trim());

                        request.send(body);
                    }

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
                        model: ["HTTP", "TCP"]
                    }

                    PlasmaComponents.TextField {
                        id: pathField

                        visible: checkTypeField.currentText === "HTTP"
                        width: parent.width
                        placeholderText: "HTTP-Pfad"
                        text: "/"
                    }

                    PlasmaComponents.Button {
                        width: parent.width
                        text: "Hinzufügen"
                        enabled: nameField.text.trim() !== "" && hostField.text.trim() !== "" && portField.acceptableInput
                        onClicked: {
                            onClicked:
                            {
                                addServerForm.submitServer();
                            };
                        }
                    }

                }

            }

        }

    }

}
