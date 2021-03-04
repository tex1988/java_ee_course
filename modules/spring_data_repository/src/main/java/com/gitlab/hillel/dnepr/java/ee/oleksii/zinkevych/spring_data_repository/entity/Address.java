package com.gitlab.hillel.dnepr.java.ee.oleksii.zinkevych.spring_data_repository.entity;

import com.gitlab.hillel.dnepr.java.ee.common.repository.entity.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "address")
@Getter
@Setter
@NoArgsConstructor
public class Address implements BaseEntity<Integer> {
    @Id
    @Column(name="address_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    @ManyToOne
    @JoinColumn(name = "city_id")
    City city;

    @Column(name = "street")
    String street;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return id.equals(address.id) && Objects.equals(city, address.city) && Objects.equals(street, address.street);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, city, street);
    }

    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", address=" + city.getId() +
                ", " + city.getCountry().getName() +
                ", " + city.getRegion().getName() +
                ", " + city.getName() +
                ", " + street +
                '}';
    }
}