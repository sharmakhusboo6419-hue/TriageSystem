public class Patient implements Comparable<Patient> {
    private String id;
    private String name;
    private int age;
    private Vitals vitals;
    private long arrivalTime; // to handle ties fairly (FIFO for same tier)

    public Patient(String id, String name, int age, Vitals vitals) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.vitals = vitals;
        this.arrivalTime = System.nanoTime(); // uniquely logs exact order
    }

    public int getTriageTier() {
        return vitals.calculateTier();
    }

    public String getName() {
        return name;
    }

    @Override
    public int compareTo(Patient other) {
        int myTier = this.getTriageTier();
        int otherTier = other.getTriageTier();

        // Remeber: Lower tier number (Tier 1) means HIGHER medical priority.
        if (myTier != otherTier) {
            return Integer.compare(myTier, otherTier);
        }
        // If tiers match, prioritize the person who has been waiting longer
        return Long.compare(this.arrivalTime, other.arrivalTime);
    }

    @Override
    public String toString() {
        return String.format("[Tier %d] ID: %s | Name: %s (%d yrs) | Vitals -> %s",
                getTriageTier(), id, name, age, vitals.toString());
    }
}