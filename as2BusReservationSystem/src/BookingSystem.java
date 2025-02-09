import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.io.File;;

public class BookingSystem {
    static String output;
    static Map<Integer, Bus> voyages = new HashMap<>();
    static StringBuilder log = new StringBuilder();

    public static void initVoyage(String type, int id, String from, String to, int numberOfRows, double price, Double refundCut, Double premiumFee) {
        Bus bus = null;
        switch (type) {
            case "Standard":
                bus = new StandardBus(id, from, to, numberOfRows, price, refundCut);
                break;
            case "Premium":
                bus = new PremiumBus(id, from, to, numberOfRows, price, refundCut, premiumFee);
                break;
            case "Minibus":
                bus = new Minibus(id, from, to, numberOfRows, price);
                break;
        }
        if (bus != null) {
            voyages.put(id, bus);
            log.append(bus.printDetails()).append("\n");
        }
    }

    public static void refundTicket(int voyageId, int... seatNumbers) {
        Bus voyage = voyages.get(voyageId);
        if (voyage == null) {
            log.append("ERROR: Voyage ").append(voyageId).append(" not found.\n");
            return;
        }
        if (voyage instanceof Minibus) {
            log.append("ERROR: Minibus tickets are not refundable!\n");
            return;
        }
        if (voyage.refundSeats(seatNumbers)) {
            double refundAmount = seatNumbers.length * (voyage.getPrice() * (1 - voyage.getRefundCut() / 100.0));
            if (voyage instanceof PremiumBus) {
                int premiumSeats = (int) Arrays.stream(seatNumbers).filter(seat -> seat % 3 == 1).count();
                int regularSeats = seatNumbers.length - premiumSeats;
                refundAmount = regularSeats * (voyage.getPrice() * (1 - voyage.getRefundCut() / 100.0)) + premiumSeats * (voyage.getPrice() * (1 + ((PremiumBus) voyage).getPremiumFee() / 100)* (1 - voyage.getRefundCut() / 100.0));
            }
            String seatNumbersStr = Arrays.toString(seatNumbers).replaceAll("[\\[\\]]", "").replace(", ", "-");
            String output = String.format(Locale.US, "Seat %s of the Voyage %d from %s to %s was successfully refunded for %.2f TL.", seatNumbersStr, voyageId, voyage.getFrom(), voyage.getTo(), refundAmount);
            log.append(output).append("\n");
        } else {
            log.append("ERROR: One or more seats are already empty!\n");
        }
    }

    public static void sellTicket(int voyageId, int... seatNumbers) {
        Bus voyage = voyages.get(voyageId);
        if (voyage == null) {
            log.append("ERROR: Voyage ").append(voyageId).append(" not found.\n");
            return;
        }

        if (voyage.sellSeats(seatNumbers)) {
            String seatNumbersStr = Arrays.toString(seatNumbers).replaceAll("[\\[\\]]", "").replace(", ", "-");
            double totalPrice = seatNumbers.length * voyage.getPrice();
            if (voyage instanceof PremiumBus) {
                int premiumSeats = (int) Arrays.stream(seatNumbers).filter(seat -> seat % 3 == 1).count();
                int regularSeats = seatNumbers.length - premiumSeats;
                totalPrice = regularSeats * voyage.getPrice() + premiumSeats * (voyage.getPrice() * (1 + ((PremiumBus) voyage).getPremiumFee() / 100));
            }
            String formattedOutput = String.format(Locale.US, "Seat %s of the Voyage %d from %s to %s was successfully sold for %.2f TL.", seatNumbersStr, voyageId, voyage.getFrom(), voyage.getTo(), totalPrice);
            log.append(formattedOutput).append("\n");
        } else {
            log.append("ERROR: One or more seats already sold!\n");
        }
    }

    public static void printVoyage(int voyageId) {
        Bus voyage = voyages.get(voyageId);
        if (voyage == null) {
            log.append("ERROR: There is no voyage with ID of ").append(voyageId).append("!\n");
            return;
        }
        log.append(voyage.printVoyageDetails()).append("\n");
    }

