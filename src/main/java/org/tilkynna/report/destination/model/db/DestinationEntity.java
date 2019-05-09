/**
 * *************************************************
 * Copyright (c) 2019, Grindrod Bank Limited
 * License MIT: https://opensource.org/licenses/MIT
 * **************************************************
 */
package org.tilkynna.report.destination.model.db;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;

import lombok.Getter;
import lombok.Setter;

//this should be an abstract class so that setDefaultDestinationParameters() can be abstract, 
//however DestinationEntity is used for listing all destinations, and Hibernate/JPA etc. needs a concrete class to be able to do that 
@Getter
@Setter
@Entity
@Table(name = "destination")
@Inheritance(strategy = InheritanceType.JOINED)
public class DestinationEntity {

    public DestinationEntity() {
        this.setDefaultDestinationParameters();
    }

    @Id
    @Column(name = "destination_id", updatable = false, unique = true, nullable = false, columnDefinition = "uuid default gen_random_uuid()")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID destinationId;

    @Column(name = "type", nullable = false)
    @Type(type = "org.hibernate.type.TextType")
    private String type;

    @Column(name = "name", unique = true, nullable = false, length = 255)
    private String name;

    @Column(name = "description")
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = "timeout")
    private Long timeout;

    @Column(name = "security_protocol")
    @Type(type = "org.hibernate.type.TextType")
    private String securityProtocol;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "downloadable")
    private boolean downloadable;

    @OneToMany(mappedBy = "destination", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<DestinationParameterEntity> destinationParameters = new HashSet<>();

    public void addDestinationParameter(DestinationParameterEntity destinationParameter) {
        if (!getDestinationParameters().contains(destinationParameter)) {
            getDestinationParameters().add(destinationParameter);
        }
    }

    public void addDestinationParameters(Set<DestinationParameterEntity> destinationParameters) {
        for (Iterator<DestinationParameterEntity> iterator = destinationParameters.iterator(); iterator.hasNext();) {
            DestinationParameterEntity destinationParameter = iterator.next();
            addDestinationParameter(destinationParameter);
        }
    }

    public DestinationParameterEntity getDestinationParameterEntity(String name) {
        for (Iterator<DestinationParameterEntity> iterator = destinationParameters.iterator(); iterator.hasNext();) {
            DestinationParameterEntity destParam = iterator.next();

            if (destParam.getName().equals(name)) {
                return destParam;
            }
        }

        return null;
    }

    public void setDefaultDestinationParameters() {
        // this should be an abstract class so that setDefaultDestinationParameters() can be abstract,
        // however DestinationEntity is used for listing all destinations, and Hibernate/JPA etc. needs a concrete class to be able to do that
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, destinationParameters, downloadable, isActive, name, securityProtocol, timeout, type);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) { return true; }
        if (obj == null) { return false; }
        if (!(obj instanceof DestinationEntity)) { return false; }
        DestinationEntity other = (DestinationEntity) obj;
        return Objects.equals(description, other.description) && Objects.equals(destinationParameters, other.destinationParameters) && downloadable == other.downloadable && isActive == other.isActive && Objects.equals(name, other.name) && Objects.equals(securityProtocol, other.securityProtocol) && Objects.equals(timeout, other.timeout) && Objects.equals(type, other.type);
    }

}
