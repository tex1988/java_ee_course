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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;
import java.util.Objects;

@Entity(name = "country_entity")
@Table(name = "country")
@NoArgsConstructor
public class Country implements BaseEntity<Integer> {
    @Id
    @Column(name = "country_id")
    @Setter
    private int countryId;

    @Column(name="name")
    @Getter
    @Setter
    private String name;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "country", cascade = CascadeType.ALL)
    @Getter
    @Setter
    private List<Region> regions;

    public Country(int countryId, String name) {
        this.countryId = countryId;
        this.name = name;
    }

    @Override
    public Integer getId() {
        return countryId;
    }

    @Override
    public String toString() {
        return "Country{" +
                "countryId=" + countryId +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Country country = (Country) o;
        return countryId == country.countryId && Objects.equals(name, country.name);
    }

    @Override
    public int hashCode() {
        int result = 0;
        for (Region region : regions) {
            result += region.hashCode();
        }
        result += Objects.hash(countryId, name);
        return result;
    }
}
