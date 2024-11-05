import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// Abstract base class representing a general book (Abstraction)
abstract class Book {
    protected String title;
    protected String author;
    protected double price;
    protected double rentCost;
    protected boolean isRented;

    public Book(String title, String author, double price, double rentCost) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.rentCost = rentCost;
        this.isRented = false;
    }

    public abstract boolean rent();

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public double getPrice() {
        return price;
    }

    public double getRentCost() {
        return rentCost;
    }

    public boolean isRented() {
        return isRented;
    }
}

// RentableBook subclass that extends Book (Inheritance)
class RentableBook extends Book {

    public RentableBook(String title, String author, double price, double rentCost) {
        super(title, author, price, rentCost);
    }

    @Override
    public boolean rent() {
        if (!isRented) {
            isRented = true;
            return true;
        }
        return false;
    }

    public void returnBook() {
        isRented = false;
    }
}

// LibraryBackend class that manages book operations
class LibraryBackend {
    private List<Book> books = new ArrayList<>();
    private int bookCount = 0;

    public void addBook(String title, String author, double price, double rentCost) {
        books.add(new RentableBook(title, author, price, rentCost));
        bookCount++;
    }

    public void deleteBook(String title) {
        books.removeIf(book -> book.getTitle().equals(title) && !book.isRented());
        bookCount = Math.max(bookCount - 1, 0);
    }

    public boolean rentBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title)) {
                if (book.rent()) {
                    bookCount--;
                    return true;
                }
            }
        }
        return false;
    }

    public boolean returnBook(String title) {
        for (Book book : books) {
            if (book.getTitle().equals(title) && book.isRented()) {
                ((RentableBook) book).returnBook();
                bookCount++;
                return true;
            }
        }
        return false;
    }

    public List<Book> getInventory() {
        return books;
    }

    public int getBookCount() {
        return bookCount;
    }
}

// Custom JPanel with background image
class BackgroundPanel extends JPanel {
    private Image backgroundImage;

    public BackgroundPanel(String imagePath) {
        backgroundImage = new ImageIcon(imagePath).getImage();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }
}

// LibraryUI class for the frontend
public class tog extends JFrame {
    private LibraryBackend backend = new LibraryBackend();
    private JTextArea inventoryDisplay;
    private Icon dialogIcon;

