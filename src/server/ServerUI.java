package server;

import client.ClientUI;
import remote_objects.Client.ClientRequest;
import remote_objects.Server.ServerResponse;
import utils.Constants;

public class ServerUI {
    public static void printServerResponse(ClientRequest request, ServerResponse response) {
        switch (request.getRequestChoice()) {
            case Constants.VIEW_ALL_FACILITIES -> ClientUI.listFacilitiesResponse(response);
            case Constants.FACILITY_AVAILABILITY -> ClientUI.showFacilityAvailabilityResponse(response);
            case Constants.FACILITY_BOOKING -> ClientUI.bookingResponse(response);
            case Constants.OFFSET_BOOKING, Constants.SHORTEN_BOOKING -> ClientUI.changeBookingResponse(response);
            case Constants.FACILITY_MONITORING -> ClientUI.monitoringResponse(response);
            case Constants.MONITOR_AND_BOOK_ON_AVAILABLE -> ClientUI.bookingIfVacancyAppearsResponse(response);
            default -> {}
        }
    }
}
