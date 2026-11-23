import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/** StayEase - GUEST PORTAL: explore rooms and the 4-step booking flow. */
public class GuestPortal extends Portal {

    final MainMenu app;
    final List<Room> rooms;
    final List<Booking> bookings;
    final CardLayout cards = new CardLayout();
    final JPanel content = this;
    final ScrollPanel guestPage = new ScrollPanel(new BorderLayout());
    final JPanel roomGrid = new JPanel(new GridLayout(0, 2, 18, 18));
    final JLabel countLabel = lbl("", sans(Font.PLAIN, 14), MUTED);
    final JPanel bookingHolder = new JPanel(new BorderLayout());
    String filter = "All rooms";

    public GuestPortal(MainMenu app) {
        this.app = app;
        this.rooms = app.hotel.rooms;
        this.bookings = app.hotel.bookings;
        setLayout(cards);
        buildGuestPage();
        add(wrap(guestPage), "explore");
        add(bookingHolder, "booking");
        rebuildGrid();
    }

    Room room(int n) { return app.hotel.room(n); }

    void refreshAll() { app.refreshAll(); }

    /** Called by MainMenu when data changes. */
    public void refresh() { rebuildGrid(); }

    public void showExplore() { cards.show(content, "explore"); }

    // ----- guest page -----
    void buildGuestPage() {
        guestPage.setBackground(BG);
        JPanel col = new JPanel();
        col.setOpaque(false);
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setBorder(new EmptyBorder(22, 34, 34, 34));

        col.add(left(lbl("Guest portal  \u203A  Explore rooms", sans(Font.PLAIN, 13), MUTED)));
        col.add(Box.createVerticalStrut(28));
        col.add(left(lbl("Inheriting Comfort, Encapsulating Luxury", serif(42), INK)));
        col.add(Box.createVerticalStrut(8));
        col.add(left(lbl("Explore our rooms, find your perfect stay, and reserve your next stay at StayEase.", sans(Font.PLAIN, 15), MUTED)));
        col.add(Box.createVerticalStrut(22));

        Room feat = room(301);
        RoundPanel hero = new RoundPanel(new BorderLayout(), NAVY_2, 26, null);
        hero.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
        hero.setPreferredSize(new Dimension(800, 290));
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.setBorder(new EmptyBorder(30, 32, 30, 20));
        text.add(left(lbl("\u2022 Featured room", sans(Font.BOLD, 12), new Color(0xBFD4F5))));
        text.add(Box.createVerticalStrut(10));
        text.add(left(lbl(feat.name, serif(38), Color.WHITE)));
        text.add(Box.createVerticalStrut(8));
        text.add(left(lbl("<html><body style='width:300px'>" + feat.desc + "</body></html>", sans(Font.PLAIN, 14), Color.WHITE)));
        text.add(Box.createVerticalStrut(10));
        text.add(left(lbl(feat.bed + "   \u00B7   " + feat.capacity + " guests   \u00B7   Wi-Fi   \u00B7   " + money(feat.price) + " / night", sans(Font.PLAIN, 13), new Color(0xD6E2F7))));
        text.add(Box.createVerticalStrut(16));
        RoundButton reserve = new RoundButton("Reserve " + feat.name + "  \u2192", new Color(0x1677E6), Color.WHITE, null);
        reserve.addActionListener(e -> openBooking(feat));
        text.add(left(reserve));
        hero.add(text, BorderLayout.WEST);
        hero.add(new RoomImage(feat, false), BorderLayout.CENTER);
        col.add(left(hero));
        col.add(Box.createVerticalStrut(30));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JPanel hl = new JPanel();
        hl.setOpaque(false);
        hl.setLayout(new BoxLayout(hl, BoxLayout.Y_AXIS));
        hl.add(left(lbl("Explore rooms", serif(30), INK)));
        hl.add(left(lbl("From a quiet single to a spacious suite.", sans(Font.PLAIN, 14), MUTED)));
        head.add(hl, BorderLayout.WEST);
        head.add(countLabel, BorderLayout.EAST);
        head.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
        col.add(left(head));
        col.add(Box.createVerticalStrut(14));

        JPanel chips = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        chips.setOpaque(false);
        for (String f : new String[]{"All rooms", "Single", "Double", "Deluxe", "Suite"}) {
            RoundButton chip = new RoundButton(f, f.equals(filter) ? BLUE : Color.WHITE, f.equals(filter) ? Color.WHITE : MUTED, LINE);
            chip.addActionListener(e -> {
                filter = f;
                for (Component c : chips.getComponents()) {
                    RoundButton rb = (RoundButton) c;
                    boolean on = rb.getText().equals(f);
                    rb.bg = on ? BLUE : Color.WHITE;
                    rb.setForeground(on ? Color.WHITE : MUTED);
                    rb.repaint();
                }
                rebuildGrid();
            });
            chips.add(chip);
        }
        chips.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        col.add(left(chips));
        col.add(Box.createVerticalStrut(16));

        roomGrid.setOpaque(false);
        col.add(left(roomGrid));
        guestPage.add(col, BorderLayout.NORTH);
    }