    public static void cancelVoyage(int voyageId) {
        Bus voyage = voyages.get(voyageId);
        if (voyage == null) {
            log.append("ERROR: There is no voyage with ID of ").append(voyageId).append("!\n");
            return;
        }
        double totalRefund = 0.0;
        int[] seatsToRefund = new int[voyage.getSeatsSold().length];
        int index = 0;
        for (int i = 0; i < voyage.getSeatsSold().length; i++) {
            if (voyage.getSeatsSold()[i]) { // if the seat was sold
                seatsToRefund[index++] = i + 1; // Store 1-based seat number

            }
        }

        if (voyage instanceof PremiumBus) {
            int premiumSeatsCount = (int) Arrays.stream(seatsToRefund, 0, index)
                    .filter(seat -> ((PremiumBus) voyage).isPremiumSeat(seat))
                    .count();
            int regularSeatsCount = index - premiumSeatsCount;
            double regularSeatRefund = regularSeatsCount * voyage.getPrice();
            double premiumSeatRefund = premiumSeatsCount * (voyage.getPrice() * (1 + ((PremiumBus) voyage).getPremiumFee() / 100));
            totalRefund = regularSeatRefund + premiumSeatRefund;
        } else {
            totalRefund = index * voyage.getPrice();
        }

        voyage.setRevenue(voyage.getRevenue()-totalRefund);

        log.append("Voyage ").append(voyageId).append(" was successfully cancelled!\nVoyage details can be found below:\n");
        log.append(voyage.printVoyageDetails()).append("\n");
        voyages.remove(voyageId);
    }

