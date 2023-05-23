package com.campusdual.jardhotels.model.dao;

import com.campusdual.jardhotels.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingDAO extends JpaRepository<Booking, Integer> {
}