    void rebuildGrid() {
        roomGrid.removeAll();
        int n = 0;
        for (Room r : rooms) {
            if (!filter.equals("All rooms") && !r.type.equals(filter)) continue;
            roomGrid.add(roomCard(r));
            n++;
        }
        countLabel.setText(n + " rooms");
        roomGrid.revalidate();
        roomGrid.repaint();
    }

    JComponent roomCard(Room r) {
        RoundPanel card = new RoundPanel(new BorderLayout(), Color.WHITE, 22, LINE);
        RoomImage img = new RoomImage(r, true);
        img.setPreferredSize(new Dimension(300, 200));
        card.add(img, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(14, 18, 18, 18));

        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.add(lbl(r.type + " \u00B7 Room " + r.number, sans(Font.PLAIN, 13), MUTED), BorderLayout.WEST);
        row.add(lbl(money0(r.price) + "  / night", sans(Font.BOLD, 14), BLUE), BorderLayout.EAST);
        body.add(left(row));
        body.add(Box.createVerticalStrut(4));
        body.add(left(lbl(r.name, sans(Font.BOLD, 19), INK)));
        body.add(Box.createVerticalStrut(8));
        body.add(left(lbl("<html><body style='width:240px'>" + r.desc + "</body></html>", sans(Font.PLAIN, 14), MUTED)));
        body.add(Box.createVerticalStrut(12));
        body.add(left(lbl(r.bed + "    \u263A " + r.capacity, sans(Font.PLAIN, 13), MUTED)));
        body.add(Box.createVerticalStrut(12));

        boolean free = r.status == Status.AVAILABLE;
        RoundButton btn = new RoundButton(free ? "Book room  \u2192" : "Not available  \u2192", Color.WHITE,
                free ? new Color(0x1D4ED8) : new Color(0x94A3B8), LINE);
        btn.setEnabled(free);
        btn.addActionListener(e -> openBooking(r));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        body.add(left(btn));
        card.add(body, BorderLayout.CENTER);
        return card;
    }

    static String money0(double v) {
        return String.format("Php %,.0f", v);
    }

    // ----- booking -----
    void openBooking(Room r) {
        if (r.status != Status.AVAILABLE) return;
        bookingHolder.removeAll();
        bookingHolder.add(wrap(new BookingView(r)), BorderLayout.CENTER);
        bookingHolder.revalidate();
        bookingHolder.repaint();
        cards.show(content, "booking");
    }

