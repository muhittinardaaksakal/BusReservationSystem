import java.util.Locale;

/**
 * Represents a standard bus with a specific seating configuration and methods for printing and handling bus-specific details.
 */
public class StandardBus extends Bus {

    /**
     * Constructs a new StandardBus with specified parameters.
     * Inherits parameters from the Bus class.
     *
     * @param id The unique identifier for the bus.
     * @param from The departure city of the bus.
     * @param to The destination city of the bus.
     * @param numberOfRows The number of rows of seats in the bus.
     * @param price The price of a standard seat.
     * @param refundCut The percentage deducted from the price for refunds.
     */
    public StandardBus(int id, String from, String to, int numberOfRows, double price, double refundCut) {
        super(id, from, to, numberOfRows, price, refundCut);
    }

    /**
     * Provides a string representation of the bus's seat configuration.
     * Marks sold seats with 'X' and available seats with '*'.
     * Organizes seats into rows for easier readability.
     *
     * @return A formatted string representing the seat configuration of the bus.
     */
    @Override
    protected String printSeatsConfiguration() {
        StringBuilder config = new StringBuilder();
        for (int i = 0; i < getSeatsSold().length; i++) {
            config.append(getSeatsSold()[i] ? "X" : "*");
            if ((i + 1) % 4 != 0) {  // Check if it's not the end of a row of 4 seats
                if ((i + 1) % 2 == 0) {
                    config.append(" | ");  // Add a pipe after every pair of seats
                } else {
                    config.append(" ");  // Add a space otherwise
                }
            }
            if ((i + 1) % 4 == 0 || i == getSeatsSold().length - 1) {
                config.append("\n");
            }
        }
        return config.toString().trim();  // Trim any trailing spaces or newline characters
    }

    /**
     * Calculates the total number of seats on the bus based on a 2+2 configuration per row.
     *
     * @return The total number of seats available on the bus.
     */
    @Override
    public int getTotalSeats() {
        return getNumberOfRows() * 4; // Each row has 4 seats in a standard bus.
    }

    /**
     * Generates a detailed description of the bus upon initialization.
     * Includes information about the bus's route, pricing, and refund policy.
     *
     * @return A descriptive string detailing the initialization parameters of the bus.
     */
    @Override
    String printDetails() {
        int seats = getNumberOfRows() * 4; // 2+2 configuration
        return "Voyage " + getId() + " was initialized as a standard (2+2) voyage from " + getFrom() + " to " + getTo() +
                " with " + String.format(Locale.US, "%.2f", getPrice()) + " TL priced " + seats + " regular seats. Note that refunds will be " +
                (int)getRefundCut() + "% less than the paid amount.";
    }
}
