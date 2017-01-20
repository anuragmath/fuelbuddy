package com.fuel.buddy.repository;

import com.fuel.buddy.domain.Fuel;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Fuel entity.
 */
@SuppressWarnings("unused")
public interface FuelRepository extends JpaRepository<Fuel,Long> {

}
