import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Service {

    // Find a room using its room number
    public Room findRoom(Hotel hotel, int roomNumber) {
        return hotel.room(roomNumber);
    }

    // Get all available rooms
    public List<Room> getAvailableRooms(Hotel hotel) {
        List<Room> availableRooms = new ArrayList<>();

        for (Room room : hotel.rooms) {
            if (room.status == Status.AVAILABLE) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }

    // Search available rooms by room type
    public List<Room> searchRooms(Hotel hotel, String roomType) {
        List<Room> results = new ArrayList<>();

        for (Room room : hotel.rooms) {
            boolean available = room.status == Status.AVAILABLE;
            boolean matchesType = roomType.equalsIgnoreCase("All rooms")
                    || room.type.equalsIgnoreCase(roomType);

            if (available && matchesType) {
                results.add(room);
            }
        }

        return results;
    }

    // Count rooms based on their status
    public int countRooms(Hotel hotel, Status status) {
        int count = 0;

        for (Room room : hotel.rooms) {
            if (room.status == status) {
                count++;
            }
        }

        return count;
    }

    // Calculate booking subtotal
    public double calculateSubtotal(double roomPrice, int nights) {
        return roomPrice * nights;
    }

    // Calculate the discount amount
    public double calculateDiscount(double subtotal, String discountType) {

        if (discountType == null || discountType.equalsIgnoreCase("None")) {
            return 0;
        }

        if (discountType.equalsIgnoreCase("Student")
                || discountType.equalsIgnoreCase("PWD")
                || discountType.equalsIgnoreCase("Senior Citizen")) {

            return subtotal * 0.15;
        }

        return 0;
    }

    // Calculate the discounted subtotal
    public double calculateDiscountedSubtotal(double subtotal, double discount) {
        return subtotal - discount;
    }

    // Calculate 12% tax after discount
    public double calculateTax(double discountedSubtotal) {
        return discountedSubtotal * Model.TAX;
    }

    // Calculate final booking total
    public double calculateTotal(double subtotal) {
        return subtotal + calculateTax(subtotal);
    }

    // Calculate and save all booking amounts
    public void calculateBookingAmount(Booking booking) {

        if (booking == null) {
            return;
        }

        booking.discountAmount =
                calculateDiscount(booking.subtotal, booking.discountType);

        booking.discountRate = booking.discountAmount > 0 ? 0.15 : 0;

        double discountedSubtotal =
                calculateDiscountedSubtotal(
                        booking.subtotal,
                        booking.discountAmount
                );

        booking.tax = calculateTax(discountedSubtotal);
        booking.total = discountedSubtotal + booking.tax;
    }

    // Check if a booking can be cancelled
    public boolean canCancelBooking(Booking booking) {

        if (booking == null || booking.checkInDate == null) {
            return false;
        }

        if (booking.bookingStatus == BookingStatus.CANCELLED
                || booking.bookingStatus == BookingStatus.CHECKED_IN
                || booking.bookingStatus == BookingStatus.COMPLETED
                || booking.bookingStatus == BookingStatus.REJECTED) {
            return false;
        }

        long daysBeforeCheckIn = ChronoUnit.DAYS.between(
                LocalDate.now(),
                booking.checkInDate
        );

        return daysBeforeCheckIn >= 3;
    }

    // Cancel a booking
    public boolean cancelBooking(Booking booking) {

        if (!canCancelBooking(booking)) {
            return false;
        }

        booking.bookingStatus = BookingStatus.CANCELLED;

        if (booking.room != null) {
            booking.room.status = Status.AVAILABLE;
        }

        return true;
    }

    // Save GCash payment information
    public void submitGcashPayment(
            Booking booking,
            String name,
            String number,
            String reference,
            String screenshotPath) {

        if (booking == null) {
            return;
        }

        booking.gcashName = name;
        booking.gcashNumber = number;
        booking.gcashReference = reference;
        booking.gcashScreenshotPath = screenshotPath;

        booking.method = "GCash";
        booking.paymentStatus = PaymentStatus.PENDING_VERIFICATION;
        booking.bookingStatus = BookingStatus.PENDING_VERIFICATION;
    }

    // Approve GCash payment
    public boolean approveGcashPayment(Booking booking) {

        if (booking == null
                || booking.paymentStatus != PaymentStatus.PENDING_VERIFICATION) {
            return false;
        }

        booking.paymentStatus = PaymentStatus.APPROVED;
        booking.bookingStatus = BookingStatus.CONFIRMED;

        if (booking.room != null) {
            booking.room.status = Status.RESERVED;
        }

        return true;
    }

    // Reject GCash payment
    public boolean rejectGcashPayment(Booking booking) {

        if (booking == null
                || booking.paymentStatus != PaymentStatus.PENDING_VERIFICATION) {
            return false;
        }

        booking.paymentStatus = PaymentStatus.REJECTED;
        booking.bookingStatus = BookingStatus.REJECTED;

        if (booking.room != null) {
            booking.room.status = Status.AVAILABLE;
        }

        return true;
    }

    // Administrator confirms guest check-in
    public boolean checkIn(Booking booking) {

        if (booking == null
                || booking.bookingStatus != BookingStatus.CONFIRMED
                || booking.room == null) {
            return false;
        }

        booking.bookingStatus = BookingStatus.CHECKED_IN;
        booking.room.status = Status.OCCUPIED;

        return true;
    }

    // Administrator confirms guest check-out
    public boolean checkOut(Booking booking) {

        if (booking == null
                || booking.bookingStatus != BookingStatus.CHECKED_IN
                || booking.room == null) {
            return false;
        }

        booking.bookingStatus = BookingStatus.COMPLETED;
        booking.room.status = Status.AVAILABLE;

        return true;
    }

    // Administrator cancels a guest booking
    public boolean adminCancelBooking(Booking booking) {

        if (booking == null || booking.room == null) {
            return false;
        }

        // Only bookings that have not checked in can be cancelled
        if (booking.bookingStatus == BookingStatus.CHECKED_IN
                || booking.bookingStatus == BookingStatus.COMPLETED
                || booking.bookingStatus == BookingStatus.CANCELLED
                || booking.bookingStatus == BookingStatus.REJECTED) {
            return false;
        }

        // Check if the customer is eligible for a refund
        if ((booking.paymentStatus == PaymentStatus.PAID
                || booking.paymentStatus == PaymentStatus.APPROVED)
                && booking.checkInDate != null) {

            long daysBeforeCheckIn = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    booking.checkInDate
            );

            if (daysBeforeCheckIn >= 3) {
                booking.refundAmount = booking.total;
                booking.refundStatus = RefundStatus.PROCESSED;
            } else {
                booking.refundAmount = 0.0;
                booking.refundStatus = RefundStatus.NOT_ELIGIBLE;
            }

        } else {
            booking.refundAmount = 0.0;
            booking.refundStatus = RefundStatus.NOT_APPLICABLE;
        }

        // Cancel the booking
        booking.bookingStatus = BookingStatus.CANCELLED;

        // Release the room
        booking.room.status = Status.AVAILABLE;

        // Reject pending GCash verification
        if (booking.paymentStatus == PaymentStatus.PENDING_VERIFICATION) {
            booking.paymentStatus = PaymentStatus.REJECTED;
        }

        return true;
    }

    // Update a room's status
    public void updateRoomStatus(Room room, Status newStatus) {

        if (room != null && newStatus != null) {
            room.status = newStatus;
        }
    }

    // Add a booking to the hotel's booking list
    public void addBooking(Hotel hotel, Booking booking) {

        if (hotel != null && booking != null) {
            hotel.bookings.add(booking);
        }
    }
}