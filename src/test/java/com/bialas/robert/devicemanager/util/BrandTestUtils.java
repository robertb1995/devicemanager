package com.bialas.robert.devicemanager.util;

import com.bialas.robert.devicemanager.domain.entity.Brand;

import java.util.Collections;

public class BrandTestUtils {

    public static Brand getExpectedBrand() {
        return Brand.builder()
                .name("Mayo")
                .devices(Collections.emptyList())
                .build();
    }

}
