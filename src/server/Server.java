package server;

import database.database;
import utils.Constants;
import remote_objects.Client.ClientCallback;
import remote_objects.Server.ServerResponse;
import network.*;
import org.apache.commons.cli.*;
import server.handlers.*;

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
            switch (query.getRequestChoice()) {
                case Constants.VIEW_ALL_FACILITIES:
                    ViewAllFacilities.handleRequest(network, origin, database, query);
                    break;
                case Constants.FACILITY_AVAILABILITY:
                    FacilitiesAvailability.handleRequest(network, origin, database, query);
                    break;

                case Constants.FACILITY_BOOKING:
                    FacilityBooking.handleRequest(network, origin, database, query);
                    break;

                case Constants.OFFSET_BOOKING:
                    OffsetBooking.handleRequest(network, origin, database, query);
                    break;
                case Constants.FACILITY_MONITORING:
                    ClientCallback cInfo = new ClientCallback(query.getId(), origin, query.getMonitoringDuration());
                    database.registerMonitoring(query.getBookings().get(0).getName(), cInfo);
                    break;
                case Constants.SHORTEN_BOOKING:
                    ShortenBooking.handleRequest(network, origin, database, query);
                    break;
                case Constants.MONITOR_AND_BOOK_ON_AVAILABLE:
                    MonitorAndBookOnVacancy.handleRequest(network, origin, database, query);
                    break;
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
        failureRate = 0.5;

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
