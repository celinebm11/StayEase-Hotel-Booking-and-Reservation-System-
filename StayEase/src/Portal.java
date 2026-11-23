import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

/**
 * Shared GUI base class for StayEase.
 * Contains colors, fonts, and reusable GUI components.
 */
public class Portal extends JPanel {

    // ---------- theme ----------
    static final Color NAVY = new Color(0x081733);
    static final Color NAVY_2 = new Color(0x10294F);
    static final Color BLUE = new Color(0x1463D6);
    static final Color BG = new Color(0xF4F6FA);
    static final Color INK = new Color(0x0F172A);
    static final Color MUTED = new Color(0x64748B);
    static final Color LINE = new Color(0xE2E8F0);

    static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("MM/dd/yyyy");

    // ---------- shared GUI helpers ----------
    static Font sans(int style, int size) {
        return new Font("Segoe UI", style, size);
    }

    static Font serif(int size) {
        return new Font("Georgia", Font.PLAIN, size);
    }

    static String money(double value) {
        return String.format("Php %,.2f", value);
    }

    static JLabel lbl(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    static <T extends JComponent> T left(T component) {
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        return component;
    }

    // ---------- rounded panel ----------
    static class RoundPanel extends JPanel {

        Color bg, border;
        int arc;

        RoundPanel(LayoutManager layout, Color bg, int arc, Color border) {
            super(layout);
            this.bg = bg;
            this.arc = arc;
            this.border = border;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(bg);

            g2.fillRoundRect(
                    0, 0,
                    getWidth() - 1,
                    getHeight() - 1,
                    arc, arc
            );

            if (border != null) {
                g2.setColor(border);

                g2.drawRoundRect(
                        0, 0,
                        getWidth() - 1,
                        getHeight() - 1,
                        arc, arc
                );
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------- rounded button ----------
    static class RoundButton extends JButton {

        Color bg, border;

        RoundButton(String text, Color bg, Color fg, Color border) {

            super(text);

            this.bg = bg;
            this.border = border;

            setForeground(fg);
            setFont(sans(Font.BOLD, 13));
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);

            setBorder(new EmptyBorder(10, 18, 10, 18));

            setCursor(
                    Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            g2.setColor(bg);

            g2.fillRoundRect(
                    0, 0,
                    getWidth() - 1,
                    getHeight() - 1,
                    14, 14
            );

            if (border != null) {
                g2.setColor(border);

                g2.drawRoundRect(
                        0, 0,
                        getWidth() - 1,
                        getHeight() - 1,
                        14, 14
                );
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ---------- input field ----------
    static class Field extends JTextField {

        final String placeholder;

        Field(String placeholder) {

            this.placeholder = placeholder;

            setFont(sans(Font.PLAIN, 14));

            setBorder(
                    BorderFactory.createCompoundBorder(
                            new LineBorder(LINE, 1, true),
                            new EmptyBorder(9, 12, 9, 12)
                    )
            );
        }

        @Override
        protected void paintComponent(Graphics g) {

            super.paintComponent(g);

            if (getText().isEmpty()) {

                Graphics2D g2 = (Graphics2D) g.create();

                g2.setRenderingHint(
                        RenderingHints.KEY_TEXT_ANTIALIASING,
                        RenderingHints.VALUE_TEXT_ANTIALIAS_ON
                );

                g2.setColor(new Color(0x94A3B8));
                g2.setFont(getFont());

                FontMetrics fm = g2.getFontMetrics();
                Insets insets = getInsets();

                g2.drawString(
                        placeholder,
                        insets.left,
                        (getHeight() - fm.getHeight()) / 2
                                + fm.getAscent()
                );

                g2.dispose();
            }
        }
    }

    // ---------- scroll panel ----------
    static class ScrollPanel extends JPanel implements Scrollable {

        ScrollPanel(LayoutManager layout) {
            super(layout);
        }

        public Dimension getPreferredScrollableViewportSize() {
            return getPreferredSize();
        }

        public int getScrollableUnitIncrement(
                Rectangle rectangle, int orientation, int direction) {
            return 24;
        }

        public int getScrollableBlockIncrement(
                Rectangle rectangle, int orientation, int direction) {
            return 160;
        }

        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    // ---------- room illustration ----------
    static class RoomImage extends JPanel {

        final Room room;
        final boolean badge;

        RoomImage(Room room, boolean badge) {
            this.room = room;
            this.badge = badge;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {

            Graphics2D g2 = (Graphics2D) g.create();

            g2.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON
            );

            int width = getWidth();
            int height = getHeight();

            Color first, second;

            switch (room.type) {

                case "Single":
                    first = new Color(0x2A4A66);
                    second = new Color(0x0B1626);
                    break;

                case "Double":
                    first = new Color(0x7A5530);
                    second = new Color(0x1D130B);
                    break;

                case "Deluxe":
                    first = new Color(0xB9A98F);
                    second = new Color(0x39414F);
                    break;

                default:
                    first = new Color(0x2A8FB0);
                    second = new Color(0x5C3B22);
                    break;
            }

            g2.setPaint(
                    new GradientPaint(0, 0, first, 0, height, second)
            );

            g2.fillRect(0, 0, width, height);

            // Headboard, mattress, and pillows
            int bedWidth = Math.min(width - 80, 360);
            int bedX = (width - bedWidth) / 2;
            int bedY = height / 2;

            g2.setColor(new Color(255, 255, 255, 40));

            g2.fillRoundRect(
                    bedX, bedY - 50,
                    bedWidth, 60,
                    12, 12
            );

            g2.setColor(new Color(0xF2F2EE));

            g2.fillRoundRect(
                    bedX - 10, bedY,
                    bedWidth + 20, 60,
                    22, 22
            );

            g2.setColor(new Color(0x37506B));

            g2.fillRoundRect(
                    bedX - 10, bedY + 28,
                    bedWidth + 20, 32,
                    14, 14
            );

            g2.setColor(Color.WHITE);

            g2.fillRoundRect(
                    bedX + 20, bedY - 12,
                    70, 28,
                    12, 12
            );

            g2.fillRoundRect(
                    bedX + bedWidth - 90, bedY - 12,
                    70, 28,
                    12, 12
            );

            // Room status badge
            if (badge) {

                String text = room.status.label;

                g2.setFont(sans(Font.PLAIN, 12));

                FontMetrics fm = g2.getFontMetrics();
                int pillWidth = fm.stringWidth(text) + 20;

                g2.setColor(new Color(255, 255, 255, 230));

                g2.fillRoundRect(
                        14, 14,
                        pillWidth, 26,
                        26, 26
                );

                g2.setColor(statusColor(room.status));

                g2.drawString(text, 24, 31);
            }

            g2.dispose();
        }

        // Get the text color for each room status
        private Color statusColor(Status status) {

            switch (status) {

                case AVAILABLE:
                    return new Color(0x166534);

                case RESERVED:
                    return new Color(0x1D4ED8);

                case OCCUPIED:
                    return new Color(0x9A5B13);

                default:
                    return Color.BLACK;
            }
        }
    }

    // ---------- scroll pane helper ----------
    static JScrollPane wrap(JComponent component) {

        JScrollPane scrollPane = new JScrollPane(component);

        scrollPane.setBorder(null);

        scrollPane.getVerticalScrollBar().setUnitIncrement(18);

        scrollPane.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        );

        return scrollPane;
    }
}