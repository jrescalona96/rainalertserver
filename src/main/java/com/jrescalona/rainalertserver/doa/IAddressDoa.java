package com.jrescalona.rainalertserver.doa;

import com.jrescalona.rainalertserver.model.Address;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IAddressDoa {
    /**
     * Creates a random UUID
     * then inserts new address using UUID
     * Invokes overloaded method
     * @param address Address
     * @return 0 if successful, 1 otherwise
     */
    default int insertAddress(Address address) {
        UUID addressId = UUID.randomUUID();
        return insertAddress(addressId, address);
    };

    int insertAddress(UUID id, Address address);
    Optional<Address> selectAddressById(UUID id);
    List<Address> selectAllAddressesByUserId(UUID id);
    int updateAddressById(UUID id, Address address);
    int deleteAddressById(UUID id);
}
