package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.jpa_criteria_repository.entities;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "city")
@NoArgsConstructor
public class City implements BaseEntity<Integer> {
    @Id
    @Column(name="city_id")
    @Setter
    private int cityId;

    @Column(name="name")
    @Getter
    @Setter
    private String name;

    @ManyToOne()
    @JoinColumn(name = "region_id")
    @Getter
    @Setter
    private Region region;

    @ManyToOne()
    @JoinColumn(name = "country_id")
    @Getter
    @Setter
    private Country country;

    public City(int cityId, String name) {
        this.cityId=cityId;
        this.name=name;
    }

    @Override
    public Integer getId() {
        return cityId;
    }

    @Override
    public String toString() {
        return "City{" +
                "cityId=" + cityId +
                ", name='" + name +
                "', region='" + region.getName() +
                "', country='" + country.getName() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return cityId == city.cityId && Objects.equals(name, city.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cityId, name);
    }
}
