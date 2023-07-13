package com.campusdual.jardhotelsontimize.ws.core.rest;

import com.campusdual.jardhotelsontimize.api.core.service.IMenuService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/menus")
public class MenuRestController extends ORestController<IMenuService> {

    @Autowired
    private IMenuService iMenuService;

    @Override
    public IMenuService getService() {
        return this.iMenuService;
    }
}
