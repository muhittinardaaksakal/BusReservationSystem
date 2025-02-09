import java.util.Locale;

/**
 * Represents a PremiumBus that extends the basic Bus functionality with premium seating options.
 * This class provides mechanisms to handle pricing and seating configurations specific to premium buses.
 */
public class PremiumBus extends Bus {
    private double premiumFee;

    /**
     * Constructs a new PremiumBus with specified parameters and premium fee.
     * Inherits parameters from the Bus class and adds a premium fee specific to premium buses.
     *
     * @param id The unique identifier for the bus.
     * @param from The departure city of the bus.
     * @param to The destination city of the bus.
     * @param numberOfRows The number of rows of seats in the bus.
     * @param price The base price of a regular seat.
     * @param refundCut The percentage deducted from the refund amount.
     * @param premiumFee Additional percentage added to the base price for premium seats.
     */
    public PremiumBus(int id, String from, String to, int numberOfRows, double price, double refundCut, double premiumFee) {
        super(id, from, to, numberOfRows, price, refundCut);
        setPremiumFee(premiumFee);
    }

    /**
     * Calculates the price of a seat based on its number.
     * Premium seats have an additional fee over the regular price.
     *
     * @param seatNumber The seat number to calculate the price for.
     * @return The calculated price of the seat.
     */
    @Override
    protected double calculateSeatPrice(int seatNumber) {
        if (isPremiumSeat(seatNumber)) {
            return getPrice() * (1 + premiumFee / 100); // Premium seats cost more
        }
        return getPrice(); // Regular price for other seats
    }

    /**
     * Refunds one or more seats on the bus. This method considers the premium fee when calculating the refund amount.
     *
     * @param seatNumbers The seat numbers to refund.
     * @return true if all seats are successfully refunded, false otherwise.
     */
    @Override
    public boolean refundSeats(int... seatNumbers) {
        double refundAmount = 0;
        for (int seatNumber : seatNumbers) {
            if (seatNumber <= 0 || seatNumber > getSeatsSold().length || !getSeatsSold()[seatNumber - 1]) {
                return false; // Seat is invalid or not sold
            }
        }

        for (int seatNumber : seatNumbers) {
            seatsSold[seatNumber - 1] = false;
            double seatPrice = isPremiumSeat(seatNumber) ?
                    getPrice() * (1 + premiumFee / 100) :
                    getPrice(); // Determine if the seat is premium and calculate accordingly
            refundAmount += seatPrice * (1 - getRefundCut() / 100); // Calculate refund amount considering the refund cut
        }

        setRevenue(getRevenue() - refundAmount);
        return true;
    }

    /**
     * Determines if a seat is a premium seat based on its number.
     *
     * @param seatNumber The seat number to check.
     * @return true if the seat is a premium seat, false otherwise.
     */
    public boolean isPremiumSeat(int seatNumber) {
        return seatNumber % 3 == 1; // Assuming every third seat is a premium seat
    }

    /**
     * Provides a string representation of the bus's seat configuration with premium indicators.
     * Marks sold seats with 'X' and available seats with '*'. Premium seats are indicated differently.
     *
     * @return A formatted string representing the seat configuration of the bus.
     */
    @Override
    protected String printSeatsConfiguration() {
        StringBuilder config = new StringBuilder();
        int seatCounter = 0; // to keep track of seat positions
        for (int i = 0; i < getSeatsSold().length; i++) {
            if (seatCounter % 3 == 0) { // every start of a new set
                if (seatCounter != 0) { // not the first seat
                    config.append("\n"); // add a newline for previous row before starting new one
                }
                config.append(getSeatsSold()[i] ? "X" : "*");
            } else {
                config.append(" ").append(getSeatsSold()[i] ? "X" : "*");
            }

            if (seatCounter % 3 == 0) { // Add divider after the first seat of every set
                config.append(" |");
            }
            seatCounter++;
        }
        if (seatCounter % 3 != 0) { // Add a newline if the last row is not empty and didn't end with a newline
            config.append("\n");
        }
        return config.toString();
    }

    /**
     * Calculates the total number of seats on the bus based on a specific configuration for premium buses.
     *
     * @return The total number of seats available on the bus.
     */
    @Override
    public int getTotalSeats() {
        return getNumberOfRows() * 3; // Assuming each row has 3 seats.
    }

    /**
     * Generates a detailed description of the bus upon initialization.
     * Includes information about the bus's route, pricing for regular and premium seats, and refund policy.
     *
     * @return A descriptive string detailing the initialization parameters of the bus.
     */
    @Override
    public String printDetails() {
        int totalSeats = getTotalSeats(); // Total number of seats (3 seats per row as an example)
        int premiumSeats = totalSeats / 3; // 1/3 of the seats are premium
        int regularSeats = totalSeats - premiumSeats; // 2/3 of the seats are regular
        double premiumPrice = getPrice() * (1 + premiumFee / 100);

        return String.format(Locale.US, "Voyage %d was initialized as a premium (1+2) voyage from %s to %s with %.2f TL priced %d regular seats and %.2f TL priced %d premium seats. Note that refunds will be %d%% less than the paid amount.",
                getId(), getFrom(), getTo(), getPrice(), regularSeats, premiumPrice, premiumSeats, (int) getRefundCut());
    }

    /**
     * Gets the additional fee percentage for premium seats.
     *
     * @return The premium fee percentage.
     */
    public double getPremiumFee() {
        return premiumFee;
    }

    /**
     * Sets the additional fee percentage for premium seats.
     *
     * @param premiumFee The premium fee percentage to set.
     */
    public void setPremiumFee(double premiumFee) {
        this.premiumFee = premiumFee;
    }
}
