package com.campusdual.jardhotels.model.dao;

import com.campusdual.jardhotels.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomDAO extends JpaRepository<Room, Integer> {
}
