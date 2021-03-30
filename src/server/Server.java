package server;

import constants.Constants;
import entity.ClientCallbackInfo;
import entity.ServerResponse;
import network.*;
import org.apache.commons.cli.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import server.handlers.*;

public class Server {
    private Network network;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);
    ServerResponse response;

    public Server(Network network) {
        this.network = network;
    }

    public void run() {
        System.out.println("Running Server...");
        ServerDB database = new ServerDB();
        System.out.println("Server Seeded...");

        network.receive((origin, query) -> {
            switch (query.getType()) {
                case Constants.VIEW_ALL_FACILITIES:
                    System.out.println("request to view all facilities");
                    ViewAllFacilities.handleRequest(network, origin, database, query);
                    break;
                case Constants.FACILITY_AVAILABILITY:
                    System.out.println("request to check facilities availability");
                    FacilitiesAvailability.handleRequest(network, origin, database, query);
                    break;

                case Constants.BOOK_FACILITY:
                    System.out.println("request to book facility");
                    FacilityBooking.handleRequest(network, origin, database, query);
                    break;

                case Constants.CHANGE_BOOKING:
                    OffsetBooking.handleRequest(network, origin, database, query);
                    break;
                case Constants.MONITOR_BOOKING:
                    // we handle monitorAvailability(facility name, monitor interval) - callback
                    System.out.println("request to monitor");
                    ClientCallbackInfo cInfo = new ClientCallbackInfo(query.getId(), origin, query.getMonitoringDuration());
                    database.registerMonitoring(query.getBookings().get(0).getName(), cInfo);
                    break;
                case Constants.SHORTEN_BOOKING:
                    System.out.println("request to shorten booking");
                    ShortenBooking.handleRequest(network, origin, database, query);
                    break;
                case Constants.BOOK_ON_VACANCY:
                    System.out.println("request to book on vacancy");
                    MonitorAndBookOnVacancy.handleRequest(network, origin, database, query);
                    break;
                case 11:
                    System.out.println("Get all bookings.");
                    database.getAllBookings();
                default:
                    response = new ServerResponse(query.getId(), 404, null);
                    network.send(response, origin);
                    break;
            }
        });
    }

    public static void main(String[] args) {
        Options options = new Options();

        Option opHost = new Option("h", "host", true, "Server host");
        options.addOption(opHost);

        Option opPort = new Option("p", "port", true, "Server port");
        opPort.setRequired(true);
        opPort.setType(Integer.TYPE);
        options.addOption(opPort);

        Option opAtLeastOnce = new Option("al", "atleast", false, "Enable at least once invocation semantic");
        options.addOption(opAtLeastOnce);

        Option opAtMostOnce = new Option("am", "atmost", false, "Enable at most once invocation semantic");
        options.addOption(opAtMostOnce);

        Option opFailureRate = new Option("fr", "failurerate", true, "Set failure rate (float)");
        opFailureRate.setType(Double.TYPE);
        options.addOption(opFailureRate);

        Option opTimeout = new Option("to", "timeout", true, "Set timeout in millisecond");
        opTimeout.setType(Integer.TYPE);
        options.addOption(opTimeout);

        Option opTimeoutCount = new Option("mt", "maxtimeout", true, "Set timeout max count");
        opTimeoutCount.setType(Integer.TYPE);
        options.addOption(opTimeoutCount);

        Option opDebug = new Option("v", "verbose", false, "Enable verbose print for debugging");
        options.addOption(opDebug);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        String host = Constants.DEFAULT_HOST;
        int port = Constants.DEFAULT_PORT;

        boolean atLeastOnce = false;
        boolean atMostOnce = false;
        double failureRate = Constants.DEFAULT_FAILURE_RATE;
        int timeout = Constants.DEFAULT_NO_TIMEOUT;
        int maxTimeout = Constants.DEFAULT_MAX_TIMEOUT;

        try {
            cmd = parser.parse(options, args);
            host = cmd.getOptionValue("host");
            port = Integer.parseInt(cmd.getOptionValue("port"));
            if (cmd.hasOption("failurerate")) {
                failureRate = Double.parseDouble(cmd.getOptionValue("failurerate"));
            }
            atLeastOnce = cmd.hasOption("atleast");
            atMostOnce = cmd.hasOption("atmost");

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("UDPClient", options);
            System.exit(1);
            return;
        }

        try {
            Server server;
            UDPCommunicator communicator = new PoorUDPCommunicator(port, failureRate);

            if (atLeastOnce) {
                server = new Server(new AtLeastOnceNetwork(communicator));
                System.out.println("At least once network");
            } else if (atMostOnce) {
                server = new Server(new AtMostOnceNetwork(communicator));
                System.out.println("At most once network");
            } else {
                server = new Server(new AtLeastOnceNetwork(communicator));
                System.out.println("Defaults to at least once network");
            }


            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
