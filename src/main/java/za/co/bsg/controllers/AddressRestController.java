package za.co.bsg.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import za.co.bsg.dataAccess.AddressRepository;
import za.co.bsg.models.Address;

import java.util.List;

/**
 * @author Sarath Muraleedharan
 */
@RestController
@RequestMapping(value = "/api")
public class AddressRestController {
    @Autowired
    private AddressRepository addressRepository;

    @RequestMapping(value = "/address", method = RequestMethod.GET)
    public List<Address> address() {
        return addressRepository.findAll();
    }

    @RequestMapping(value = "/address/{id}", method = RequestMethod.GET)
    public ResponseEntity<Address> addressById(@PathVariable Long id) {
        Address address = addressRepository.findOne(id);
        if (address == null) {
            return new ResponseEntity<Address>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<Address>(address, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/address/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Address> deleteAddress(@PathVariable Long id) {
        Address address = addressRepository.findOne(id);
        if (address == null) {
            return new ResponseEntity<Address>(HttpStatus.NO_CONTENT);
        } else {
            addressRepository.delete(address);
            return new ResponseEntity<Address>(address, HttpStatus.OK);
        }

    }

    @RequestMapping(value = "/address", method = RequestMethod.POST)
    public ResponseEntity<Address> createAddress(@RequestBody Address address) {
        return new ResponseEntity<Address>(addressRepository.save(address), HttpStatus.CREATED);
    }

    @RequestMapping(value = "/address", method = RequestMethod.PUT)
    public Address updateAddress(@RequestBody Address address) {
        return addressRepository.save(address);
    }

}
