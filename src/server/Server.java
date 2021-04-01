package server;

import database.database;
import utils.Constants;
import remote_objects.Client.ClientCallback;
import remote_objects.Server.ServerResponse;
import semantics.*;
import org.apache.commons.cli.*;
import server.handlers.*;

public class Server {
    private Semantics semInvo;
    ServerResponse response;

    public Server(Semantics semInvo) {
        this.semInvo = semInvo;
    }

    public void run() {
        database database = new database();
        System.out.println("Database Initialised ...");

        semInvo.receiveClientRequest((origin, query) -> {
            switch (query.getRequestChoice()) {
                case Constants.VIEW_ALL_FACILITIES -> ViewAllFacilities.handleRequest(semInvo, origin, database, query);
                case Constants.FACILITY_AVAILABILITY -> FacilitiesAvailability.handleRequest(semInvo, origin, database, query);
                case Constants.FACILITY_BOOKING -> FacilityBooking.handleRequest(semInvo, origin, database, query);
                case Constants.OFFSET_BOOKING -> OffsetBooking.handleRequest(semInvo, origin, database, query);
                case Constants.FACILITY_MONITORING -> {
                    ClientCallback cInfo = new ClientCallback(query.getId(), origin, query.getMonitoringDuration() * 1000);
                    database.registerMonitoring(query.getBookings().get(0).getName(), cInfo);
                }
                case Constants.SHORTEN_BOOKING -> ShortenBooking.handleRequest(semInvo, origin, database, query);
                case Constants.MONITOR_AND_BOOK_ON_AVAILABLE -> MonitorAndBookOnVacancy.handleRequest(semInvo, origin, database, query);
                default -> {
                    response = new ServerResponse(query.getId(), 404, null);
                    semInvo.replyClient(response, origin);
                }
            }
        });
    }

    public static void main(String[] args) {
        Options options = new Options();

        Option portNumber = new Option("PORT", true, "Server port");
        Option ALOSemantics = new Option("ALO", false, "Configures the request-reply protocol to use least once semantics invocation");
        Option AMOSemantics = new Option("AMO", false, "Configures the request-reply protocol to use at most once semantics invocation");
        Option failureRate = new Option("FRATE", true, "Failure rate of the simulated UDP environment (0-1)");
        Option timeout = new Option("TIMEOUT", true, "Timeout duration in request-reply protocol");
        Option maxRetransmissions = new Option("MR", true, "Maximum number of retries in request-reply protocol");
        Option verbose = new Option("V", false, "Set debugging statements");

        // required args
        portNumber.setRequired(true);

        // non string args
        portNumber.setType(Integer.TYPE);
        failureRate.setType(Double.TYPE);
        timeout.setType(Integer.TYPE);
        maxRetransmissions.setType(Integer.TYPE);

        // set up
        options.addOption(portNumber);
        options.addOption(ALOSemantics);
        options.addOption(AMOSemantics);
        options.addOption(failureRate);
        options.addOption(timeout);
        options.addOption(maxRetransmissions);
        options.addOption(verbose);

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd;


        double fr = Constants.DEFAULT_FAILURE_RATE;
        fr = 0.5;

        int port;
        boolean atLeastOnce;
        try {
            cmd = parser.parse(options, args);
            port = Integer.parseInt(cmd.getOptionValue("PORT"));
            if (cmd.hasOption("FRATE")) {
                fr = Double.parseDouble(cmd.getOptionValue("FRATE"));
            }
            atLeastOnce = cmd.hasOption("ALO");

        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(1);
            return;
        }

        try {
            Server server;
            UdpAgent communicator = new UdpAgentWithFailures(port, fr);

            if (atLeastOnce) {
                server = new Server(new AtLeastOnceSemantics(communicator));
            } else {
                server = new Server(new AtMostOnceSemantics(communicator));
            }

            server.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
