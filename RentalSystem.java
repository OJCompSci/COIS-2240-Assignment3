import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

public class RentalSystem {
	
	private static RentalSystem instance;
	
    private List<Vehicle> vehicles;
    private List<Customer> customers;
    private RentalHistory rentalHistory;
    
    private static final String VEHICLE_FILE = "vehicles.txt";
    private static final String CUSTOMER_FILE = "customers.txt";
    private static final String RECORD_FILE  = "records.txt";
	
	private RentalSystem() 
	{
		vehicles = new ArrayList<>();
		customers = new ArrayList<>();
		rentalHistory = new RentalHistory();
	}

    public static RentalSystem getInstance()
    {
    	if(instance == null)
    	{
    		instance = new RentalSystem();
    	}
    	return instance;
    }
    
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null) {
            return false;
        }
        vehicles.add(vehicle);          // in‑memory
        saveVehicle(vehicle);           // on disk (private helper)
        return true;
    }

    public boolean addCustomer(Customer customer) {
        if (customer == null) {
            return false;
        }
        customers.add(customer);
        saveCustomer(customer);
        return true;
    }

    public boolean rentVehicle(Vehicle vehicle, Customer customer,
                               LocalDate date, double amount) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Available) {
            vehicle.setStatus(Vehicle.VehicleStatus.Rented);
            RentalRecord record =
                    new RentalRecord(vehicle, customer, date, amount, "RENT");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle rented to " + customer.getCustomerName());
            return true;
        }
        System.out.println("Vehicle is not available for renting.");
        return false;
    }

    public boolean returnVehicle(Vehicle vehicle, Customer customer,
                                 LocalDate date, double extraFees) {
        if (vehicle.getStatus() == Vehicle.VehicleStatus.Rented) {
            vehicle.setStatus(Vehicle.VehicleStatus.Available);
            RentalRecord record =
                    new RentalRecord(vehicle, customer, date, extraFees, "RETURN");
            rentalHistory.addRecord(record);
            saveRecord(record);
            System.out.println("Vehicle returned by " + customer.getCustomerName());
            return true;
        }
        System.out.println("Vehicle is not rented.");
        return false;
    }   

    public void displayVehicles(Vehicle.VehicleStatus status) {
        // Display appropriate title based on status
        if (status == null) {
            System.out.println("\n=== All Vehicles ===");
        } else {
            System.out.println("\n=== " + status + " Vehicles ===");
        }
        
        // Header with proper column widths
        System.out.printf("|%-16s | %-12s | %-12s | %-12s | %-6s | %-18s |%n", 
            " Type", "Plate", "Make", "Model", "Year", "Status");
        System.out.println("|--------------------------------------------------------------------------------------------|");
    	  
        boolean found = false;
        for (Vehicle vehicle : vehicles) {
            if (status == null || vehicle.getStatus() == status) {
                found = true;
                String vehicleType;
                if (vehicle instanceof Car) {
                    vehicleType = "Car";
                } else if (vehicle instanceof Minibus) {
                    vehicleType = "Minibus";
                } else if (vehicle instanceof PickupTruck) {
                    vehicleType = "Pickup Truck";
                } else {
                    vehicleType = "Unknown";
                }
                System.out.printf("| %-15s | %-12s | %-12s | %-12s | %-6d | %-18s |%n", 
                    vehicleType, vehicle.getLicensePlate(), vehicle.getMake(), vehicle.getModel(), vehicle.getYear(), vehicle.getStatus().toString());
            }
        }
        if (!found) {
            if (status == null) {
                System.out.println("  No Vehicles found.");
            } else {
                System.out.println("  No vehicles with Status: " + status);
            }
        }
        System.out.println();
    }

    public void displayAllCustomers() {
        for (Customer c : customers) {
            System.out.println("  " + c.toString());
        }
    }
    
    public void displayRentalHistory() {
        if (rentalHistory.getRentalHistory().isEmpty()) {
            System.out.println("  No rental history found.");
        } else {
            // Header with proper column widths
            System.out.printf("|%-10s | %-12s | %-20s | %-12s | %-12s |%n", 
                " Type", "Plate", "Customer", "Date", "Amount");
            System.out.println("|-------------------------------------------------------------------------------|");
            
            for (RentalRecord record : rentalHistory.getRentalHistory()) {                
                System.out.printf("| %-9s | %-12s | %-20s | %-12s | $%-11.2f |%n", 
                    record.getRecordType(), 
                    record.getVehicle().getLicensePlate(),
                    record.getCustomer().getCustomerName(),
                    record.getRecordDate().toString(),
                    record.getTotalAmount()
                );
            }
            System.out.println();
        }
    }
    
    public Vehicle findVehicleByPlate(String plate) {
        for (Vehicle v : vehicles) {
            if (v.getLicensePlate().equalsIgnoreCase(plate)) {
                return v;
            }
        }
        return null;
    }
    
    public Customer findCustomerById(int id) {
        for (Customer c : customers)
            if (c.getCustomerId() == id)
                return c;
        return null;
    }
    
    private void saveVehicle(Vehicle vehicle) {
    	try (PrintWriter out = new PrintWriter(new FileWriter(VEHICLE_FILE, true))) {
            String type;
            String extra;

            if (vehicle instanceof Car) {
                type = "CAR";
                extra = String.valueOf(((Car) vehicle).getNumSeats());
            } else if (vehicle instanceof Minibus) {
                type = "MINIBUS";
                extra = String.valueOf(((Minibus) vehicle).getInfo());
            } else if (vehicle instanceof PickupTruck) {
                type = "PICKUP";
                extra = ((PickupTruck) vehicle).getCargoSize() + ";" +
                        ((PickupTruck) vehicle).hasTrailer();
            } else {
                type = "UNKNOWN";
                extra = "";
            }

            out.println(
                    type + "," +
                    vehicle.getLicensePlate() + "," +
                    vehicle.getMake() + "," +
                    vehicle.getModel() + "," +
                    vehicle.getYear() + "," +
                    extra + "," +
                    vehicle.getStatus()
            );
        } catch (IOException e) {
            System.out.println("Error saving vehicle: " + e.getMessage());
        }
    }
    
    private void saveCustomer(Customer customer) {
        try (PrintWriter out = new PrintWriter(new FileWriter(CUSTOMER_FILE, true))) {
            out.println(customer.getCustomerId() + "," + customer.getCustomerName());
        } catch (IOException e) {
            System.out.println("Error saving customer: " + e.getMessage());
        }
    }

    private void saveRecord(RentalRecord record) {
        try (PrintWriter out = new PrintWriter(new FileWriter(RECORD_FILE, true))) {
            out.println(
                    record.getRecordType() + "," +
                    record.getVehicle().getLicensePlate() + "," +
                    record.getCustomer().getCustomerId() + "," +
                    record.getRecordDate().toString() + "," +
                    record.getTotalAmount()
            );
        } catch (IOException e) {
            System.out.println("Error saving record: " + e.getMessage());
        }
    }
}