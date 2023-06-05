package com.campusdual.jardhotelsontimize.ws.core.rest;

import com.campusdual.jardhotelsontimize.api.core.service.ICountryService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/countries")
public class CountryRestController extends ORestController<ICountryService> {

    @Autowired
    private ICountryService iCountryService;

    @Override
    public ICountryService getService() {
        return this.iCountryService;
    }
}
