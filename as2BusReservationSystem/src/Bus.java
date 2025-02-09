import java.util.Locale; // Required for formatting specific locales

/**
 * Abstract base class representing a generic bus with capabilities to manage voyages,
 * including selling and refunding seats, and printing details.
 */
public abstract class Bus {
    private int id;
    private String from;
    private String to;
    private int numberOfRows;
    private double price;
    private double refundCut;
    protected boolean[] seatsSold;
    private double revenue = 0;

    /**
     * Constructor for Bus.
     *
     * @param id The unique identifier for the bus.
     * @param from Starting point of the voyage.
     * @param to Destination of the voyage.
     * @param numberOfRows Number of rows in the bus.
     * @param price Price per seat.
     * @param refundCut Percentage of the price deducted on refund.
     */
    public Bus(int id, String from, String to, int numberOfRows, double price, double refundCut) {
        setId(id);
        setFrom(from);
        setTo(to);
        setNumberOfRows(numberOfRows);
        setPrice(price);
        setRefundCut(refundCut);
        this.seatsSold = new boolean[getTotalSeats()];
    }

    /**
     * Abstract method to get the total number of seats in the bus.
     * @return Total number of seats.
     */
    abstract int getTotalSeats();

    /**
     * Sells one or more seats on the bus.
     *
     * @param seatNumbers The seat numbers to sell.
     * @return true if all seats are successfully sold, false otherwise.
     */
    public boolean sellSeats(int... seatNumbers) {
        double totalSalePrice = 0.0;
        for (int seatNumber : seatNumbers) {
            if (seatNumber <= 0 || seatNumber > seatsSold.length || seatsSold[seatNumber - 1]) {
                return false; // Invalid seat number or seat already sold
            }
        }

        for (int seatNumber : seatNumbers) {
            seatsSold[seatNumber - 1] = true;
            totalSalePrice += calculateSeatPrice(seatNumber);
        }

        revenue += totalSalePrice;
        return true;
    }

    /**
     * Calculates the price of a specific seat.
     *
     * @param seatNumber The seat number to calculate the price for.
     * @return The price of the seat.
     */
    protected double calculateSeatPrice(int seatNumber) {
        return price; // Default behavior, return the standard price
    }

    /**
     * Prints details of the current voyage.
     *
     * @return Formatted string of voyage details including revenue.
     */
    public String printVoyageDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Voyage ").append(getId()).append("\n");
        details.append(from).append("-").append(to).append("\n");
        details.append(printSeatsConfiguration() + "\n");
        details.append(String.format(Locale.US, "Revenue: %.2f", revenue));
        return details.toString();
    }

    /**
     * Abstract method to print the seats configuration.
     * @return Formatted string representing the seats configuration.
     */
    abstract String printSeatsConfiguration();

    /**
     * Refunds one or more seats on the bus.
     *
     * @param seatNumbers The seat numbers to refund.
     * @return true if all seats are successfully refunded, false otherwise.
     */
    public boolean refundSeats(int... seatNumbers) {
        double refundAmount = 0;
        for (int seatNumber : seatNumbers) {
            if (seatNumber <= 0 || seatNumber > seatsSold.length || !seatsSold[seatNumber - 1]) {
                return false; // Seat is invalid or not sold, fail the entire operation
            }
        }

        for (int seatNumber : seatNumbers) {
            seatsSold[seatNumber - 1] = false;
            refundAmount += price - (price * refundCut / 100);
        }

        revenue -= refundAmount;
        return true; // Success
    }

    /**
     * Abstract method to print detailed information about the bus.
     * @return Detailed information string.
     */
    abstract String printDetails();

    // Getter and setter methods
    // Getter and Setter methods
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
        this.seatsSold = new boolean[getTotalSeats()]; // Reinitialize seats array if number of rows changes
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getRefundCut() {
        return refundCut;
    }

    public void setRefundCut(double refundCut) {
        this.refundCut = refundCut;
    }

    public boolean[] getSeatsSold() {
        return seatsSold.clone(); // Return a copy to protect the internal array
    }

    public double getRevenue() {
        return revenue;
    }

    public void setRevenue(double revenue) {
        this.revenue = revenue;
    }

    public void setSeatsSold(boolean[] seatsSold) {
        this.seatsSold = seatsSold;
    }

}
