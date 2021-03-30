package client;

import client.handlers.*;
import constants.Constants;
import entity.ClientQuery;
import network.*;
import utils.ProgramArgumentsHelper;
import org.apache.commons.cli.*;

import java.net.InetSocketAddress;
import java.util.Scanner;


public class Client {
    private final Network network;

    public Client(Network network) {
        this.network = network;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);
        Constants.MainMenuSelectionMessage();

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
                case Constants.CHECK_FACILITIES_AVAILABILITY:
                    FacilitiesAvailability.createAndSendMessage(network, scanner);
                    break;
                case Constants.BOOK_FACILITY:
                    FacilityBooking.createAndSendMessage(network, scanner);
                    break;
                case Constants.CHANGE_BOOKING:
                    OffsetBooking.createAndSendMessage(network, scanner);
                    break;
                case Constants.MONITOR_BOOKING:
                    FacilityMonitoring.createAndSendMessage(network, scanner);
                    break;
                case Constants.SHORTEN_BOOKING:
                    ShortenBooking.createAndSendMessage(network, scanner);
                    break;
                case Constants.BOOK_ON_VACANCY:
                    MonitorAndBookOnVacancy.createAndSendMessage(network, scanner);
                    break;
                case Constants.SERVICE_EXIT:
                    System.out.println(Constants.EXIT_MSG);
                    break;
                case 11:
                    // TODO - delete close to completion
                    // sanity check - prints all the current bookings
                    ClientQuery query = new ClientQuery();
                    query.setType(11);

                    int id = network.send(query);
                    network.receive(id, (response) -> {
                        if (response.getStatus() != 200) {
                            Constants.PrintErrorMessage(response);
                        }
                    }, false, 5);

                default:
                    System.out.println(Constants.UNRECOGNIZE_SVC_MSG);
                    System.out.println();
            }

        } catch (Exception e) {
            System.out.println(Constants.SEPARATOR);
            System.out.printf(Constants.ERR_MSG, e.getMessage());
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
            UDPCommunicator communicator = new PoorUDPCommunicator(socketAddress, failureRate);

            System.out.print(Constants.SEPARATOR);
            System.out.println(Constants.WELCOME_MSG);
            System.out.println(Constants.SEPARATOR);

            if (atLeastOnce) {
                client = new Client(new AtLeastOnceNetwork(communicator));
                System.out.println("At least once network");
            } else if (atMostOnce) {
                client = new Client(new AtMostOnceNetwork(communicator));
                System.out.println("At most once network");
            } else {
                client = new Client(new AtLeastOnceNetwork(communicator));
                System.out.println("Defaults to at most once network");
            }

            while (true) {
                client.run();
            }

        } catch (Exception e) {
            System.out.print(Constants.SEPARATOR);
            System.out.print(Constants.ERR_MSG);
            e.printStackTrace();
            System.out.println(Constants.EXIT_MSG);
            System.out.println(Constants.SEPARATOR);
        }
    }
}