    class BookingView extends ScrollPanel {
        final Room room;
        final Field name = new Field("Full name"), email = new Field("Email address"), phone = new Field("Contact number"),
                checkIn = new Field("mm/dd/yyyy"), checkOut = new Field("mm/dd/yyyy"),
                cardName = new Field("Name on card"), cardNo = new Field("Card number (demo)"),
                exp = new Field("MM/YY"), cvv = new Field("CVV");
        final JComboBox<Integer> guests = new JComboBox<>();
        final JComboBox<String> method = new JComboBox<>(
            new String[]{"Credit/Debit Card", "Cash", "GCash"}
        );

        final JComboBox<String> discountType = new JComboBox<>(
            new String[]{"None", "Student", "PWD", "Senior Citizen"}
        );

        String discountIdImagePath = "";
        
        final JLabel discountFileLabel = lbl(
            "No ID photo selected",
            sans(Font.PLAIN, 13),
            MUTED
        );

        final Field discountIdNumber = new Field("Enter ID number");
        final Field gcashName = new Field("GCash account name");
        final Field gcashNumber = new Field("GCash mobile number");
        final Field gcashReference = new Field("Transaction reference number");

        final JPanel gcashFields = new JPanel();
        String gcashScreenshotPath = "";
        final JLabel gcashFileLabel = lbl(
            "No screenshot selected",
            sans(Font.PLAIN, 13),
            MUTED
        );
        final CardLayout stepLayout = new CardLayout();
        final JPanel stepCards = new JPanel(stepLayout);
        final JLabel[] stepLbl = new JLabel[4];
        final JPanel review = new JPanel(), receipt = new JPanel(), cardFields = new JPanel(new GridLayout(2, 2, 10, 10));
        final JLabel sIn = lbl("Not selected", sans(Font.PLAIN, 14), INK), sOut = lbl("Not selected", sans(Font.PLAIN, 14), INK),
                sNights = lbl("0", sans(Font.PLAIN, 14), INK), sGuests = lbl("1", sans(Font.PLAIN, 14), INK),
                sSub = lbl("Php 0.00", sans(Font.PLAIN, 14), INK),
                sDiscount = lbl("Php 0.00", sans(Font.PLAIN, 14), INK),
                sTax = lbl("Php 0.00", sans(Font.PLAIN, 14), INK),
                sTotal = lbl("Php0.00", sans(Font.BOLD, 20), INK);

        BookingView(Room room) {
            super(new BorderLayout(24, 0));
            this.room = room;
            setBackground(BG);
            setBorder(new EmptyBorder(22, 34, 34, 34));
            for (int i = 1; i <= room.capacity; i++) guests.addItem(i);
            guests.setFont(sans(Font.PLAIN, 14));
            method.setFont(sans(Font.PLAIN, 14));

            JPanel main = new JPanel();
            main.setOpaque(false);
            main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));

            JButton back = new JButton("\u2190 Back to rooms");
            back.setBorderPainted(false); back.setContentAreaFilled(false); back.setFocusPainted(false);
            back.setForeground(BLUE); back.setFont(sans(Font.PLAIN, 14));
            back.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            back.addActionListener(e -> cards.show(content, "explore"));
            main.add(left(back));
            main.add(Box.createVerticalStrut(12));

            JPanel steps = new JPanel(new FlowLayout(FlowLayout.LEFT, 22, 0));
            steps.setOpaque(false);
            String[] sn = {"1  Guest details", "2  Review", "3  Payment", "4  Receipt"};
            for (int i = 0; i < 4; i++) { stepLbl[i] = lbl(sn[i], sans(Font.PLAIN, 14), MUTED); steps.add(stepLbl[i]); }
            steps.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            main.add(left(steps));
            main.add(Box.createVerticalStrut(18));
            main.add(left(lbl("Book your stay", serif(40), INK)));
            main.add(Box.createVerticalStrut(6));
            main.add(left(lbl("Demo checkout. Use sample information; no room will be booked or charged.", sans(Font.PLAIN, 14), MUTED)));
            main.add(Box.createVerticalStrut(18));