    public tog() {
        setTitle("Library Management System");
        setSize(600, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);

        dialogIcon = resizeIcon(new ImageIcon("logo.png"), 40, 40);
        setIconImage(((ImageIcon) dialogIcon).getImage());

        BackgroundPanel bgPanel = new BackgroundPanel("bg.jpg");
        bgPanel.setLayout(new BorderLayout());

        inventoryDisplay = new JTextArea(10, 50);
        inventoryDisplay.setEditable(false);
        updateInventoryDisplay();

        inventoryDisplay.setBackground(new Color(240, 240, 240));
        inventoryDisplay.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY, 3),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        inventoryDisplay.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel inventoryPanel = new JPanel();
        inventoryPanel.setOpaque(false);
        inventoryPanel.setLayout(new GridBagLayout());
        inventoryPanel.add(new JScrollPane(inventoryDisplay));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout());
        JButton addButton = new JButton("Add Book");
        JButton deleteButton = new JButton("Delete Book");
        JButton rentButton = new JButton("Rent Book");
        JButton returnButton = new JButton("Return Book");

        addButton.addActionListener(e -> addBook());
        deleteButton.addActionListener(e -> deleteBook());
        rentButton.addActionListener(e -> rentBook());
        returnButton.addActionListener(e -> returnBook());

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);

        bgPanel.add(inventoryPanel, BorderLayout.CENTER);
        bgPanel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(bgPanel);
    }

    private Icon resizeIcon(ImageIcon icon, int width, int height) {
        Image img = icon.getImage();
        Image newImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(newImg);
    }

    private void addBook() {
        JPanel inputPanel = new JPanel(new GridLayout(4, 2));
        inputPanel.add(new JLabel("Title:"));
        JTextField titleField = new JTextField();
        inputPanel.add(titleField);
        inputPanel.add(new JLabel("Author:"));
        JTextField authorField = new JTextField();
        inputPanel.add(authorField);
        inputPanel.add(new JLabel("Price:"));
        JTextField priceField = new JTextField();
        inputPanel.add(priceField);
        inputPanel.add(new JLabel("Rent Cost:"));
        JTextField rentCostField = new JTextField();
        inputPanel.add(rentCostField);

        int result = JOptionPane.showConfirmDialog(
                this, inputPanel, "Add Book", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, dialogIcon);

        if (result == JOptionPane.OK_OPTION) {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String priceText = priceField.getText().trim();
            String rentCostText = rentCostField.getText().trim();

            if (!title.isEmpty() && !author.isEmpty() && !priceText.isEmpty() && !rentCostText.isEmpty()) {
                try {
                    double price = Double.parseDouble(priceText);
                    double rentCost = Double.parseDouble(rentCostText);
                    backend.addBook(title, author, price, rentCost);
                    JOptionPane.showMessageDialog(this, "Book added!", "Success", JOptionPane.INFORMATION_MESSAGE, dialogIcon);
                    updateInventoryDisplay();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Please enter valid numbers for price and rent cost.",
                            "Input Error", JOptionPane.ERROR_MESSAGE, dialogIcon);
                }
            }
        }
    }

    private void deleteBook() {
        String title = (String) JOptionPane.showInputDialog(
                this, "Enter Book Title to Delete:", "Delete Book", JOptionPane.PLAIN_MESSAGE, dialogIcon, null, "");

        if (title != null && !title.isEmpty()) {
            backend.deleteBook(title);
            JOptionPane.showMessageDialog(this, "Book deleted if it exists and is not rented.",
                    "Delete Book", JOptionPane.INFORMATION_MESSAGE, dialogIcon);
            updateInventoryDisplay();
        }
    }

    private void rentBook() {
        String title = (String) JOptionPane.showInputDialog(
                this, "Enter Book Title to Rent:", "Rent Book", JOptionPane.PLAIN_MESSAGE, dialogIcon, null, "");

        if (title != null && !title.isEmpty()) {
            if (backend.rentBook(title)) {
                JOptionPane.showMessageDialog(this, "Book rented!", "Rent Book", JOptionPane.INFORMATION_MESSAGE, dialogIcon);
            } else {
                JOptionPane.showMessageDialog(this, "Book is unavailable or already rented.",
                        "Rent Book", JOptionPane.WARNING_MESSAGE, dialogIcon);
            }
            updateInventoryDisplay();
        }
    }

    private void returnBook() {
        String title = (String) JOptionPane.showInputDialog(
                this, "Enter Book Title to Return:", "Return Book", JOptionPane.PLAIN_MESSAGE, dialogIcon, null, "");

        if (title != null && !title.isEmpty()) {
            if (backend.returnBook(title)) {
                JOptionPane.showMessageDialog(this, "Book returned!", "Return Book", JOptionPane.INFORMATION_MESSAGE, dialogIcon);
            } else {
                JOptionPane.showMessageDialog(this, "Book is not rented or does not exist.",
                        "Return Book", JOptionPane.WARNING_MESSAGE, dialogIcon);
            }
            updateInventoryDisplay();
        }
    }

    private void updateInventoryDisplay() {
        StringBuilder displayText = new StringBuilder("Inventory:\n");
        for (Book book : backend.getInventory()) {
            displayText.append(String.format("%-30s %-20s Price: $%.2f Rent: $%.2f Status: %s\n",
                    book.getTitle(), book.getAuthor(), book.getPrice(), book.getRentCost(),
                    book.isRented() ? "Rented" : "Available"));
        }
        displayText.append("\nTotal Books Available: ").append(backend.getBookCount());
        inventoryDisplay.setText(displayText.toString());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LibraryUI libraryUI = new LibraryUI();
            libraryUI.setVisible(true);
        });
    }
}