import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

/**
 * StayEase - MAIN MENU (run this one).
 * Holds the window and the sidebar, and switches
 * between GuestPortal and AdminPortal.
 *
 *   Compile:  javac *.java
 *   Run:      java MainMenu
 */
public class MainMenu extends JFrame {

    // ---------- main window ----------
    final Hotel hotel = new Hotel();
    final Service service = new Service();
    final CardLayout cards = new CardLayout();
    final JPanel content = new JPanel(cards);
    final GuestPortal guest;
    final AdminPortal admin;
    final Sidebar sidebar;

    // ===== ADMIN SECURITY =====
    private boolean adminAuthenticated = false;
    private final String ADMIN_PASSWORD = "admin123";

    /** Left navigation bar: logo, portal label, Guest/Admin switch. */
    static class Sidebar extends Portal {
        final MainMenu app;
        final JPanel sidebarNav = new JPanel();
        boolean adminMode = false;
        RoundButton guestBtn, adminBtn;

        Sidebar(MainMenu app) {
            this.app = app;
            build();
        }

        void build() {
            JPanel side = this;
            side.setLayout(new BorderLayout());
            side.setBackground(NAVY);
            side.setPreferredSize(new Dimension(250, 0));
            side.setBorder(new EmptyBorder(24, 18, 24, 18));

            JPanel top = new JPanel();
            top.setOpaque(false);
            top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));

            // StayEase sidebar logo
            JPanel logo = new JPanel();
            logo.setOpaque(false);
            logo.setLayout(new BoxLayout(logo, BoxLayout.Y_AXIS));
            logo.setAlignmentX(Component.LEFT_ALIGNMENT);

            try {
                ImageIcon original = new ImageIcon("stayease_logo.png");

                Image scaled = original.getImage().getScaledInstance(
                    170,
                    170,
                    Image.SCALE_SMOOTH
                );

                JLabel logoLabel = new JLabel(new ImageIcon(scaled));
                logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                logo.add(logoLabel);

            } catch (Exception e) {
                System.out.println("Sidebar logo could not be loaded.");
            }

            // Stay Ease title
            JLabel brandTitle = lbl(
                "Stay Ease",
                sans(Font.BOLD, 22),
                Color.WHITE
            );

            brandTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

            logo.add(Box.createVerticalStrut(8));
            logo.add(brandTitle);

            top.add(logo);
            top.add(Box.createVerticalStrut(30));

            sidebarNav.setOpaque(false);
            sidebarNav.setLayout(new BoxLayout(sidebarNav, BoxLayout.Y_AXIS));
            left(sidebarNav);
            top.add(sidebarNav);
            top.add(Box.createVerticalStrut(26));
            top.add(left(lbl("MAIN MENU", sans(Font.BOLD, 11), new Color(0x8DA0C2))));
            top.add(Box.createVerticalStrut(10));

            JPanel toggle = new RoundPanel(new GridLayout(1, 2, 4, 0), new Color(0x0C2044), 16, new Color(0x1B3566));
            toggle.setBorder(new EmptyBorder(5, 5, 5, 5));
            guestBtn = modeButton("Guest", false);
            adminBtn = modeButton("Admin", true);
            toggle.add(guestBtn);
            toggle.add(adminBtn);
            left(toggle);
            toggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
            top.add(toggle);
            side.add(top, BorderLayout.NORTH);

