import java.util.Locale;

/**
 * Represents a minibus type of bus in the booking system.
 * This class extends from the Bus class and specializes behavior for minibuses.
 */
public class Minibus extends Bus {

    /**
     * Constructs a Minibus object with specific parameters.
     *
     * @param id The unique identifier for the voyage.
     * @param from The departure location of the voyage.
     * @param to The destination location of the voyage.
     * @param numberOfRows The number of rows of seats in the minibus.
     * @param price The price per seat for the voyage.
     */
    public Minibus(int id, String from, String to, int numberOfRows, double price) {
        super(id, from, to, numberOfRows, price, 0); // Minibuses do not have refund cuts.
    }

    /**
     * Generates and returns a string representing the configuration of seats in the minibus.
     * Seats are printed in pairs, separated by a space, and each pair is on a new line.
     *
     * @return A formatted string representing the seat configuration.
     */
    protected String printSeatsConfiguration() {
        StringBuilder config = new StringBuilder();
        for (int i = 0; i < getSeatsSold().length; i++) {
            config.append(getSeatsSold()[i] ? "X" : "*");
            // Append a space only if it's not the end of a pair of seats
            if ((i + 1) % 2 != 0) {  // Check if it's not the second seat in the pair
                config.append(" ");
            }
            if ((i + 1) % 2 == 0) {  // New line after every 2 seats
                config.append("\n");
            }
        }
        return config.toString().trim(); // Ensure no trailing new lines or spaces
    }

    /**
     * Returns the total number of seats available in the minibus.
     * It assumes each row has 2 seats.
     *
     * @return The total number of seats in the minibus.
     */
    @Override
    public int getTotalSeats() {
        // Assuming each row has 2 seats in a Minibus.
        return getNumberOfRows() * 2;
    }

    /**
     * Provides a detailed string describing the minibus voyage.
     * Includes ID, route, pricing, and refund information.
     *
     * @return A string detailing the minibus voyage initialization.
     */
    @Override
    String printDetails() {
        int seats = getTotalSeats(); // Use the getTotalSeats method to get the number of seats
        String output = "Voyage " + getId() + " was initialized as a minibus (2) voyage from " + getFrom() + " to " + getTo() +
                " with " + String.format(Locale.US, "%.2f", getPrice()) + " TL priced " + seats + " regular seats. Note that minibus tickets are" +
                " not refundable.";
        return output;
    }

}
