import java.util.PriorityQueue;
import java.util.Scanner;

public class TriageSystem {
    public static void main(String[] Easton) {
        PriorityQueue<Patient> triageQueue = new PriorityQueue<>();
        Scanner scanner = new Scanner(System.in);

        // Pre-populating some dummy patients to see the sorting engine in action
        // immediately
        triageQueue.add(new Patient("P001", "John Doe", 45, new Vitals(75, 120, 98, 16))); // Stable (Tier 3)
        triageQueue.add(new Patient("P002", "Jane Smith", 29, new Vitals(145, 75, 88, 28))); // Critical (Tier 1)
        triageQueue.add(new Patient("P003", "Aarav Mehta", 62, new Vitals(115, 165, 93, 22)));// Urgent (Tier 2)

        while (true) {
            System.out.println("\n🏥 EMERGENCY ROOM PATIENT TRIAGE MATRIX");
            System.out.println("1. Register & Triage New Patient");
            System.out.println("2. Call Next Most Critical Patient for Treatment");
            System.out.println("3. View Current Triage Queue Status");
            System.out.println("4. Exit System");
            System.out.print("Select an option (1-4): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("Enter Patient ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Enter Patient Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Patient Age: ");
                    int age = Integer.parseInt(scanner.nextLine());

                    System.out.println("--- Input Vitals ---");
                    System.out.print("Heart Rate (bpm): ");
                    int hr = Integer.parseInt(scanner.nextLine());
                    System.out.print("Systolic Blood Pressure (mmHg): ");
                    int bp = Integer.parseInt(scanner.nextLine());
                    System.out.print("Oxygen Saturation (SpO2%): ");
                    int o2 = Integer.parseInt(scanner.nextLine());
                    System.out.print("Respiratory Rate (breaths/min): ");
                    int rr = Integer.parseInt(scanner.nextLine());

                    Vitals patientVitals = new Vitals(hr, bp, o2, rr);
                    Patient newPatient = new Patient(id, name, age, patientVitals);
                    triageQueue.add(newPatient);

                    System.out.println("\n✅ Patient dynamically triaged into " + newPatient);
                    break;

                case "2":
                    if (triageQueue.isEmpty()) {
                        System.out.println("🎉 No patients left in the queue. Waiting room is empty!");
                    } else {
                        // poll() automatically pulls the element at the top of the priority heap
                        Patient nextPatient = triageQueue.poll();
                        System.out.println("\n🚨 CALLING PATIENT IMMEDIATELY TO TRIAGE BAY:");
                        System.out.println("👉 " + nextPatient);
                    }
                    break;

                case "3":
                    if (triageQueue.isEmpty()) {
                        System.out.println("Queue is empty.");
                    } else {
                        System.out.println("\n--- Current Waiting Room (Sorted by Critical Urgency) ---");
                        // Creating a temporary copy array to print elements in strict sorted priority
                        // order
                        PriorityQueue<Patient> tempQueue = new PriorityQueue<>(triageQueue);
                        while (!tempQueue.isEmpty()) {
                            System.out.println(tempQueue.poll());
                        }
                    }
                    break;

                case "4":
                    System.out.println("Shutting down hospital triage dashboard...");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option. Please enter a number from 1 to 4.");
            }
        }
    }
}