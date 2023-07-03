package com.campusdual.jardhotelsontimize.model.core.service;

import com.campusdual.jardhotelsontimize.api.core.service.IBookingService;
import com.campusdual.jardhotelsontimize.model.core.dao.BookingDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.common.services.user.UserInformation;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Lazy
@Service("BookingService")
public class BookingService implements IBookingService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Autowired
    private BookingDao bookingDao;

    @Autowired
    private RoomService roomService;

    @Autowired
    private UserService userService;

    @Autowired
    private StaffService staffService;


    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult bookingQuery(Map<String, Object> keyMap, List<String> attrList) {
        boolean deleteId = false;
        if (!attrList.contains("id")) {
            attrList.add("id");
            deleteId = true;
        }
        EntityResult result = this.daoHelper.query(this.bookingDao, keyMap, attrList);
        if (result.toString().contains("id")) {
            result.setMessage("");
            if (deleteId) result.remove("id");
        } else {
            result.setMessage("The booking doesn't exist");
            result.setCode(EntityResult.OPERATION_WRONG);
            result.setColumnSQLTypes(new HashMap<>());
        }
        return result;
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult bookingInsert(Map<String, Object> attrMap) {

        EntityResult result = new EntityResultMapImpl();

        EntityResult checkCheckInCheckOut = checkCheckInCheckOut(attrMap);

        if (checkCheckInCheckOut.getCode() == EntityResult.OPERATION_WRONG) {
            return checkCheckInCheckOut;
        }

        if (attrMap.containsKey("room")) {
            Map<String, Object> keyMapRoom = new HashMap<>();
            keyMapRoom.put("id", Integer.parseInt(attrMap.get("room").toString()));

            List<String> attrListRoom = new ArrayList<>();
            attrListRoom.add("price");
            attrListRoom.add("hotel");

            EntityResult roomQuery = roomService.roomQuery(keyMapRoom, attrListRoom);

            if (roomQuery.toString().contains("price")) {
                if (!attrMap.containsKey("totalprice") && attrMap.containsKey("arrivaldate") && attrMap.containsKey("departuredate")) {
                    double price = Double.parseDouble(((List<BigDecimal>) roomQuery.get("price")).get(0).toString());
                    attrMap.put("totalprice", calculateTotalPrice(attrMap.get("arrivaldate").toString(), attrMap.get("departuredate").toString(), price));
                }
                List<Integer> hotels = (List<Integer>) roomQuery.get("hotel");
                EntityResult checkPermisions = checkPermission(hotels.get(0), "insert");
                if (checkPermisions.getCode() == EntityResult.OPERATION_WRONG) {
                    return checkPermisions;
                }
            }
        } else {
            EntityResult error = new EntityResultMapImpl();
            error.setMessage("Missing room attribute");
            error.setCode(EntityResult.OPERATION_WRONG);
            return error;
        }

        try {
            result = this.daoHelper.insert(this.bookingDao, attrMap);
            result.setMessage("Successful booking insertion");


        } catch (Exception e) {

            result.setCode(EntityResult.OPERATION_WRONG);
            if (e.getMessage().contains("null value")) {
                result.setMessage("All attributes must be filled");
            } else if (e.getMessage().contains("Arrival date must be greater than or equal to current date")) {
                result.setMessage("Arrival date must be greater than or equal to current date");
            } else if (e.getMessage().contains("Departure date must be greater than arrival date")) {
                result.setMessage("Departure date must be greater than arrival date");
            } else if (e.getMessage().contains("The date range overlaps with the dates of an existing booking")) {
                result.setMessage("The date range overlaps with the dates of an existing booking");
            } else if (e.getMessage().contains("The total price can't be lower than 0")) {
                result.setMessage("The total price can't be lower than 0");
            } else if (e.getMessage().contains("booking_room_fkey")) {
                result.setMessage("Room not found");
            } else if (e.getMessage().contains("booking_guest_fkey")) {
                result.setMessage("Guest not found");
            } else {
                result.setMessage(e.getMessage());
            }

        }
        return result;
    }

    private EntityResult checkCheckInCheckOut(Map<String, Object> attrMap) {
        try {
            if (attrMap.containsKey("arrivaldate") && attrMap.containsKey("departuredate")) {
                if (attrMap.containsKey("checkindate")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date arrivalDate = dateFormat.parse(attrMap.get("arrivaldate").toString());
                    Date checkInDate = dateFormat.parse(attrMap.get("checkindate").toString());
                    if (checkInDate.before(arrivalDate)) {
                        EntityResult error = new EntityResultMapImpl();
                        error.setMessage("Check in date must be greater than or equal to arrival date");
                        error.setCode(EntityResult.OPERATION_WRONG);
                        return error;
                    }
                }
                if (attrMap.containsKey("checkoutdate")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date departureDate = dateFormat.parse(attrMap.get("departuredate").toString());
                    Date checkOutDate = dateFormat.parse(attrMap.get("checkoutdate").toString());
                    if (checkOutDate.after(departureDate)) {
                        EntityResult error = new EntityResultMapImpl();
                        error.setMessage("Check out date must be less than or equal to departure date");
                        error.setCode(EntityResult.OPERATION_WRONG);
                        return error;
                    }
                    if (attrMap.containsKey("checkindate")) {
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date checkInDate = dateFormat.parse(attrMap.get("checkindate").toString());
                        if (checkOutDate.before(checkInDate)) {
                            EntityResult error = new EntityResultMapImpl();
                            error.setMessage("Check out date must be greater than or equal to check in date");
                            error.setCode(EntityResult.OPERATION_WRONG);
                            return error;
                        }
                    } else {
                        EntityResult error = new EntityResultMapImpl();
                        error.setMessage("You can't check out without check in");
                        error.setCode(EntityResult.OPERATION_WRONG);
                        return error;
                    }
                }
            }
            return new EntityResultMapImpl();
        } catch (Exception e) {
            e.printStackTrace();
            return new EntityResultMapImpl();
        }
    }

    private EntityResult checkCheckInCheckOut(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        try {
            List<String> attrList = new ArrayList<>();
            attrList.add("id");
            attrList.add("arrivaldate");
            attrList.add("departuredate");
            attrList.add("checkindate");
            attrList.add("checkoutdate");
            EntityResult bookingQuery = bookingQuery(keyMap, attrList);
            if (!attrMap.containsKey("arrivaldate")) {
                List<Object> objects = (List<Object>) bookingQuery.get("arrivaldate");
                attrMap.put("arrivaldate", objects.get(0));
            }
            if (!attrMap.containsKey("departuredate")) {
                List<Object> objects = (List<Object>) bookingQuery.get("departuredate");
                attrMap.put("departuredate", objects.get(0));
            }
            if (!attrMap.containsKey("checkindate")) {
                List<Object> objects = (List<Object>) bookingQuery.get("checkindate");
                attrMap.put("checkindate", objects.get(0));
            }
            if (!attrMap.containsKey("checkoutdate")) {
                List<Object> objects = (List<Object>) bookingQuery.get("checkoutdate");
                attrMap.put("checkoutdate", objects.get(0));
            }
            return checkCheckInCheckOut(attrMap);
        } catch (Exception e) {
            e.printStackTrace();
            return new EntityResultMapImpl();
        }
    }

    private EntityResult checkPermission(int idHotel, String operation) {
        try {

            UserInformation userInformation = (UserInformation) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Map<String, Object> key = new HashMap<>();
            key.put("username", userInformation.getUsername());
            List<String> attrList = new ArrayList<>();
            attrList.add("idperson");
            attrList.add("username");
            EntityResult userQuery = userService.userQuery(key, attrList);
            key = new HashMap<>();
            List<Object> ids = (List<Object>) userQuery.get("idperson");
            key.put("id", ids.get(0));
            attrList = new ArrayList<>();
            attrList.add("idhotel");
            attrList.add("job");
            attrList.add("id");
            EntityResult staffQuery = staffService.staffQuery(key, attrList);
            if (staffQuery.getCode() == 0) {
                List<Integer> idsHotel = (List<Integer>) staffQuery.get("idhotel");
                List<Integer> jobs = (List<Integer>) staffQuery.get("job");
                if (jobs.get(0) == 3 && (idHotel != (int) idsHotel.get(0))) {
                    EntityResult error = new EntityResultMapImpl();
                    error.setCode(EntityResult.OPERATION_WRONG);
                    error.setMessage("This receptionist can only " + operation + " bookings in rooms from the hotel " + idsHotel.get(0));
                    return error;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new EntityResultMapImpl();

        }
        return new EntityResultMapImpl();

    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult bookingUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {

        EntityResult result = new EntityResultMapImpl();

        EntityResult check = checkCheckInCheckOut(attrMap, keyMap);
        if (check.getCode() == EntityResult.OPERATION_WRONG) {
            return check;
        }

        List<String> attrList = new ArrayList<>();
        attrList.add("room");
        EntityResult bookingQuery = bookingQuery(keyMap, attrList);
        if (bookingQuery.getCode() == 0) {
            List<Integer> ids = (List<Integer>) bookingQuery.get("room");
            EntityResult checkPermissions = checkPermission(ids.get(0), "update");
            if (checkPermissions.getCode() == EntityResult.OPERATION_WRONG) {
                return checkPermissions;
            }
        }

        //sacar el precio de la habitación
        if (attrMap.containsKey("room")) {
            Map<String, Object> keyMapRoom = new HashMap<>();
            keyMapRoom.put("id", Integer.parseInt(attrMap.get("room").toString()));

            List<String> attrListRoom = new ArrayList<>();
            attrListRoom.add("price");

            EntityResult roomQuery = roomService.roomQuery(keyMapRoom, attrListRoom);

            if (roomQuery.toString().contains("price")) {
                if (!attrMap.containsKey("totalprice")) {
                    double price = Double.parseDouble(((List<BigDecimal>) roomQuery.get("price")).get(0).toString());
                    attrMap.put("totalprice", calculateTotalPrice(attrMap.get("arrivaldate").toString(), attrMap.get("departuredate").toString(), price));
                }
            }


        }

        try {
            result = this.daoHelper.update(this.bookingDao, attrMap, keyMap);

            if (result.getCode() == 2) {
                result.setMessage("Booking not found");
                result.setCode(EntityResult.OPERATION_WRONG);
            } else {
                result.setMessage("Successful booking update");
            }
        } catch (Exception e) {
            result.setCode(EntityResult.OPERATION_WRONG);
            if (e.getMessage().contains("Arrival date must be greater than or equal to current date")) {
                result.setMessage("Arrival date must be greater than or equal to current date");
            } else if (e.getMessage().contains("Departure date must be greater than arrival date")) {
                result.setMessage("Departure date must be greater than arrival date");
            } else if (e.getMessage().contains("The date range overlaps with the dates of an existing booking")) {
                result.setMessage("The date range overlaps with the dates of an existing booking");
            } else if (e.getMessage().contains("The total price can't be lower than 0")) {
                result.setMessage("The total price can't be lower than 0");
            } else if (e.getMessage().contains("Changing the guest is not allowed")) {
                result.setMessage("Changing the guest is not allowed");
            } else if (e.getMessage().contains("Changing the room to a different hotel is not allowed")) {
                result.setMessage("Changing the room to a different hotel is not allowed");
            } else if (e.getMessage().contains("booking_room_fkey")) {
                result.setMessage("Room not found");
            } else {
                result.setMessage(e.getMessage());
            }
        }

        return result;
    }

    private static double calculateTotalPrice(String arrivalDate, String departureDate, double price) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        double totalPrice = 0;

        try {
            Date arrival = format.parse(arrivalDate.substring(0, 10));
            Date departure = format.parse(departureDate.substring(0, 10));

            long diffInMillies = Math.abs(departure.getTime() - arrival.getTime());
            long diffInDays = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);

            totalPrice = diffInDays * price;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return totalPrice;
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult bookingDelete(Map<String, Object> keyMap) {
        List<String> attrList = new ArrayList<>();
        attrList.add("room");
        attrList.add("id");
        attrList.add("totalprice");
        attrList.add("arrivaldate");
        EntityResult bookingQuery = bookingQuery(keyMap, attrList);
        if (bookingQuery.getCode() == 0) {
            List<Integer> ids = (List<Integer>) bookingQuery.get("room");
            EntityResult checkPermissions = checkPermission(ids.get(0), "delete");
            if (checkPermissions.getCode() == EntityResult.OPERATION_WRONG) {
                return checkPermissions;
            }
        }


        EntityResult result = this.daoHelper.delete(this.bookingDao, keyMap);
        if (bookingQuery.toString().contains("id")) {
            result.setMessage("Successful booking delete");
            double price = Double.parseDouble(((List<BigDecimal>) bookingQuery.get("totalprice")).get(0).toString());
            result.put("refund", calculateRefund(price, bookingQuery.get("arrivaldate").toString()));
        } else {
            result.setMessage("Booking not found");
            result.setCode(EntityResult.OPERATION_WRONG);
        }

        return result;
    }

    private double calculateRefund(double totalprice, String arrivalDate) {

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("'['yyyy-MM-dd']'");
        LocalDate parsedArrivalDate = LocalDate.parse(arrivalDate, formatter);

        long daysUntilArrival = ChronoUnit.DAYS.between(currentDate, parsedArrivalDate);

        if (daysUntilArrival > 7) {
            return totalprice;
        } else if (daysUntilArrival > 1) {
            return totalprice / 2;
        } else {
            return 0;
        }
    }
}
