package com.campusdual.jardhotelsontimize.ws.core.rest;

import com.campusdual.jardhotelsontimize.api.core.service.ICountryService;
import com.campusdual.jardhotelsontimize.api.core.service.IHotelService;
import com.campusdual.jardhotelsontimize.model.core.dao.HotelDao;
import com.ontimize.jee.common.db.SQLStatementBuilder.*;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hotels")
public class HotelRestController extends ORestController<IHotelService> {

    @Autowired
    private IHotelService iHotelService;

    @Autowired
    private ICountryService iCountryService;

    @Override
    public IHotelService getService() {
        return this.iHotelService;
    }


    @RequestMapping(value = "/filter", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityResult filter(@RequestBody Map<String, Object> req) {
        try {
            List<String> columns = (List<String>) req.get("columns");
            Map<String, Object> filter = (Map<String, Object>) req.get("filter");
            Map<String, Object> key = new HashMap<String, Object>();

            key.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,
                    concatenateExpressions(filter));

            return iHotelService.hotelQuery(key, columns);

        } catch (Exception e) {
            EntityResult res = new EntityResultMapImpl();
            res.setCode(EntityResult.OPERATION_WRONG);

            // excepciones estrellas
            if (e.getMessage().contains("Stars must be between 1 and 5")) {
                res.setMessage("Stars must be between 1 and 5");
            } else if (e.getMessage().contains("Min stars must be lower or equal than max")) {
                res.setMessage("Min stars must be lower or equal than max");
            } else if (e.getMessage().contains("Stars must be a whole number")) {
                res.setMessage("Stars must be a whole number");
            }

            // excepciones pais
            if (e.getMessage().contains("Country must be a whole number")) {
                res.setMessage("Country must be a whole number");
            } else if (e.getMessage().contains("Country must exist")) {
                res.setMessage("Country must exist");
            }

            return res;
        }
    }

    private BasicExpression concatenateExpressions(Map<String, Object> filter) {

        // filtro estrellas
        int stars_min = 1, stars_max = 5;

        try {
            if (filter.get("stars_min") != null) {
                stars_min = (int) filter.get("stars_min");
            }
            if (filter.get("stars_max") != null) {
                stars_max = (int) filter.get("stars_max");
            }
        } catch (ClassCastException e) {
            throw new RuntimeException("Stars must be a whole number");
        }

        if (stars_min < 1 || stars_min > 5 || stars_max < 1 || stars_max > 5) {
            throw new RuntimeException("Stars must be between 1 and 5");
        } else if (stars_min > stars_max) {
            throw new RuntimeException("Min stars must be lower or equal than max");
        }

        BasicExpression bexp = searchBetweenStars(HotelDao.ATTR_STARS, stars_min, stars_max);

        // filtro nombre
        if (filter.get("name") != null) {
            bexp = new BasicExpression(bexp, BasicOperator.AND_OP,
                    searchLikeName(HotelDao.ATTR_NAME, (String) filter.get("name")));
        }

        // filtro direccion
        if (filter.get("address") != null) {
            bexp = new BasicExpression(bexp, BasicOperator.AND_OP,
                    searchLikeAddress(HotelDao.ATTR_ADDRESS, (String) filter.get("address")));
        }

        // filtro pais
        if (filter.get("country") != null) {

            try {
                int country = (int) filter.get("country");
            } catch (Exception e) {
                throw new RuntimeException("Country must be a whole number");
            }

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("id", filter.get("country"));
            List<String> attrList = new ArrayList<>();
            attrList.add("id");

            EntityResult entityResult = iCountryService.countryQuery(keyMap, attrList);

            if (entityResult.getMessage().contains("The country doesn't exist")) {
                throw new RuntimeException("Country must exist");
            }

            bexp = new BasicExpression(bexp, BasicOperator.AND_OP,
                    searchByCountry(HotelDao.ATTR_COUNTRY, (int) filter.get("country")));
        }

        return bexp;
    }

    private BasicExpression searchBetweenStars(String param, int stars_min, int stars_max) {

        BasicField field = new BasicField(param);
        BasicExpression bexp1 = new BasicExpression(field, BasicOperator.MORE_EQUAL_OP, stars_min);
        BasicExpression bexp2 = new BasicExpression(field, BasicOperator.LESS_EQUAL_OP, stars_max);
        return new BasicExpression(bexp1, BasicOperator.AND_OP, bexp2);
    }

    private BasicExpression searchLikeName(String param, String name) {

        BasicField field = new BasicField(param);
        return new BasicExpression(field, BasicOperator.LIKE_OP, "%" + name + "%");
    }

    private BasicExpression searchLikeAddress(String param, String address) {

        BasicField field = new BasicField(param);
        return new BasicExpression(field, BasicOperator.LIKE_OP, "%" + address + "%");
    }

    private BasicExpression searchByCountry(String param, int country) {

        BasicField field = new BasicField(param);
        return new BasicExpression(field, BasicOperator.EQUAL_OP, country);
    }
}
