package de.xstoff.homeserver;

public class Server {
        private String name;
        private String ipAddress;
        private int port;
        private String status;

        public Server(String name, String ipAddress, int port, String status){
            this.name = name;
            this.ipAddress = ipAddress;
            this.port = port;
            this.status = status;
        }
        public String getName() {
            return name;
        }
        public String getIpAddress() {
            return ipAddress;
        }
        public int getPort() {
            return port;
        }
        public String getStatus() {
            return status;
        }

}