    public static void printZReport() {
        log.append("Z Report:\n");
        if (voyages.isEmpty()) {
            log.append("----------------\nNo Voyages Available!\n----------------\n");
        } else {
            voyages.values().stream()
                    .sorted(Comparator.comparingInt(v -> v.getId()))
                    .forEach(voyage -> {
                        log.append("----------------\n").append(voyage.printVoyageDetails()).append("\n");
                    });
            log.append("----------------\n");
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("ERROR: This program works exactly with two command line arguments, the first one is the path to the input file whereas the second one is the path to the output file. Sample usage can be as follows: \"java BookingSystem input.txt output.txt\". Program is going to terminate!");
            return;
        }
        String input = args[0];
        output = args[1];
        File inputFile = new File(input);
        if (!inputFile.exists() || !inputFile.canRead()) {
            System.out.println("ERROR: This program cannot read from the \"" + input + "\", either this program does not have read permission to read that file or file does not exist. Program is going to terminate!");
            return;
        }

        String[] inputlines = FileInput.readFile(input, true, false);
        int lastIndex = inputlines.length - 1;

        for (String line : inputlines) {
            line = line.trim();

            log.append("COMMAND: ").append(line).append("\n");
            String[] parts = line.split("\t");
            switch (parts[0]) {
                case "INIT_VOYAGE":
                    if (!("Premium".equals(parts[1]) || "Minibus".equals(parts[1]) || "Standard".equals(parts[1]))) {

                        log.append("ERROR: Erroneous usage of \"INIT_VOYAGE\" command!\n");
                        break; // Exit the switch-case block after logging the error
                    }
                    if ("Premium".equals(parts[1]) && parts.length != 9) {

                        log.append("ERROR: Erroneous usage of \"INIT_VOYAGE\" command!\n");
                        break; // Exit the switch-case block after logging the error
                    }
                    if ("Standard".equals(parts[1]) && parts.length != 8){

                        log.append("ERROR: Erroneous usage of \"INIT_VOYAGE\" command!\n");
                        break;
                    }
                    if ("Minibus".equals(parts[1]) && parts.length != 7){

                        log.append("ERROR: Erroneous usage of \"INIT_VOYAGE\" command!\n");
                        break;
                    }


                    int id = Integer.parseInt(parts[2]);
                    if (id <= 0){

                        log.append("ERROR: ").append(parts[2]).append(" is not a positive integer, ID of a voyage must be a positive integer!\n");
                        break;
                    }
                    if (voyages.containsKey(id)) {

                        log.append("ERROR: There is already a voyage with ID of ").append(id).append("!\n");
                        break;
                    }
                    int numberOfRows = Integer.parseInt(parts[5]);
                        if (numberOfRows <= 0){

                            log.append("ERROR: ").append(parts[5]).append( " is not a positive integer, number of seat rows of a voyage must be a positive integer!\n");
                            break;
                        }
                    double price = Double.parseDouble(parts[6]);
                        if (price <= 0){

                            log.append("ERROR: ").append(parts[6]).append( " is not a positive number, price must be a positive number!\n");
                            break;
                        }
                    Double refundCut = null;
                    if (parts.length > 7 && !parts[7].isEmpty()) {

                        try {
                            refundCut = Double.parseDouble(parts[7]);
                            int intRefundCut = Integer.parseInt(parts[7]);
                            // Check if the refundCut is outside the range of 0 to 100
                            if (refundCut < 0 || refundCut > 100) {

                                log.append("ERROR: ").append(intRefundCut).append(" is not an integer that is in range of [0, 100], refund cut must be an integer that is in range of [0, 100]!\n");
                                break; // Reset refundCut or handle as needed
                            }
                        } catch (NumberFormatException e) {

                            log.append("ERROR: Invalid format for refund cut, must be a numeric value.\n");
                            break; // Reset refundCut or handle as needed
                        }
                    }



                    Double premiumFee = null;
                    if (parts.length > 8 && !parts[8].isEmpty()) {
                        try {
                            premiumFee = Double.parseDouble(parts[8]);
                            int intPremiumFee = Integer.parseInt(parts[8]);
                            if (premiumFee < 0) {

                                log.append("ERROR: ").append(intPremiumFee).append(" is not a non-negative integer, premium fee must be a non-negative integer!\n");
                                break; // Optionally reset premiumFee or handle as needed
                            }
                        } catch (NumberFormatException e) {

                            log.append("ERROR: ").append(parts[8]).append(" is not a valid integer.\n");
                            break; // Optionally reset premiumFee or handle as needed
                        }
                    }

                    initVoyage(parts[1], id, parts[3], parts[4], numberOfRows, price, refundCut, premiumFee);
                    break;
                case "SELL_TICKET":
                    if (parts.length != 3) {
                        log.append("ERROR: Erroneous usage of \"SELL_TICKET\" command!\n");

                        break; // Exit the switch-case block after logging the error
                    }
                    int Id2 = Integer.parseInt(parts[1]);

                    if (!voyages.containsKey(Id2)) {
                        log.append("ERROR: There is no voyage with ID of ").append(Id2).append("!\n");

                        break; // Exit the switch-case block after logging the error
                    }
                    String[] seatStrings = parts[2].split("_");
                    int totalSeats = voyages.get(Id2).getTotalSeats();
                    boolean validSeats = true;
                    int[] seatNumbers = Arrays.stream(seatStrings).mapToInt(Integer::parseInt).toArray();
                    for (int seatNumber : seatNumbers) {
                        if (seatNumber <= 0) {
                            log.append("ERROR: ").append(seatNumber).append(" is not a positive integer, seat number must be a positive integer!\n");

                            validSeats = false;
                            break; // Exit the loop and skip selling tickets as there is an invalid seat number
                        }
                        if (seatNumber > totalSeats) {
                            log.append("ERROR: There is no such a seat!\n");

                            validSeats = false;
                            break; // Exit the loop and skip selling tickets as there is an invalid seat number
                        }
                    }
                    if (validSeats) {
                        sellTicket(Id2, seatNumbers);
                    }
                    break;
                case "PRINT_VOYAGE":
                    if (parts.length != 2) {
                        log.append("ERROR: Erroneous usage of \"PRINT_VOYAGE\" command!\n");

                        break; // Exit the switch-case block after logging the error
                    }

                    int Id3;
                    try {
                        Id3 = Integer.parseInt(parts[1]);
                        if (Id3 <= 0) {  // Check if ID is not a positive integer
                            log.append("ERROR: ").append(Id3).append(" is not a positive integer, ID of a voyage must be a positive integer!\n");

                            break; // Exit the switch-case block after logging the error
                        }
                    } catch (NumberFormatException e) {
                        log.append("ERROR: Invalid format for ID, ID must be an integer.\n");

                        break; // Exit the switch-case block after logging the error
                    }

                    printVoyage(Id3);
                    break;

                case "CANCEL_VOYAGE":
                    if (parts.length != 2) {
                        log.append("ERROR: Erroneous usage of \"CANCEL_VOYAGE\" command!\n");

                        break; // Exit the switch-case block after logging the error
                    }

                    int cancelVoyageId;
                    try {
                        cancelVoyageId = Integer.parseInt(parts[1]);
                        if (cancelVoyageId <= 0) {
                            log.append("ERROR: ").append(cancelVoyageId).append(" is not a positive integer, ID of a voyage must be a positive integer!\n");

                            break; // Exit the switch-case block after logging the error
                        }
                    } catch (NumberFormatException e) {
                        log.append("ERROR: Invalid ID format. ID must be an integer.\n");

                        break; // Exit the switch-case block after logging the error
                    }

                    if (!voyages.containsKey(cancelVoyageId)) {
                        log.append("ERROR: There is no voyage with ID of ").append(cancelVoyageId).append("!\n");

                        break; // Exit the switch-case block after logging the error
                    }

                    // Proceed with cancelling the voyage
                    cancelVoyage(cancelVoyageId);
                    break;

                case "Z_REPORT":
                    // Check if parts array length is 1 and the command is "Z_REPORT"
                    if (parts.length == 1 && "Z_REPORT".equals(parts[0])) {
                        printZReport();
                        break;
                    } else {

                        log.append("ERROR: Erroneous usage of \"Z_REPORT\" command!\n");
                        break;
                    }


                case "REFUND_TICKET":
                    if (parts.length != 3) {
                        log.append("ERROR: Erroneous usage of \"REFUND_TICKET\" command!\n");

                        break; // Exit the switch-case block after logging the error
                    }
                    int refundVoyageId = Integer.parseInt(parts[1]);
                    if (!voyages.containsKey(refundVoyageId)) {
                        log.append("ERROR: There is no voyage with ID of ").append(refundVoyageId).append("!\n");

                        break; // Exit the switch-case block after logging the error
                    }
                    String[] refundSeatStrings = parts[2].split("_");
                    int totalSeatsRefund = voyages.get(refundVoyageId).getTotalSeats();
                    int[] refundSeatNumbers = Arrays.stream(refundSeatStrings).mapToInt(Integer::parseInt).toArray();
                    boolean validSeatsRefund = true;
                    int[] seatNumbersRefund = Arrays.stream(refundSeatStrings).mapToInt(Integer::parseInt).toArray();
                    for (int seatNumber : seatNumbersRefund) {
                        if (seatNumber <= 0) {
                            log.append("ERROR: ").append(seatNumber).append(" is not a positive integer, seat number must be a positive integer!\n");

                            validSeatsRefund = false;
                            break; // Exit the loop and skip selling tickets as there is an invalid seat number
                        }
                        if (seatNumber > totalSeatsRefund) {
                            log.append("ERROR: There is no such a seat!\n");

                            validSeatsRefund = false;
                            break; // Exit the loop and skip selling tickets as there is an invalid seat number
                        }
                    }
                    if (validSeatsRefund) {
                        refundTicket(refundVoyageId, refundSeatNumbers);
                    }

                    break;
                default:
                    log.append("ERROR: There is no command namely ").append(parts[0]).append("!\n");

                    break;
            }
        }
        if (!"Z_REPORT".equals(inputlines[lastIndex].split("\t")[0])){
            printZReport();
        }
        StringBuilder finalLog = new StringBuilder(log);
        if (finalLog.length() > 0 && finalLog.charAt(finalLog.length() - 1) == '\n') {
            finalLog.setLength(finalLog.length() - 1); // Remove the last newline character
        }

        // Write the adjusted log to the output file
        FileOutput.writeToFile(output, finalLog.toString(), false, false);
    }
}
