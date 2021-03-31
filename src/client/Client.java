package client;

import client.handlers.*;
import semantics.*;
import org.apache.commons.cli.*;
import utils.Constants;
import utils.ProgramArgumentsHelper;

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

        Options options = ProgramArgumentsHelper.getOptions();
        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd;

        String host;
        int port;

        double failureRate = Constants.DEFAULT_FAILURE_RATE;

        // parse program arguments
        try {
            cmd = parser.parse(options, args);
            host = cmd.getOptionValue("host");
            port = Integer.parseInt(cmd.getOptionValue("port"));

            cmd.hasOption("atleast");
            cmd.hasOption("atmost");

            if (cmd.hasOption("failurerate")) {
                failureRate = Double.parseDouble(cmd.getOptionValue("failurerate"));
            }
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("UDPClient", options);

            System.exit(1);
            return;
        }

        // initialise client with program arguments
        try {
            Client client;
            InetSocketAddress socketAddress = new InetSocketAddress(host, port);
            UdpAgent communicator = new UdpAgentWithFailures(socketAddress, failureRate);

            System.out.print(ClientUI.LINE_SEPARATOR);
            System.out.println(ClientUI.STARTING_MESSAGE);
            System.out.println(ClientUI.LINE_SEPARATOR);

            // TODO - confirm that client only uses at least once
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
