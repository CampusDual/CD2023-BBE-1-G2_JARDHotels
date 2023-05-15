package com.campusdual.jardhotels.model.dao;

import com.campusdual.jardhotels.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HotelDAO extends JpaRepository<Hotel, Integer> {

}
