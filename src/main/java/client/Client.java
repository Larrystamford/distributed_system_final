package main.java.client;

import main.java.client.handlers.*;
import main.java.network.*;
import org.apache.commons.cli.*;
import main.java.utils.Constants;
import main.java.utils.ProgramArgumentsHelper;

import java.net.InetSocketAddress;
import java.util.Scanner;


public class Client {
    private final Network network;

    public Client(Network network) {
        this.network = network;
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
                case Constants.VIEW_ALL_FACILITIES:
                    ViewAllFacilities.createAndSendMessage(network);
                    break;
                case Constants.FACILITY_AVAILABILITY:
                    FacilitiesAvailability.createAndSendMessage(network, scanner);
                    break;
                case Constants.FACILITY_BOOKING:
                    FacilityBooking.createAndSendMessage(network, scanner);
                    break;
                case Constants.OFFSET_BOOKING:
                    OffsetBooking.createAndSendMessage(network, scanner);
                    break;
                case Constants.FACILITY_MONITORING:
                    FacilityMonitoring.createAndSendMessage(network, scanner);
                    break;
                case Constants.SHORTEN_BOOKING:
                    ShortenBooking.createAndSendMessage(network, scanner);
                    break;
                case Constants.MONITOR_AND_BOOK_ON_AVAILABLE:
                    MonitorAndBookOnVacancy.createAndSendMessage(network, scanner);
                    break;
                case Constants.SERVICE_EXIT:
                    System.out.println(ClientUI.EXIT_SYSTEM_MESSAGE);
                    break;
                default:
                    System.out.println(ClientUI.UNKNOWN_INPUT_MESSAGE);
                    System.out.println();
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

        boolean atLeastOnce;
        boolean atMostOnce;
        double failureRate = Constants.DEFAULT_FAILURE_RATE;

        // parse program arguments
        try {
            cmd = parser.parse(options, args);
            host = cmd.getOptionValue("host");
            port = Integer.parseInt(cmd.getOptionValue("port"));

            atLeastOnce = cmd.hasOption("atleast");
            atMostOnce = cmd.hasOption("atmost");

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

            client = new Client(new Network(communicator));

            while (true) {
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
