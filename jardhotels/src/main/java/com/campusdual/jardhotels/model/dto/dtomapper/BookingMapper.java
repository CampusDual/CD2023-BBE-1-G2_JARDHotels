package com.campusdual.jardhotels.model.dto.dtomapper;

import com.campusdual.jardhotels.model.Booking;
import com.campusdual.jardhotels.model.Guest;
import com.campusdual.jardhotels.model.Room;
import com.campusdual.jardhotels.model.dto.BookingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface BookingMapper {


    BookingMapper INSTANCE = Mappers.getMapper(BookingMapper.class);

    List<BookingDTO> toDtoList(List<Booking> bookings);

    List<Booking> toEntityList(List<BookingDTO> bookingsDTO);


    @Mapping(source = "guest", target = "guest", qualifiedByName = "guestToGuestDto")
    @Mapping(source = "room", target = "room", qualifiedByName = "roomToRoomDto")
    BookingDTO toDto(Booking booking);

    @Mapping(source = "guest", target = "guest", qualifiedByName = "guestDtoToGuest")
    @Mapping(source = "room", target = "room", qualifiedByName = "roomDtoToRoom")
    Booking toEntity(BookingDTO bookingDTO);


    @Named("roomDtoToRoom")
    default Room roomDtoToRoom(int room) {
        Room r = new Room();
        r.setId(room);
        return r;
    }

    @Named("roomToRoomDto")
    default int roomToRoomDto(Room room) {
        return room != null ? room.getId() : 0;
    }


    @Named("guestDtoToGuest")
    default Guest guestDtoToGuest(int guest) {
        Guest g = new Guest();
        g.setId(guest);
        return g;
    }

    @Named("guestToGuestDto")
    default int guestToGuestDto(Guest guest) {
        return guest != null ? guest.getId() : 0;
    }
}