            stepCards.setOpaque(false);
            stepCards.add(detailsStep(), "0");
            review.setOpaque(false); review.setLayout(new BoxLayout(review, BoxLayout.Y_AXIS));
            stepCards.add(cardOf(review), "1");
            stepCards.add(paymentStep(), "2");
            receipt.setOpaque(false); receipt.setLayout(new BoxLayout(receipt, BoxLayout.Y_AXIS));
            stepCards.add(cardOf(receipt), "3");
            main.add(left(stepCards));

            add(main, BorderLayout.CENTER);
            add(summaryPanel(), BorderLayout.EAST);

            DocumentListener dl = new DocumentListener() {
                public void insertUpdate(DocumentEvent e) { updateSummary(); }
                public void removeUpdate(DocumentEvent e) { updateSummary(); }
                public void changedUpdate(DocumentEvent e) { updateSummary(); }
            };
            discountType.addActionListener(e -> updateSummary());
            checkIn.getDocument().addDocumentListener(dl);
            checkOut.getDocument().addDocumentListener(dl);
            guests.addActionListener(e -> updateSummary());
            goStep(0);
        }

        JPanel cardOf(JPanel inner) {
            RoundPanel c = new RoundPanel(new BorderLayout(), Color.WHITE, 22, LINE);
            c.setBorder(new EmptyBorder(24, 26, 24, 26));
            c.add(inner, BorderLayout.CENTER);
            return c;
        }

        JPanel labeled(String text, JComponent comp) {
            JPanel p = new JPanel(new BorderLayout(0, 6));
            p.setOpaque(false);
            p.add(lbl(text, sans(Font.BOLD, 13), INK), BorderLayout.NORTH);
            p.add(comp, BorderLayout.CENTER);
            return p;
        }

        JPanel detailsStep() {
            JPanel form = new JPanel(new GridBagLayout());
            form.setOpaque(false);
            GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL; c.weightx = 1; c.insets = new Insets(8, 6, 8, 6);

            JPanel head = new JPanel(new BorderLayout());
            head.setOpaque(false);
            head.add(lbl("Guest information", sans(Font.BOLD, 20), INK), BorderLayout.WEST);
            RoundButton sample = new RoundButton("Use sample details", Color.WHITE, BLUE, LINE);
            sample.addActionListener(e -> {
                name.setText("Juan Dela Cruz"); email.setText("juan@example.com"); phone.setText("0917 123 4567");
                checkIn.setText(LocalDate.now().plusDays(1).format(FMT));
                checkOut.setText(LocalDate.now().plusDays(3).format(FMT));
            });
            head.add(sample, BorderLayout.EAST);
            c.gridx = 0; c.gridy = 0; c.gridwidth = 2; form.add(head, c);
            c.gridy = 1; form.add(labeled("Full name", name), c);
            c.gridwidth = 1;
            c.gridy = 2; c.gridx = 0; form.add(labeled("Email address", email), c);
            c.gridx = 1; form.add(labeled("Contact number", phone), c);
            c.gridy = 3; c.gridx = 0; form.add(labeled("Check-in date", checkIn), c);
            c.gridx = 1; form.add(labeled("Check-out date", checkOut), c);
            c.gridy = 4;
            c.gridx = 0;
            form.add(labeled("Number of guests", guests), c);

            // Discount selection
            discountType.setFont(sans(Font.PLAIN, 14));

            c.gridy = 5;
            c.gridx = 0;
            form.add(labeled("Discount type", discountType), c);

            c.gridy = 6;
            c.gridx = 0;
            form.add(labeled("ID number", discountIdNumber), c);

            // Upload ID proof
            RoundButton uploadID = new RoundButton(
                "Upload ID photo",
                Color.WHITE,
                BLUE,
                LINE
            );

            uploadID.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Select ID photo");

                int result = chooser.showOpenDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    discountIdImagePath =
                        chooser.getSelectedFile().getAbsolutePath();

                    discountFileLabel.setText(
                        chooser.getSelectedFile().getName()
                    );
                }
            });

            JPanel idPanel = new JPanel();
            idPanel.setOpaque(false);
            idPanel.setLayout(new BoxLayout(idPanel, BoxLayout.Y_AXIS));
            idPanel.add(uploadID);
            idPanel.add(Box.createVerticalStrut(5));
            idPanel.add(discountFileLabel);

            c.gridy = 7;
            c.gridx = 0;
            form.add(labeled("ID proof Front & Back(required for discounts)", idPanel), c);

            RoundButton next = new RoundButton("Review booking  \u2192", BLUE, Color.WHITE, null);
            next.addActionListener(e -> {
                String err = validateDetails();
                if (err != null) warn(err); else { fillReview(); goStep(1); }
            });
            c.gridy = 8;
            c.gridx = 0;
            c.gridwidth = 1;
            c.fill = GridBagConstraints.NONE;
            c.anchor = GridBagConstraints.WEST;
            form.add(next, c);
            return cardOf(form);
        }

        JPanel paymentStep() {
            JPanel p = new JPanel();
            p.setOpaque(false);
            p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

            p.add(left(lbl("Payment", sans(Font.BOLD, 20), INK)));
            p.add(Box.createVerticalStrut(6));
            p.add(left(lbl(
                "Demo only. Do not enter real card details.",
                sans(Font.PLAIN, 13),
                MUTED
            )));
            p.add(Box.createVerticalStrut(14));

            JPanel m = labeled("Payment method", method);
            m.setMaximumSize(new Dimension(420, 70));
            p.add(left(m));
            p.add(Box.createVerticalStrut(10));

            cardFields.setOpaque(false);
            cardFields.add(cardName);
            cardFields.add(cardNo);
            cardFields.add(exp);
            cardFields.add(cvv);
            cardFields.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
            p.add(left(cardFields));

            gcashFields.setOpaque(false);
            gcashFields.setLayout(new BoxLayout(gcashFields, BoxLayout.Y_AXIS));

            gcashFields.add(left(labeled("GCash account name", gcashName)));
            gcashFields.add(Box.createVerticalStrut(8));
            gcashFields.add(left(labeled("GCash mobile number", gcashNumber)));
            gcashFields.add(Box.createVerticalStrut(8));
            gcashFields.add(left(labeled("Transaction reference number", gcashReference)));
            gcashFields.add(Box.createVerticalStrut(10));

            RoundButton upload = new RoundButton(
                "Upload transaction screenshot",
                Color.WHITE,
                BLUE,
                LINE
            );

            upload.addActionListener(e -> {
                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("Select GCash transaction screenshot");

                int result = chooser.showOpenDialog(this);

                if (result == JFileChooser.APPROVE_OPTION) {
                    gcashScreenshotPath =
                        chooser.getSelectedFile().getAbsolutePath();

                    gcashFileLabel.setText(
                        chooser.getSelectedFile().getName()
                    );
                }
            });

            gcashFields.add(left(upload));
            gcashFields.add(Box.createVerticalStrut(5));
            gcashFields.add(left(gcashFileLabel));
            gcashFields.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

            p.add(left(gcashFields));

            method.addActionListener(e -> {
                int selected = method.getSelectedIndex();

                cardFields.setVisible(selected == 0);
                gcashFields.setVisible(selected == 2);

                revalidate();
                repaint();
            });

            gcashFields.setVisible(false);

            p.add(Box.createVerticalStrut(16));

            JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            btns.setOpaque(false);

            RoundButton back = new RoundButton(
                "\u2190 Back",
                Color.WHITE,
                INK,
                LINE
            );
            back.addActionListener(e -> goStep(1));

            RoundButton pay = new RoundButton(
                "Submit payment",
                BLUE,
                Color.WHITE,
                null
            );
            pay.addActionListener(e -> pay());

            btns.add(back);
            btns.add(pay);
            btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));

            p.add(left(btns));

            return cardOf(p);
        }

        JPanel summaryPanel() {
            RoundPanel s = new RoundPanel(new BorderLayout(), Color.WHITE, 22, LINE);
            s.setPreferredSize(new Dimension(340, 10));
            JPanel in = new JPanel();
            in.setOpaque(false);
            in.setLayout(new BoxLayout(in, BoxLayout.Y_AXIS));
            in.setBorder(new EmptyBorder(0, 0, 18, 0));
            RoomImage img = new RoomImage(room, false);
            img.setPreferredSize(new Dimension(340, 170));
            img.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
            in.add(left(img));
            JPanel body = new JPanel();
            body.setOpaque(false);
            body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
            body.setBorder(new EmptyBorder(14, 20, 0, 20));
            body.add(left(lbl(room.name, serif(26), INK)));
            body.add(left(lbl("Room " + room.number + " \u00B7 " + room.bed, sans(Font.PLAIN, 13), MUTED)));
            body.add(Box.createVerticalStrut(14));
            body.add(sumRow("Check-in", sIn)); body.add(sumRow("Check-out", sOut));
            body.add(sumRow("Nights", sNights)); body.add(sumRow("Guests", sGuests));
            body.add(Box.createVerticalStrut(10));
            body.add(sumRow("Price per night", lbl(money(room.price), sans(Font.PLAIN, 14), INK)));
            body.add(sumRow("Subtotal", sSub));
            body.add(sumRow("Discount", sDiscount));
            body.add(sumRow("Taxes and fees (12%)", sTax));
            body.add(Box.createVerticalStrut(10));
            body.add(sumRow("Total (PHP)", sTotal));
            in.add(left(body));
            s.add(in, BorderLayout.NORTH);
            return s;
        }

        JPanel sumRow(String k, JLabel v) {
            JPanel r = new JPanel(new BorderLayout());
            r.setOpaque(false);
            r.add(lbl(k, sans(Font.PLAIN, 14), MUTED), BorderLayout.WEST);
            r.add(v, BorderLayout.EAST);
            r.setBorder(new EmptyBorder(5, 0, 5, 0));
            r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
            r.setAlignmentX(Component.LEFT_ALIGNMENT);
            return r;
        }

        LocalDate parse(String s) {
            try { return LocalDate.parse(s.trim(), FMT); } catch (Exception e) { return null; }
        }

        long nights() {
            LocalDate a = parse(checkIn.getText()), b = parse(checkOut.getText());
            if (a == null || b == null || !b.isAfter(a)) return 0;
            return ChronoUnit.DAYS.between(a, b);
        }

        void updateSummary() {
            LocalDate a = parse(checkIn.getText());
            LocalDate b = parse(checkOut.getText());

            sIn.setText(a == null ? "Not selected" : a.format(FMT));
            sOut.setText(b == null ? "Not selected" : b.format(FMT));

            long n = nights();

            double subtotal = room.price * n;
            double rate = discountType.getSelectedIndex() == 0 ? 0 : 0.15;
            double discount = subtotal * rate;
            double discountedSubtotal = subtotal - discount;
            double tax = discountedSubtotal * 0.12;
            double total = discountedSubtotal + tax;

            sNights.setText(String.valueOf(n));
            sGuests.setText(String.valueOf(guests.getSelectedItem()));

            sSub.setText(money(subtotal));
            sDiscount.setText("-" + money(discount));
            sTax.setText(money(tax));
            sTotal.setText(money(total));
        }

        String validateDetails() {
            if (name.getText().trim().isEmpty()) return "Please enter your full name.";
            if (!email.getText().trim().matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) return "Please enter a valid email address.";
            if (phone.getText().trim().length() < 7) return "Please enter a valid contact number.";
            LocalDate a = parse(checkIn.getText()), b = parse(checkOut.getText());
            if (a == null) return "Check-in date must look like mm/dd/yyyy.";
            if (b == null) return "Check-out date must look like mm/dd/yyyy.";
            if (a.isBefore(LocalDate.now())) return "Check-in date cannot be in the past.";
            if (!b.isAfter(a)) return "Check-out must be after check-in.";
            if (!discountType.getSelectedItem().equals("None")
                    && discountIdImagePath.isEmpty()) {
                return "Please upload an ID photo to apply your discount.";
            }
            if (!discountType.getSelectedItem().equals("None")) {
                if (discountIdNumber.getText().trim().isEmpty()) {
                    return "Please enter your discount ID number.";
                }

                if (discountIdImagePath.isEmpty()) {
                    return "Please upload an ID photo to apply your discount.";
                }
            }
            return null;
        }

        void warn(String msg) {
            JOptionPane.showMessageDialog(this, msg, "Please check", JOptionPane.WARNING_MESSAGE);
        }

        void kv(JPanel p, String k, String v) {
            JPanel r = new JPanel(new BorderLayout());
            r.setOpaque(false);
            r.add(lbl(k, sans(Font.PLAIN, 14), MUTED), BorderLayout.WEST);
            r.add(lbl(v, sans(Font.BOLD, 14), INK), BorderLayout.EAST);
            r.setBorder(new EmptyBorder(7, 0, 7, 0));
            r.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            r.setAlignmentX(Component.LEFT_ALIGNMENT);
            p.add(r);
        }

        void fillReview() {
            review.removeAll();
            review.add(left(lbl("Review your booking", sans(Font.BOLD, 20), INK)));
            review.add(Box.createVerticalStrut(10));
            kv(review, "Guest", name.getText().trim());
            kv(review, "Email", email.getText().trim());
            kv(review, "Contact", phone.getText().trim());
            kv(review, "Check-in", checkIn.getText().trim());
            kv(review, "Check-out", checkOut.getText().trim());
            kv(review, "Nights", String.valueOf(nights()));
            kv(review, "Guests", String.valueOf(guests.getSelectedItem()));
            kv(review, "Discount type", (String) discountType.getSelectedItem());
            if (!discountType.getSelectedItem().equals("None")) {
                kv(review, "Discount ID number", discountIdNumber.getText().trim());
            }
            kv(review, "Discount amount", sDiscount.getText());
            kv(review, "Total (PHP)", sTotal.getText());
            review.add(Box.createVerticalStrut(14));
            JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            btns.setOpaque(false);
            RoundButton back = new RoundButton("\u2190 Edit details", Color.WHITE, INK, LINE);
            back.addActionListener(e -> goStep(0));
            RoundButton next = new RoundButton("Continue to payment  \u2192", BLUE, Color.WHITE, null);
            next.addActionListener(e -> goStep(2));
            btns.add(back); btns.add(next);
            btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            review.add(left(btns));
            review.revalidate();
        }

        void pay() {
            int selected = method.getSelectedIndex();

            if (selected == 0 &&
                (cardName.getText().trim().isEmpty()
                || cardNo.getText().trim().isEmpty()
                || exp.getText().trim().isEmpty()
                || cvv.getText().trim().isEmpty())) {

                warn("Please fill in the demo card fields.");
                return;
            }

            if (selected == 2) {
                if (gcashName.getText().trim().isEmpty()
                    || gcashNumber.getText().trim().isEmpty()
                    || gcashReference.getText().trim().isEmpty()) {

                    warn("Please complete all GCash information.");
                    return;
                }

                if (!gcashNumber.getText().trim().matches("\\d{11}")) {
                    warn("Please enter a valid 11-digit GCash mobile number.");
                    return;
                }

                if (gcashScreenshotPath.isEmpty()) {
                    warn("Please upload your GCash transaction screenshot.");
                    return;
                }
            }

            if (room.status != Status.AVAILABLE) {
                warn("Sorry, this room is no longer available.");
                return;
            }

            long n = nights();

            Booking b = new Booking();

            b.guest = name.getText().trim();
            b.email = email.getText().trim();
            b.phone = phone.getText().trim();

            b.room = room;
            b.nights = (int) n;
            b.guests = (Integer) guests.getSelectedItem();

            b.checkInDate = parse(checkIn.getText());
            b.checkOutDate = parse(checkOut.getText());

            b.subtotal = room.price * n;

            b.discountType = (String) discountType.getSelectedItem();
            b.discountIdNumber = discountIdNumber.getText().trim();
            b.discountRate = b.discountType.equals("None") ? 0 : 0.15;
            b.discountAmount = b.subtotal * b.discountRate;
            b.discountIdImagePath = discountIdImagePath;

            double discountedSubtotal = b.subtotal - b.discountAmount;

            b.tax = discountedSubtotal * 0.12;
            b.total = discountedSubtotal + b.tax;

            b.method = (String) method.getSelectedItem();
            b.stay = checkIn.getText().trim() + " \u2192 " + checkOut.getText().trim();
            b.ref = "SE-" + (10000 + room.number);

            if (selected == 2) {
                b.gcashName = gcashName.getText().trim();
                b.gcashNumber = gcashNumber.getText().trim();
                b.gcashReference = gcashReference.getText().trim();
                b.gcashScreenshotPath = gcashScreenshotPath;

                b.paymentStatus = PaymentStatus.PENDING_VERIFICATION;
                b.bookingStatus = BookingStatus.PENDING_VERIFICATION;

                // Temporarily hold the room while payment is being verified.
                app.service.updateRoomStatus(room, Status.RESERVED);

            } else {
                b.paymentStatus = PaymentStatus.PAID;
                b.bookingStatus = BookingStatus.CONFIRMED;

                app.service.updateRoomStatus(room, Status.RESERVED);
            }

            app.service.addBooking(app.hotel, b);
            refreshAll();

            receipt.removeAll();

            if (selected == 2) {
                receipt.add(left(lbl(
                    "Payment submitted for verification",
                    sans(Font.BOLD, 22),
                    BLUE
                )));
            } else {
                receipt.add(left(lbl(
                    "Booking confirmed (demo)",
                    sans(Font.BOLD, 22),
                    new Color(0x166534)
                )));
            }

            receipt.add(Box.createVerticalStrut(10));

            kv(receipt, "Reference", b.ref);
            kv(receipt, "Guest", b.guest);
            kv(receipt, "Room", room.name + " \u00B7 Room " + room.number);
            kv(receipt, "Stay", b.stay);
            kv(receipt, "Nights", String.valueOf(b.nights));
            kv(receipt, "Subtotal", money(b.subtotal));
            kv(receipt, "Discount", b.discountType + " (-" + money(b.discountAmount) + ")");
            kv(receipt, "Taxes and fees (12%)", money(b.tax));
            kv(receipt, "Total", money(b.total));
            kv(receipt, "Payment", b.method);

            if (selected == 2) {
                kv(receipt, "Payment status", "Pending Verification");
                kv(receipt, "GCash reference", b.gcashReference);
                receipt.add(Box.createVerticalStrut(8));
                receipt.add(left(lbl(
                    "Your payment will be reviewed by the administrator.",
                    sans(Font.PLAIN, 13),
                    MUTED
                )));
            } else {
                kv(receipt, "Payment status", "Paid (Demo)");
            }

            receipt.add(Box.createVerticalStrut(14));

            RoundButton done = new RoundButton(
                "Back to rooms",
                BLUE,
                Color.WHITE,
                null
            );

            done.addActionListener(e -> cards.show(content, "explore"));

            receipt.add(left(done));
            receipt.revalidate();

            goStep(3);
        }

        void goStep(int i) {
            stepLayout.show(stepCards, String.valueOf(i));
            for (int k = 0; k < 4; k++) {
                stepLbl[k].setForeground(k == i ? BLUE : MUTED);
                stepLbl[k].setFont(sans(k == i ? Font.BOLD : Font.PLAIN, 14));
            }
        }
    }
}