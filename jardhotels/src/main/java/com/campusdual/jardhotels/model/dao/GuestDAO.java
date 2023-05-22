package com.campusdual.jardhotels.model.dao;

import com.campusdual.jardhotels.model.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestDAO extends JpaRepository<Guest, Integer> {

}
