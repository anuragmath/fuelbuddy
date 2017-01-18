package com.fuel.buddy.repository;

import com.fuel.buddy.domain.Assets;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Assets entity.
 */
@SuppressWarnings("unused")
public interface AssetsRepository extends JpaRepository<Assets,Long> {

    @Query("select assets from Assets assets where assets.user.login = ?#{principal.username}")
    List<Assets> findByUserIsCurrentUser();

}
