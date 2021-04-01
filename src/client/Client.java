package client;

import client.handlers.*;
import semantics.*;
import org.apache.commons.cli.*;
import utils.Constants;

import java.net.InetSocketAddress;
import java.util.Scanner;


public class Client {
    private final Semantics semInvo;
    private static boolean run = true;

    public Client(Semantics semInvo) {
        this.semInvo = semInvo;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        ClientUI.MenuMessage();

        int serviceType;
        String message = scanner.nextLine();
        try {
            serviceType = Integer.parseInt(message);
        } catch (Exception e) {
            serviceType = 0;
        }
        System.out.println();

        try {
            switch (serviceType) {
                case Constants.VIEW_ALL_FACILITIES -> ViewAllFacilities.createAndSendMessage(semInvo);
                case Constants.FACILITY_AVAILABILITY -> FacilitiesAvailability.createAndSendMessage(semInvo, scanner);
                case Constants.FACILITY_BOOKING -> FacilityBooking.createAndSendMessage(semInvo, scanner);
                case Constants.OFFSET_BOOKING -> OffsetBooking.createAndSendMessage(semInvo, scanner);
                case Constants.FACILITY_MONITORING -> FacilityMonitoring.createAndSendMessage(semInvo, scanner);
                case Constants.SHORTEN_BOOKING -> ShortenBooking.createAndSendMessage(semInvo, scanner);
                case Constants.MONITOR_AND_BOOK_ON_AVAILABLE -> MonitorAndBookOnVacancy.createAndSendMessage(semInvo, scanner);
                case Constants.SERVICE_EXIT -> {
                    System.out.println(ClientUI.EXIT_SYSTEM_MESSAGE);
                    run = false;
                }
                default -> {
                    System.out.println(ClientUI.UNKNOWN_INPUT_MESSAGE);
                    System.out.println();
                }
            }

        } catch (Exception e) {
            System.out.println(ClientUI.LINE_SEPARATOR);
            System.out.printf(ClientUI.ERROR_MESSAGE, e.getMessage());
        }

    }

    public static void main(String[] args) {

        Options options = new Options();

        Option hostAddress = new Option("HOST",true, "Server host");
        Option portNumber = new Option("PORT", true, "Server port");
        Option ALOSemantics = new Option("ALO", false, "Configures the request-reply protocol to use least once semantics invocation");
        Option AMOSemantics = new Option("AMO", false, "Configures the request-reply protocol to use at most once semantics invocation");
        Option failureRate = new Option("FRATE", true, "Failure rate of the simulated UDP environment (0-1)");
        Option timeout = new Option("TIMEOUT", true, "Timeout duration in request-reply protocol");
        Option maxRetransmissions = new Option("MR", true, "Maximum number of retries in request-reply protocol");
        Option verbose = new Option("V", false, "Set debugging statements");

        // required args
        hostAddress.setRequired(true);
        portNumber.setRequired(true);

        // non string args
        portNumber.setType(Integer.TYPE);
        failureRate.setType(Double.TYPE);
        timeout.setType(Integer.TYPE);
        maxRetransmissions.setType(Integer.TYPE);

        // set up
        options.addOption(portNumber);
        options.addOption(hostAddress);
        options.addOption(ALOSemantics);
        options.addOption(AMOSemantics);
        options.addOption(failureRate);
        options.addOption(timeout);
        options.addOption(maxRetransmissions);
        options.addOption(verbose);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        String host;
        int port;

        double fr = Constants.DEFAULT_FAILURE_RATE;

        try {
            cmd = parser.parse(options, args);
            host = cmd.getOptionValue("HOST");
            port = Integer.parseInt(cmd.getOptionValue("PORT"));

            cmd.hasOption("ALO");
            cmd.hasOption("AMO");

            if (cmd.hasOption("FRATE")) {
                fr = Double.parseDouble(cmd.getOptionValue("FRATE"));
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("UDPClient", options);

            System.exit(1);
            return;
        }

        try {
            Client client;
            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            UdpAgent communicator = new UdpAgentWithFailures(socketAddress, fr);

            System.out.print(ClientUI.LINE_SEPARATOR);
            System.out.println(ClientUI.STARTING_MESSAGE);
            System.out.println(ClientUI.LINE_SEPARATOR);

            client = new Client(new AtLeastOnceSemantics(communicator));

            while (run) {
                client.run();
            }

        } catch (Exception e) {
            System.out.print(ClientUI.LINE_SEPARATOR);
            System.out.print(ClientUI.ERROR_MESSAGE);
            e.printStackTrace();
            System.out.println(ClientUI.EXIT_SYSTEM_MESSAGE);
            System.out.println(ClientUI.LINE_SEPARATOR);
        }
    }
}
