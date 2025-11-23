import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class VehicleRentalTest {

    // 1) isValidPlate: valid cases
    @Test
    void testIsValidPlateValid() {
        assertTrue(Vehicle.isValidPlate("AB1234"));
        assertTrue(Vehicle.isValidPlate("xyz9"));
        assertTrue(Vehicle.isValidPlate("  aa11  "));
    }

    // 2) isValidPlate: invalid cases
    @Test
    void testIsValidPlateInvalid() {
        assertFalse(Vehicle.isValidPlate(null));
        assertFalse(Vehicle.isValidPlate(""));
        assertFalse(Vehicle.isValidPlate("A"));          // too short
        assertFalse(Vehicle.isValidPlate("TOO_LONG123"));// bad chars/length
        assertFalse(Vehicle.isValidPlate("AB 12"));      // space not allowed
    }


    @Test
    void testSetLicensePlateUppercases() {
        Car car = new Car("toyota", "corolla", 2020, 5);
        car.setLicensePlate("ab1234");
        assertEquals("AB1234", car.getLicensePlate());
    }

    // 4) setLicensePlate: rejects invalid plate
    @Test
    void testSetLicensePlateRejectsInvalid() {
        Car car = new Car("toyota", "corolla", 2020, 5);
        assertThrows(IllegalArgumentException.class,
                () -> car.setLicensePlate("A"));
    }
    
    // 5) rent: status changes and boolean true
    @Test
    void testRentVehicleChangesStatus() {
        RentalSystem rs = RentalSystem.getInstance();

        Car car = new Car("toyota", "corolla", 2020, 5);
        car.setLicensePlate("AB1234");
        Customer cust = new Customer(1, "Alice");

        assertTrue(rs.addVehicle(car));
        assertTrue(rs.addCustomer(cust));
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());

        boolean rented = rs.rentVehicle(car, cust, java.time.LocalDate.now(), 100.0);
        assertTrue(rented);
        assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus());
    }

    // 6) return: status changes back and boolean true
    @Test
    void testReturnVehicleChangesStatusBack() {
        RentalSystem rs = RentalSystem.getInstance();

        Car car = new Car("honda", "civic", 2021, 4);
        car.setLicensePlate("CD5678");
        Customer cust = new Customer(2, "Bob");

        assertTrue(rs.addVehicle(car));
        assertTrue(rs.addCustomer(cust));

        assertTrue(rs.rentVehicle(car, cust, java.time.LocalDate.now(), 80.0));
        assertEquals(Vehicle.VehicleStatus.Rented, car.getStatus());

        boolean returned = rs.returnVehicle(car, cust, java.time.LocalDate.now(), 0.0);
        assertTrue(returned);
        assertEquals(Vehicle.VehicleStatus.Available, car.getStatus());
    }
    // 7) getInstance always returns same object
    @Test
    void testSingletonSameInstance() {
        RentalSystem r1 = RentalSystem.getInstance();
        RentalSystem r2 = RentalSystem.getInstance();
        assertSame(r1, r2);
    }
}

