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
}
