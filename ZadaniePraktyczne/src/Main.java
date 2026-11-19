import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class Main extends JFrame {

    private JTextField txtImie;
    private JTextField txtNazwisko;
    private JTextField txtStanowisko;
    private JButton btnDodaj;
    private JTable tabela;
    private DefaultTableModel model;

    private final String DB_URL = "jdbc:sqlite:pracownicy.db";

    public Main() {
        setTitle("INF.04 - System Obsługi Pracowników");
        setSize(650, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel panelFormularza = new JPanel(new GridLayout(4, 2, 5, 5));
        panelFormularza.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panelFormularza.add(new JLabel("Imię:"));
        txtImie = new JTextField();
        panelFormularza.add(txtImie);

        panelFormularza.add(new JLabel("Nazwisko:"));
        txtNazwisko = new JTextField();
        panelFormularza.add(txtNazwisko);

        panelFormularza.add(new JLabel("Stanowisko:"));
        txtStanowisko = new JTextField();
        panelFormularza.add(txtStanowisko);

        panelFormularza.add(new JLabel(""));
        btnDodaj = new JButton("Dodaj Pracownika");
        panelFormularza.add(btnDodaj);

        add(panelFormularza, BorderLayout.NORTH);

        String[] kolumny = {"ID", "Imię", "Nazwisko", "Stanowisko"};
        model = new DefaultTableModel(kolumny, 0);
        tabela = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(tabela);

        add(scrollPane, BorderLayout.CENTER);

        utworzTabele();
        odswiezTabele();

        btnDodaj.addActionListener(e -> dodajPracownika());
    }

    private Connection polacz() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    private void utworzTabele() {
        String sql = "CREATE TABLE IF NOT EXISTS pracownicy (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "imie TEXT NOT NULL, " +
                "nazwisko TEXT NOT NULL, " +
                "stanowisko TEXT NOT NULL)";

        try (Connection conn = polacz();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Błąd bazy danych: " + e.getMessage());
        }
    }

    private void odswiezTabele() {
        model.setRowCount(0);
        String sql = "SELECT * FROM pracownicy";

        try (Connection conn = polacz();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String imie = rs.getString("imie");
                String nazwisko = rs.getString("nazwisko");
                String stanowisko = rs.getString("stanowisko");
                model.addRow(new Object[]{id, imie, nazwisko, stanowisko});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Błąd odczytu danych: " + e.getMessage());
        }
    }

    private void dodajPracownika() {
        String imie = txtImie.getText().trim();
        String nazwisko = txtNazwisko.getText().trim();
        String stanowisko = txtStanowisko.getText().trim();

        if (imie.isEmpty() || nazwisko.isEmpty() || stanowisko.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione!", "Błąd walidacji", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "INSERT INTO pracownicy (imie, nazwisko, stanowisko) VALUES (?, ?, ?)";

        try (Connection conn = polacz();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, imie);
            pstmt.setString(2, nazwisko);
            pstmt.setString(3, stanowisko);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Dodano pracownika pomyślnie!");

            txtImie.setText("");
            txtNazwisko.setText("");
            txtStanowisko.setText("");

            odswiezTabele();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Błąd zapisu: " + e.getMessage(), "Błąd", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main().setVisible(true);
        });
    }
}