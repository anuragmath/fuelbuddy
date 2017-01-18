package com.fuel.buddy.repository;

import com.fuel.buddy.domain.Assets;

import org.springframework.data.jpa.repository.*;
import org.springframework.security.access.prepost.PostFilter;

import java.util.List;

/**
 * Spring Data JPA repository for the Assets entity.
 */
@SuppressWarnings("unused")
public interface AssetsRepository extends JpaRepository<Assets,Long> {

    @PostFilter("filterObject.user.getId() == principal.id")
    @Query("select assets from Assets assets where assets.user.login = ?#{principal.username}")
    List<Assets> findByUserIsCurrentUser();

    @PostFilter("filterObject.user.login == principal.username")
    List<Assets> findAll();

}
