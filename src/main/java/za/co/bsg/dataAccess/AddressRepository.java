package za.co.bsg.dataAccess;

import org.springframework.data.jpa.repository.JpaRepository;
import za.co.bsg.models.Address;

/**
 * @author Sarath Muraleedharan
 */
public interface AddressRepository extends JpaRepository<Address, Long> {

}
