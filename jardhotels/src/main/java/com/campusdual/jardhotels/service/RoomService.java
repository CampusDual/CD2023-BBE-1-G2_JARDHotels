package com.campusdual.jardhotels.service;
import com.campusdual.jardhotels.api.IRoomService;
import com.campusdual.jardhotels.model.Room;
import com.campusdual.jardhotels.model.dao.RoomDAO;
import com.campusdual.jardhotels.model.dto.RoomDTO;
import com.campusdual.jardhotels.model.dto.dtomapper.RoomMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("RoomService")
@Lazy
public class RoomService implements IRoomService {

    @Autowired
    private RoomDAO roomDAO;

    @Override
    public int insertRoom(RoomDTO roomDTO) {
        Room room = RoomMapper.INSTANCE.toEntity(roomDTO);
        roomDAO.saveAndFlush(room);
        return room.getId();
    }

    @Override
    public int deleteRoom(RoomDTO roomDTO) {
        Room room = RoomMapper.INSTANCE.toEntity(roomDTO);
        roomDAO.delete(room);
        return roomDTO.getId();
    }

    @Override
    public List<RoomDTO> queryAll() {
        return RoomMapper.INSTANCE.toDtoList(roomDAO.findAll());
    }

    @Override
    public RoomDTO queryRoom(RoomDTO roomDTO) {
        Room room = RoomMapper.INSTANCE.toEntity(roomDTO);
        return RoomMapper.INSTANCE.toDto(roomDAO.getReferenceById(room.getId()));
    }
}
