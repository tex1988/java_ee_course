package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

@Entity(name = "region_entity")
@Table(name = "region")
@NoArgsConstructor
public class Region implements BaseEntity<Integer> {
    @Id
    @Column(name = "region_id")
    @Setter
    private int regionId;

    @Column(name = "name")
    @Getter
    @Setter
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "region", cascade = CascadeType.ALL)
    @Getter
    @Setter
    private List<City> cities;

    @ManyToOne()
    @JoinColumn(name = "country_id")
    @Getter
    @Setter
    private Country country;

    public Region(int regionId, String name) {
        this.regionId = regionId;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return regionId;
    }

    @Override
    public String toString() {
        return "Region{" +
                "regionId=" + regionId +
                ", name='" + name +
                "', country='" + country.getName() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Region region = (Region) o;
        return regionId == region.regionId && Objects.equals(name, region.name) && Objects.equals(cities, region.cities);
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (City city : cities) {
            result += city.hashCode();
        }
        result += Objects.hash(regionId, name);
        return result;
    }
}
