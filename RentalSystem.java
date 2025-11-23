import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.time.format.DateTimeParseException;

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
    
    public void loadData() {
        loadVehicles();
        loadCustomers();
        loadRecords();
    }
    private void loadVehicles() {
        File file = new File(VEHICLE_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 7) {
                    continue; // skip malformed lines
                }

                String type = parts[0];
                String plate = parts[1];
                String make = parts[2];
                String model = parts[3];
                int year = Integer.parseInt(parts[4]);
                String extra = parts[5];
                String statusStr = parts[6];

                Vehicle v = null;
                if ("CAR".equals(type)) {
                    int seats = Integer.parseInt(extra);
                    v = new Car(make, model, year, seats);
                } else if ("MINIBUS".equals(type)) {
                    boolean accessible = Boolean.parseBoolean(extra);
                    v = new Minibus(make, model, year, accessible);
                } else if ("PICKUP".equals(type)) {
                    String[] ex = extra.split(";");
                    double cargo = Double.parseDouble(ex[0]);
                    boolean hasTrailer = Boolean.parseBoolean(ex[1]);
                    v = new PickupTruck(make, model, year, cargo, hasTrailer);
                }

                if (v != null) {
                    v.setLicensePlate(plate);
                    if ("Rented".equalsIgnoreCase(statusStr)) {
                        v.setStatus(Vehicle.VehicleStatus.Rented);
                    } else {
                        v.setStatus(Vehicle.VehicleStatus.Available);
                    }
                    vehicles.add(v);
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading vehicles: " + e.getMessage());
        }
    }
    private void loadCustomers() {
        File file = new File(CUSTOMER_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 2) {
                    continue;
                }

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                customers.add(new Customer(id, name));
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading customers: " + e.getMessage());
        }
    }
    private void loadRecords() {
        File file = new File(RECORD_FILE);
        if (!file.exists()) {
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 5) {
                    continue;
                }

                String type = parts[0];
                String plate = parts[1];
                int customerId = Integer.parseInt(parts[2]);
                LocalDate date = LocalDate.parse(parts[3]);
                double amount = Double.parseDouble(parts[4]);

                Vehicle v = findVehicleByPlate(plate);
                Customer c = findCustomerById(customerId);
                if (v == null || c == null) {
                    continue;
                }

                RentalRecord record = new RentalRecord(v, c, date, amount, type);
                rentalHistory.addRecord(record);
            }
        } catch (IOException | NumberFormatException | DateTimeParseException e) {
            System.out.println("Error loading records: " + e.getMessage());
        }
    }

}