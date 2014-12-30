package escape_room.escaperoom.services;

import escape_room.escaperoom.EscapeRoomActivity;
import escape_room.escaperoom.services.archivation_service.ArchivationService;
import escape_room.escaperoom.services.bluetooth_communication_service.CommunicationHandler;
import escape_room.escaperoom.services.data_service.DataService;

/**
 * Created by Dell on 29.12.2014.
 */
public class Services {

    private final DataService dataService;
    public DataService getDataService() {
        return dataService;
    }

    private final CommunicationHandler communicationHandler;
    public CommunicationHandler getCommunicationHandler() {
        return communicationHandler;
    }

    private final ArchivationService archivationService;
    public ArchivationService getArchivationService() {
        return archivationService;
    }

    public Services(EscapeRoomActivity escapeRoomActivity,String externalCacheDirePath ){
        dataService = new DataService(escapeRoomActivity);
        communicationHandler = new CommunicationHandler(dataService,escapeRoomActivity);
        archivationService = new ArchivationService(externalCacheDirePath,escapeRoomActivity.getApplicationContext());
    }
}
