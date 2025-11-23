public abstract class Vehicle {
    private String licensePlate;
    private String make;
    private String model;
    private int year;
    private VehicleStatus status;

    public enum VehicleStatus { Available, Held, Rented, UnderMaintenance, OutOfService }

    public Vehicle(String make, String model, int year) {
        this.make = capitalize(make);
        this.model = capitalize(model);
        this.year = year;
        this.status = VehicleStatus.Available;
        this.licensePlate = null;
    }
    
    protected String capitalize(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        s = s.trim().toLowerCase();
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }


    public Vehicle() {
        this(null, null, 0);
    }

    public void setLicensePlate(String plate) {
        if (!isValidPlate(plate)) {
            throw new IllegalArgumentException("Invalid license plate: " + plate);
        }
        this.licensePlate = plate.toUpperCase();
    }

    public static boolean isValidPlate(String plate) {
        if (plate == null) return false;
        String trimmed = plate.trim();

        return trimmed.matches("[A-Za-z0-9]{2,8}");
    }

    public void setStatus(VehicleStatus status) {
    	this.status = status;
    }

    public String getLicensePlate() { return licensePlate; }

    public String getMake() { return make; }

    public String getModel() { return model;}

    public int getYear() { return year; }

    public VehicleStatus getStatus() { return status; }

    public String getInfo() {
        return "| " + licensePlate + " | " + make + " | " + model + " | " + year + " | " + status + " |";
    }

}
