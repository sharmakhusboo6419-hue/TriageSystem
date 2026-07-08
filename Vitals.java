
public class Vitals {
    private int heartRate; // beats per minute (bpm)
    private int sysBP; // Systolic Blood Pressure (mmHg)
    private int oxygenSat; // SpO2 percentage
    private int respiratoryRate; // breaths per minute

    public Vitals(int heartRate, int sysBP, int oxygenSat, int respiratoryRate) {
        this.heartRate = heartRate;
        this.sysBP = sysBP;
        this.oxygenSat = oxygenSat;
        this.respiratoryRate = respiratoryRate;
    }

    // Getters
    public int getHeartRate() {
        return heartRate;
    }

    public int getSysBP() {
        return sysBP;
    }

    public int getOxygenSat() {
        return oxygenSat;
    }

    public int getRespiratoryRate() {
        return respiratoryRate;
    }

    /**
     * Triage Logic Algorithm
     * Tier 1 = Critical, Tier 2 = Urgent, Tier 3 = Stable
     */
    public int calculateTier() {
        // Tier 1: Immediate life-threatening vitals
        if (oxygenSat < 90 || heartRate < 40 || heartRate > 140 || sysBP < 80) {
            return 1;
        }
        // Tier 2: Severe abnormalities but not immediate arrest
        if ((oxygenSat >= 90 && oxygenSat <= 94) || heartRate > 110 || sysBP > 160 || respiratoryRate > 24) {
            return 2;
        }
        // Tier 3: Within safe, normal structural boundaries
        return 3;
    }

    @Override
    public String toString() {
        return String.format("HR: %dbpm, BP: %d, SpO2: %d%%, RR: %d", heartRate, sysBP, oxygenSat, respiratoryRate);
    }
}
