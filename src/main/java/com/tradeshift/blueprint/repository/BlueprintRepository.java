package com.tradeshift.blueprint.repository;

import com.tradeshift.blueprint.model.BlueprintModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Sample Repository for a Blueprint JPA Repository
 */
@Repository
public interface BlueprintRepository extends JpaRepository<BlueprintModel, Long> {

}
