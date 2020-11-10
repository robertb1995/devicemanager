package com.bialas.robert.devicemanager.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.time.ZonedDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table
@Builder
@EqualsAndHashCode
public class Device {

    @Id
    @GeneratedValue
    @EqualsAndHashCode.Exclude
    private Long id;

    private String deviceName;

    @ManyToOne(fetch = FetchType.EAGER,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "brand_name",
            nullable = false
    )
    private Brand brand;

    @EqualsAndHashCode.Exclude
    private ZonedDateTime creationTime;

}
