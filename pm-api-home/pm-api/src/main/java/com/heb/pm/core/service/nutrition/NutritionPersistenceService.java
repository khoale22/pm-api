/*
 *  NutritionPersistenceService.java
 *
 *  Copyright (c) 2019 H-E-B
 *  All rights reserved.
 *
 *  This software is the confidential and proprietary information of H-E-B.
 */
package com.heb.pm.core.service.nutrition;

import com.heb.pm.core.model.updates.nutrition.NutrientStatement;

/**
 * Interface for classes responsible for persisting nutriton update requests.
 *
 * @author vn70529
 * @since 1.9.0
 */
public interface NutritionPersistenceService {

    /**
     * Saves a NutrientStatement. It is the responsibility of classes that implement this interface to validate
     * the NutrientStatement before saving.
     *
     * @param upc The UPC the NutrientStatement.
     * @param nutrientStatement The NutrientStatement to save.
     * @return A response with the ID of the statement, and, possibly, the UPC it is meant for and if it is tied to the UPC.
     */
    NutritionUpdateResponse save(Long upc, NutrientStatement nutrientStatement);
}
