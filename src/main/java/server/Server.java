package main.java.server;

import main.java.database.database;
import main.java.utils.Constants;
import main.java.remote_objects.Client.ClientCallback;
import main.java.remote_objects.Server.ServerResponse;
import main.java.network.*;
import org.apache.commons.cli.*;
import main.java.server.handlers.*;

public class Server {
    private Network network;
    ServerResponse response;

    public Server(Network network) {
        this.network = network;
    }

    public void run() {
        database database = new database();
        System.out.println("Database Initialised ...");

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

                case Constants.FACILITY_BOOKING:
                    System.out.println("request to book facility");
                    FacilityBooking.handleRequest(network, origin, database, query);
                    break;

                case Constants.OFFSET_BOOKING:
                    OffsetBooking.handleRequest(network, origin, database, query);
                    break;
                case Constants.FACILITY_MONITORING:
                    // we handle monitorAvailability(facility name, monitor interval) - callback
                    System.out.println("request to monitor");
                    ClientCallback cInfo = new ClientCallback(query.getId(), origin, query.getMonitoringDuration());
                    database.registerMonitoring(query.getBookings().get(0).getName(), cInfo);
                    break;
                case Constants.SHORTEN_BOOKING:
                    System.out.println("request to shorten booking");
                    ShortenBooking.handleRequest(network, origin, database, query);
                    break;
                case Constants.MONITOR_AND_BOOK_ON_AVAILABLE:
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
        CommandLine cmd;


        double failureRate = Constants.DEFAULT_FAILURE_RATE;

        int port;
        boolean atMostOnce, atLeastOnce;
        try {
            cmd = parser.parse(options, args);
            port = Integer.parseInt(cmd.getOptionValue("port"));
            if (cmd.hasOption("failurerate")) {
                failureRate = Double.parseDouble(cmd.getOptionValue("failurerate"));
            }
            atLeastOnce = cmd.hasOption("atleast");
            atMostOnce = cmd.hasOption("atmost");

        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        try {
            Server server;
            UdpAgent communicator = new UdpAgentWithFailures(port, failureRate);

            if (atLeastOnce) {
                server = new Server(new AtLeastOnceNetwork(communicator));
            } else {
                server = new Server(new AtMostOnceNetwork(communicator));
            }

            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