            JPanel note = new JPanel();
            note.setOpaque(false);
            note.setLayout(new BoxLayout(note, BoxLayout.Y_AXIS));
            note.add(left(lbl("Preview mode", sans(Font.BOLD, 14), Color.WHITE)));
            note.add(Box.createVerticalStrut(6));
            note.add(left(lbl("<html>Rooms and reservations are for<br>demonstration only.</html>", sans(Font.PLAIN, 13), new Color(0xA9B7D0))));
            side.add(note, BorderLayout.SOUTH);
        }

        RoundButton modeButton(String text, boolean admin) {
            RoundButton b = new RoundButton(text, admin == adminMode ? NAVY_2 : new Color(0, 0, 0, 0), Color.WHITE, null);
            b.addActionListener(e -> app.setMode(admin));
            return b;
        }
        
        void select(boolean admin) {
            adminMode = admin;
            guestBtn.bg = admin ? new Color(0, 0, 0, 0) : NAVY_2;
            adminBtn.bg = admin ? NAVY_2 : new Color(0, 0, 0, 0);
            guestBtn.repaint(); adminBtn.repaint();
            sidebarNav.removeAll();
            sidebarNav.add(left(lbl(admin ? "ADMIN PORTAL" : "GUEST PORTAL", sans(Font.BOLD, 11), new Color(0x8DA0C2))));
            sidebarNav.add(Box.createVerticalStrut(10));
            RoundButton nav = new RoundButton(admin ? "Overview   \u203A" : "Explore rooms   \u203A", new Color(0x143A78), Color.WHITE, null);
            nav.setHorizontalAlignment(SwingConstants.LEFT);
            nav.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
            nav.addActionListener(e -> app.goHome(admin));
            sidebarNav.add(left(nav));
            sidebarNav.revalidate(); sidebarNav.repaint();
        }
    }

    public MainMenu() {
        super("StayEase - Hotel & Reservations");
        // Set StayEase application window icon
        try {
            ImageIcon logo = new ImageIcon("stayease_logo.png");

            Image windowIcon = logo.getImage().getScaledInstance(
                64,
                64,
                Image.SCALE_SMOOTH
            );

            setIconImage(windowIcon);

        } catch (Exception e) {
            System.out.println("Unable to load StayEase window icon.");
        }
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1280, 820);
        setMinimumSize(new Dimension(1000, 640));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        guest = new GuestPortal(this);
        admin = new AdminPortal(this);
        sidebar = new Sidebar(this);

        content.add(guest, "guest");
        content.add(admin, "admin");
        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        setMode(false);
    }

    // ===== ADMIN PASSWORD LOGIN =====
    private boolean authenticateAdmin() {

        JPasswordField passwordField = new JPasswordField(18);

        JPanel panel = new JPanel(new BorderLayout(5, 10));

        JLabel message = new JLabel(
                "Enter the administrator password to continue:"
        );

        message.setFont(new Font("Arial", Font.PLAIN, 13));

        panel.add(message, BorderLayout.NORTH);
        panel.add(passwordField, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "StayEase | Admin Authentication",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return false;
        }

        String enteredPassword =
                new String(passwordField.getPassword());

        if (enteredPassword.equals(ADMIN_PASSWORD)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Welcome, Administrator!",
                    "Access Granted",
                    JOptionPane.INFORMATION_MESSAGE
            );

            return true;

        } else {

            JOptionPane.showMessageDialog(
                    this,
                    "Incorrect password. Access denied.",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE
            );

            return false;
        }
    }

    /** Switch between the Guest and Admin portals. */
    void setMode(boolean adminMode) {
        
        // Check password before entering Admin Portal
        if (adminMode && !adminAuthenticated) {

            if (!authenticateAdmin()) {
                return;
            }

            adminAuthenticated = true;
        }

        sidebar.select(adminMode);

        refreshAll();

        cards.show(
                content,
                adminMode ? "admin" : "guest"
        );
    }

    /** Sidebar button: jump to the portal's home page. */
    void goHome(boolean adminMode) {
        if (!adminMode) guest.showExplore();
        cards.show(content, adminMode ? "admin" : "guest");
    }

    /** Called whenever room statuses or bookings change. */
    void refreshAll() {
        guest.refresh();
        admin.refresh();
    }

    // ===== STAYEASE WELCOME / SPLASH SCREEN =====
    private static void showSplashScreen(Runnable onComplete) {

        JWindow splash = new JWindow();

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.BLACK);
        panel.setBorder(
            BorderFactory.createLineBorder(
                new Color(0xB99B61), 2
            )
        );

        JLabel welcome = new JLabel(
            "WELCOME TO",
            SwingConstants.CENTER
        );

        welcome.setFont(new Font("Arial", Font.BOLD, 18));
        welcome.setForeground(new Color(0xD6BD85));
        welcome.setBorder(new EmptyBorder(18, 10, 5, 10));

        panel.add(welcome, BorderLayout.NORTH);

        // Load the StayEase logo
        try {
            BufferedImage original = ImageIO.read(
                new File("stayease_sidebar.png")
            );

            int originalWidth = original.getWidth();
            int originalHeight = original.getHeight();

            // Keep the original image proportions
            double scale = Math.min(
                440.0 / originalWidth,
                550.0 / originalHeight
            );

            int newWidth = (int) (originalWidth * scale);
            int newHeight = (int) (originalHeight * scale);

            Image scaledImage = original.getScaledInstance(
                newWidth,
                newHeight,
                Image.SCALE_SMOOTH
            );

            JLabel logo = new JLabel(
                new ImageIcon(scaledImage),
                SwingConstants.CENTER
            );

            panel.add(logo, BorderLayout.CENTER);

        } catch (Exception ex) {

            JLabel error = new JLabel(
                "StayEase",
                SwingConstants.CENTER
            );

            error.setFont(new Font("Georgia", Font.BOLD, 42));
            error.setForeground(new Color(0xD6BD85));

            panel.add(error, BorderLayout.CENTER);

            System.out.println(
                "Logo could not be loaded: " + ex.getMessage()
            );
        }

        JLabel tagline = new JLabel(
            "Inheriting Comfort, Encapsulating Luxury",
            SwingConstants.CENTER
        );

        tagline.setFont(new Font("Arial", Font.PLAIN, 12));
        tagline.setForeground(new Color(0xA9B7D0));
        tagline.setBorder(new EmptyBorder(5, 10, 18, 10));

        panel.add(tagline, BorderLayout.SOUTH);

        splash.setContentPane(panel);
        splash.setSize(500, 650);
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);

        // Automatically open Main Menu after 3 seconds
        Timer timer = new Timer(3000, e -> {
            splash.dispose();
            onComplete.run();
        });

        timer.setRepeats(false);
        timer.start();
    }

    // ===== APPLICATION START =====
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            showSplashScreen(() -> {

                MainMenu menu = new MainMenu();
                menu.setVisible(true);

            });

        });
    }
}