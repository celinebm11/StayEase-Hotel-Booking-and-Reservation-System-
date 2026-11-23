import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

/**
 * StayEase - MODEL
 * Stores the data used by the hotel booking system.
 */
public class Model {
    static final double TAX = 0.12;
}

// Room status
enum Status {

    AVAILABLE("Available"),
    RESERVED("Reserved"),
    OCCUPIED("Occupied");

    final String label;

    Status(String label) {
        this.label = label;
    }
}

// Booking status
enum BookingStatus {
    PENDING_PAYMENT,
    PENDING_VERIFICATION,
    CONFIRMED,
    CHECKED_IN,
    COMPLETED,
    CANCELLED,
    REJECTED
}

// Payment status
enum PaymentStatus {
    UNPAID,
    PENDING_VERIFICATION,
    APPROVED,
    REJECTED,
    PAID
}

enum RefundStatus {
    NOT_APPLICABLE,
    PROCESSED,
    NOT_ELIGIBLE
}

// Room information
class Room {
    int number;
    String name;
    String type;
    String bed;
    String desc;
    double price;
    int capacity;
    Status status = Status.AVAILABLE;
}

// Booking information
class Booking {

    String ref;
    String guest;
    String email;
    String phone;
    String stay;
    String method;

    Room room;

    int nights;
    int guests;

    // Booking dates
    LocalDate checkInDate;
    LocalDate checkOutDate;

    // Discount information
    String discountType = "None";
    String discountIdNumber;
    String discountIdImagePath;
    double discountRate;
    double discountAmount;

    // GCash information
    String gcashName;
    String gcashNumber;
    String gcashReference;
    String gcashScreenshotPath;

    // Booking and payment status
    BookingStatus bookingStatus = BookingStatus.PENDING_PAYMENT;
    PaymentStatus paymentStatus = PaymentStatus.UNPAID;

    // Refund information
    RefundStatus refundStatus = RefundStatus.NOT_APPLICABLE;
    double refundAmount = 0.0;

    // Booking calculations
    double subtotal;
    double tax;
    double total;
}

// Shared hotel data
class Hotel {

    final List<Room> rooms = seedRooms();
    final List<Booking> bookings = new ArrayList<>();

    // Create the 22 sample rooms
    static List<Room> seedRooms() {
        
        Object[][] data = {
            {101, "Classic Single"},
            {102, "Classic Single"},
            {103, "Courtyard Single"},
            {104, "Courtyard Single"},
            {105, "Parkside Single"},
            {106, "Parkside Single"},

            {201, "Classic Double"},
            {202, "Classic Double"},
            {203, "Courtyard Double"},
            {204, "Courtyard Double"},
            {205, "Garden Double"},
            {206, "Garden Double"},

            {301, "Deluxe King"},
            {302, "Deluxe King"},
            {303, "Garden Deluxe"},
            {304, "Garden Deluxe"},
            {305, "Corner Deluxe"},
            {306, "Corner Deluxe"},

            {401, "Skyline Suite"},
            {402, "Skyline Suite"},
            {403, "Terrace Suite"},
            {404, "Terrace Suite"}
        };

        List<Room> roomList = new ArrayList<>();

        for (Object[] item : data) {

            Room room = new Room();

            room.number = (Integer) item[0];
            room.name = (String) item[1];

            int floor = room.number / 100;

            switch (floor) {

                case 1:
                    room.type = "Single";
                    room.price = 1200.00;
                    room.bed = "1 double bed";
                    room.capacity = 2;
                    room.desc = "A comfortable room with a single bed, good for one person, and a relaxing atmosphere.";
                    break;

                case 2:
                    room.type = "Double";
                    room.price = 2500.00;
                    room.bed = "2 single bed";
                    room.capacity = 4;
                    room.desc = "A spacious room with 2 single beds, good for two people, and a relaxing atmosphere.";
                    break;

                case 3:
                    room.type = "Deluxe";
                    room.price = 5000.00;
                    room.bed = "1 queen bed & 1 double bed";
                    room.capacity = 5;
                    room.desc = "An elegant room with a queen bed and a double bed, good for families, and a relaxing lounge area.";
                    break;

                default:
                    room.type = "Suite";
                    room.price = 7000.00;
                    room.bed = "King size bed";
                    room.capacity = 4;
                    room.desc = "A spacious suite with a king bed, a living area with a beautiful view.";
                    break;
            }

            roomList.add(room);
        }

        return roomList;
    }

    // Find a room using its room number
    Room room(int number) {

        for (Room room : rooms) {
            if (room.number == number) {
                return room;
            }
        }

        return null;
    }

    // Create the original sample bookings
    void seedBookings() {

        Object[][] samples = {
            {"Nina Reyes", 103, "Credit/Debit Card", Status.RESERVED},
            {"Elliot Brooks", 203, "Credit/Debit Card", Status.RESERVED},
            {"Amara Cruz", 303, "Credit/Debit Card", Status.RESERVED},
            {"Luca Romano", 403, "Credit/Debit Card", Status.RESERVED},

            {"Leila Haddad", 104, "Cash", Status.OCCUPIED},
            {"Theo Bennett", 204, "Cash", Status.OCCUPIED},
            {"Isabel Flores", 304, "Cash", Status.OCCUPIED},
            {"Samir Desai", 404, "Cash", Status.OCCUPIED}
        };

        for (Object[] item : samples) {

            Room room = room((Integer) item[1]);

            Status roomStatus = (Status) item[3];
            room.status = roomStatus;

            Booking booking = new Booking();

            booking.guest = (String) item[0];
            booking.room = room;
            booking.method = (String) item[2];

            booking.ref = "SE-" + (10000 + room.number);
            booking.stay = "Sample stay";
            booking.nights = 2;
            booking.guests = 1;

            booking.subtotal = room.price * 2;
            booking.tax = booking.subtotal * Model.TAX;
            booking.total = booking.subtotal + booking.tax;

            // Set the correct booking and payment status
            booking.paymentStatus = PaymentStatus.PAID;

            if (roomStatus == Status.RESERVED) {
                booking.bookingStatus = BookingStatus.CONFIRMED;
            } else if (roomStatus == Status.OCCUPIED) {
                booking.bookingStatus = BookingStatus.CHECKED_IN;
            }

            bookings.add(booking);
        }
    }

    Hotel() {
        seedBookings();
    }
}