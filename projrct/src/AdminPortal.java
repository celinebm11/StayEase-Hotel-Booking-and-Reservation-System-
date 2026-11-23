import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.io.File;
import java.util.List;

/** StayEase - ADMIN PORTAL: room availability, guest bookings, room inventory. */
public class AdminPortal extends Portal {

    final MainMenu app;
    final List<Room> rooms;
    final List<Booking> bookings;
    final ScrollPanel adminPage = new ScrollPanel(new BorderLayout());

    public AdminPortal(MainMenu app) {
        this.app = app;
        this.rooms = app.hotel.rooms;
        this.bookings = app.hotel.bookings;
        setLayout(new BorderLayout());
        add(wrap(adminPage), BorderLayout.CENTER);
        rebuildAdmin();
    }

    void refreshAll() {
        app.refreshAll();
    }

    /** Called by MainMenu when data changes. */
    public void refresh() {
        rebuildAdmin();
    }

    void rebuildAdmin() {
        adminPage.removeAll();
        adminPage.setBackground(BG);

        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBorder(new EmptyBorder(22, 34, 34, 34));

        col.add(left(lbl("Admin portal  \u203A  Overview",
                sans(Font.PLAIN, 13), MUTED)));
        col.add(Box.createVerticalStrut(28));
        col.add(left(lbl("Admin Portal", serif(42), INK)));
        col.add(Box.createVerticalStrut(8));
        col.add(left(lbl(
                "Monitor room availability, payments, and guest reservations.",
                sans(Font.PLAIN, 15), MUTED)));
        col.add(Box.createVerticalStrut(22));

        // Dashboard statistics
        JPanel stats = new JPanel(new GridLayout(1, 3, 16, 0));
        stats.setOpaque(false);
        stats.add(stat("Available rooms", count(Status.AVAILABLE)));
        stats.add(stat("Reserved rooms", count(Status.RESERVED)));
        stats.add(stat("Occupied rooms", count(Status.OCCUPIED)));
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        col.add(left(stats));
        col.add(Box.createVerticalStrut(22));

        // Guest bookings
        RoundPanel bk = new RoundPanel(
                new BorderLayout(), Color.WHITE, 22, LINE);
        bk.setBorder(new EmptyBorder(20, 24, 12, 24));

        JPanel bl = new JPanel();
        bl.setOpaque(false);
        bl.setLayout(new BoxLayout(bl, BoxLayout.Y_AXIS));

        bl.add(left(lbl("Guest bookings",
                sans(Font.BOLD, 20), INK)));
        bl.add(left(lbl(
                "Review payments and manage guest check-in and check-out.",
                sans(Font.PLAIN, 14), MUTED)));
        bl.add(Box.createVerticalStrut(10));

        if (bookings.isEmpty()) {
            bl.add(left(lbl("No bookings yet.",
                    sans(Font.PLAIN, 14), MUTED)));
        }

        for (Booking b : bookings) {
            bl.add(bookingRow(b));
        }

        bk.add(bl);
        col.add(left(bk));
        col.add(Box.createVerticalStrut(22));

        // Room inventory
        RoundPanel inv = new RoundPanel(
                new BorderLayout(), Color.WHITE, 22, LINE);
        inv.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel il = new JPanel();
        il.setOpaque(false);
        il.setLayout(new BoxLayout(il, BoxLayout.Y_AXIS));

        il.add(left(lbl("Room inventory",
                sans(Font.BOLD, 20), INK)));
        il.add(left(lbl(
                "All " + rooms.size()
                + " rooms across four categories. Use the dropdown to change a room's status.",
                sans(Font.PLAIN, 14), MUTED)));
        il.add(Box.createVerticalStrut(12));

        JPanel grid = new JPanel(new GridLayout(0, 2, 30, 0));
        grid.setOpaque(false);

        for (Room r : rooms) {
            JPanel row = new JPanel(new BorderLayout(10, 0));
            row.setOpaque(false);
            row.setBorder(new CompoundBorder(
                    new MatteBorder(1, 0, 0, 0, LINE),
                    new EmptyBorder(8, 0, 8, 0)));

            row.add(lbl(r.number + "   " + r.name,
                    sans(Font.PLAIN, 15), INK),
                    BorderLayout.CENTER);

            JComboBox<Status> cb =
                    new JComboBox<>(Status.values());

            cb.setSelectedItem(r.status);

            cb.setRenderer(new DefaultListCellRenderer() {
                @Override
                public Component getListCellRendererComponent(
                        JList<?> l, Object v, int i,
                        boolean s, boolean f) {

                    JLabel c = (JLabel)
                            super.getListCellRendererComponent(
                                    l, v, i, s, f);

                    c.setText(((Status) v).label);
                    return c;
                }
            });

            cb.addActionListener(e -> {
                r.status = (Status) cb.getSelectedItem();
                SwingUtilities.invokeLater(this::refreshAll);
            });

            row.add(cb, BorderLayout.EAST);
            grid.add(row);
        }

        il.add(left(grid));
        inv.add(il);
        col.add(left(inv));

        adminPage.add(col, BorderLayout.NORTH);
        adminPage.revalidate();
        adminPage.repaint();
    }

