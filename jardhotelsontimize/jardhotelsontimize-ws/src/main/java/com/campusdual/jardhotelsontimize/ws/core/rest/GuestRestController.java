package com.campusdual.jardhotelsontimize.ws.core.rest;

import com.campusdual.jardhotelsontimize.api.core.service.IGuestService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/guests")
public class GuestRestController extends ORestController<IGuestService> {

    @Autowired
    private IGuestService iGuestService;

    @Override
    public IGuestService getService() {
        return this.iGuestService;
    }
}
