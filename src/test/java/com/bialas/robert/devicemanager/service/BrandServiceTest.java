package com.bialas.robert.devicemanager.service;

import com.bialas.robert.devicemanager.domain.entity.Brand;
import com.bialas.robert.devicemanager.repository.BrandRepository;
import com.bialas.robert.devicemanager.util.BrandTestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandService brandService;

    @Test
    public void givenUserId_whenFindByIdCalled_returnOptionalOfBrand() {
        //given
        String brandName = "Mayo";
        Brand expectedBrand = BrandTestUtils.getExpectedBrand();
        when(brandRepository.findById(brandName)).thenReturn(Optional.of(expectedBrand));

        //when
        Optional<Brand> result = brandService.findById(brandName);

        //then
        assertEquals(Optional.of(expectedBrand), result);
    }
}