    // Individual booking information and actions
    JPanel bookingRow(Booking b) {
        JPanel outer = new JPanel();
        outer.setOpaque(false);
        outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));
        outer.setBorder(new CompoundBorder(
                new MatteBorder(1, 0, 0, 0, LINE),
                new EmptyBorder(14, 0, 14, 0)));

        JPanel info = new JPanel(new GridLayout(1, 3, 10, 0));
        info.setOpaque(false);

        JPanel c1 = vbox(
                lbl(b.guest, sans(Font.BOLD, 15), INK),
                lbl(b.ref, sans(Font.PLAIN, 13), MUTED));

        JPanel c2 = vbox(
                lbl(b.room.name + " \u00B7 Room " + b.room.number,
                        sans(Font.PLAIN, 14), INK),
                lbl(b.stay, sans(Font.PLAIN, 13), MUTED));

        JPanel c3 = vbox(
                lbl(money(b.total), sans(Font.BOLD, 15), INK),
                lbl(b.method, sans(Font.PLAIN, 13), MUTED));

        for (Component k : c3.getComponents()) {
            ((JLabel) k).setAlignmentX(Component.RIGHT_ALIGNMENT);
        }

        info.add(c1);
        info.add(c2);
        info.add(c3);

        outer.add(left(info));
        outer.add(Box.createVerticalStrut(8));

        outer.add(left(lbl(
                "Booking status: " + b.bookingStatus,
                sans(Font.BOLD, 13), BLUE)));

        outer.add(left(lbl(
                "Payment status: " + b.paymentStatus,
                sans(Font.PLAIN, 13), MUTED)));

        outer.add(left(lbl(
                "Refund status: " + b.refundStatus,
                sans(Font.PLAIN, 13), MUTED)));

        outer.add(left(lbl(
                "Refund amount: Php " + String.format("%.2f", b.refundAmount),
                sans(Font.PLAIN, 13), MUTED)));

        // GCash information
        if ("GCash".equalsIgnoreCase(b.method)) {
            outer.add(Box.createVerticalStrut(8));

            outer.add(left(lbl(
                    "GCash account: " + safe(b.gcashName),
                    sans(Font.PLAIN, 13), INK)));

            outer.add(left(lbl(
                    "Mobile number: " + safe(b.gcashNumber),
                    sans(Font.PLAIN, 13), INK)));

            outer.add(left(lbl(
                    "Transaction reference: " + safe(b.gcashReference),
                    sans(Font.PLAIN, 13), INK)));

            RoundButton viewScreenshot = new RoundButton(
                    "View GCash screenshot",
                    Color.WHITE, BLUE, LINE);

            viewScreenshot.setEnabled(
                    b.gcashScreenshotPath != null
                    && !b.gcashScreenshotPath.trim().isEmpty());

            viewScreenshot.addActionListener(e ->
                    openScreenshot(b.gcashScreenshotPath));

            outer.add(left(viewScreenshot));
        }

        outer.add(Box.createVerticalStrut(8));

        JPanel actions = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 8, 0));
        actions.setOpaque(false);

        // Approve or reject pending GCash payments
        if (b.paymentStatus == PaymentStatus.PENDING_VERIFICATION) {

            RoundButton approve = new RoundButton(
                    "Approve payment",
                    new Color(0x166534), Color.WHITE, null);

            approve.addActionListener(e -> {
                int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Approve this GCash payment?",
                        "Confirm approval",
                        JOptionPane.YES_NO_OPTION);

                if (answer == JOptionPane.YES_OPTION) {
                    app.service.approveGcashPayment(b);
                    refreshAll();
                }
            });

            RoundButton reject = new RoundButton(
                    "Reject payment",
                    new Color(0xB91C1C), Color.WHITE, null);

            reject.addActionListener(e -> {
                int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Reject this GCash payment?",
                        "Confirm rejection",
                        JOptionPane.YES_NO_OPTION);

                if (answer == JOptionPane.YES_OPTION) {
                    app.service.rejectGcashPayment(b);
                    refreshAll();
                }
            });

            actions.add(approve);
            actions.add(reject);
        }

        // Check-in for confirmed bookings
        if (b.bookingStatus == BookingStatus.CONFIRMED) {
            RoundButton checkIn = new RoundButton(
                    "Confirm check-in",
                    BLUE, Color.WHITE, null);

            checkIn.addActionListener(e -> {
                int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Confirm check-in for " + b.guest + "?",
                        "Confirm check-in",
                        JOptionPane.YES_NO_OPTION);

                if (answer == JOptionPane.YES_OPTION) {
                    app.service.checkIn(b);
                    refreshAll();
                }
            });

            actions.add(checkIn);
        }

        // Check-out for checked-in bookings
        if (b.bookingStatus == BookingStatus.CHECKED_IN) {
            RoundButton checkOut = new RoundButton(
                    "Confirm check-out",
                    new Color(0x166534), Color.WHITE, null);

            checkOut.addActionListener(e -> {
                int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Confirm check-out for " + b.guest + "?",
                        "Confirm check-out",
                        JOptionPane.YES_NO_OPTION);

                if (answer == JOptionPane.YES_OPTION) {
                    app.service.checkOut(b);
                    refreshAll();
                }
            });

            actions.add(checkOut);
        }

        // Administrator cancellation for active bookings
        if (b.bookingStatus != BookingStatus.CHECKED_IN
                && b.bookingStatus != BookingStatus.COMPLETED
                && b.bookingStatus != BookingStatus.CANCELLED
                && b.bookingStatus != BookingStatus.REJECTED) {

            RoundButton cancel = new RoundButton(
                    "Cancel Booking",
                    new Color(0xB91C1C),
                    Color.WHITE,
                    null);

            cancel.addActionListener(e -> {

                int answer = JOptionPane.showConfirmDialog(
                        this,
                        "Are you sure you want to cancel the booking of "
                                + b.guest + "?\n\n"
                                + "Room " + b.room.number
                                + " will become Available.",
                        "Cancel Guest Booking",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (answer == JOptionPane.YES_OPTION) {

                    boolean cancelled =
                            app.service.adminCancelBooking(b);

                    if (cancelled) {

                        String refundMessage;

                        if (b.refundStatus == RefundStatus.PROCESSED) {

                            refundMessage = "The customer is eligible for a full refund.\n"
                                    + "Refund amount: Php "
                                    + String.format("%.2f", b.refundAmount)
                                    + "\n\nThe refund has been recorded.";

                        } else if (b.refundStatus == RefundStatus.NOT_ELIGIBLE) {

                            refundMessage = "No refund is applicable.\n"
                                    + "The cancellation was made less than 3 days "
                                    + "before check-in.";

                        } else {

                            refundMessage = "No refund is applicable because "
                                    + "the payment has not been completed or verified.";
                        }

                        JOptionPane.showMessageDialog(
                                this,
                                "Booking successfully cancelled!\n\n" + refundMessage,
                                "Cancellation Successful",
                                JOptionPane.INFORMATION_MESSAGE);

                        refreshAll();
                    } else {
                        JOptionPane.showMessageDialog(
                                this,
                                "Unable to cancel this booking.",
                                "Cancellation Failed",
                                JOptionPane.WARNING_MESSAGE);
                    }
                }
            });

            actions.add(cancel);
        }

        if (actions.getComponentCount() > 0) {
            outer.add(left(actions));
        }

        outer.setMaximumSize(
                new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        return outer;
    }

    String safe(String value) {
        return value == null || value.trim().isEmpty()
                ? "Not provided" : value;
    }

    void openScreenshot(String path) {
        if (path == null || path.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No screenshot was uploaded.",
                    "Screenshot unavailable",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            File file = new File(path);

            if (!file.exists()) {
                JOptionPane.showMessageDialog(
                        this,
                        "The screenshot file could not be found.",
                        "File not found",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Your computer does not support opening files this way.",
                        "Unable to open screenshot",
                        JOptionPane.WARNING_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to open screenshot: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    JPanel vbox(JLabel a, JLabel b) {
        JPanel p = new JPanel(new GridLayout(2, 1, 0, 3));
        p.setOpaque(false);
        p.add(a);
        p.add(b);
        return p;
    }

    int count(Status s) {
        int n = 0;
        for (Room r : rooms) {
            if (r.status == s) n++;
        }
        return n;
    }

    JComponent stat(String title, int value) {
        RoundPanel p = new RoundPanel(
                new BorderLayout(), Color.WHITE, 20, LINE);

        p.setBorder(new EmptyBorder(16, 22, 16, 22));
        p.add(lbl(title, sans(Font.PLAIN, 14), MUTED),
                BorderLayout.NORTH);
        p.add(lbl(String.valueOf(value),
                sans(Font.BOLD, 30), INK),
                BorderLayout.CENTER);

        return p;
    }
}
