package com.campusdual.jardhotelsontimize.ws.core.rest;

import com.campusdual.jardhotelsontimize.api.core.service.IRoomService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rooms")
public class RoomRestController extends ORestController<IRoomService> {

    @Autowired
    private IRoomService iRoomService;

    @Override
    public IRoomService getService() {
        return this.iRoomService;
    }
}
