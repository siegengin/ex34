import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.sql.*;
import java.awt.*;
import java.awt.event.*;

public class Ex34 extends JApplet {
    private static final long serialVersionUID = 1L;
    private final JTextField[] textFields = new JTextField[9];

    private Connection connection;

    public void init() {
        setLayout(new BorderLayout());
        initializeDB();

        JPanel jPanel1 = new JPanel(new FlowLayout());
        jPanel1.setBorder(new TitledBorder("Staff information"));

        String[] labels = {"ID", "Last Name", "First Name", "mi", "Address", "City", "State", "Telephone", "E-mail"};
        for (int i = 0; i < labels.length; i++) {
            jPanel1.add(new JLabel(labels[i]));
            textFields[i] = (i == 3 || i == 6) ? new JTextField(3) : new JTextField(10);
            jPanel1.add(textFields[i]);
        }
        add(jPanel1, BorderLayout.CENTER);

        JPanel jPanel2 = new JPanel(new GridLayout(1, 4, 5, 5));
        jPanel2.setBorder(new EmptyBorder(5, 5, 5, 5));
        JButton jButton1 = new JButton("View");
        jPanel2.add(jButton1);
        JButton jButton2 = new JButton("Insert");
        jPanel2.add(jButton2);
        JButton jButton3 = new JButton("Update");
        jPanel2.add(jButton3);
        JButton jButton4 = new JButton("Clear");
        jPanel2.add(jButton4);
        add(jPanel2, BorderLayout.SOUTH);

        jButton1.addActionListener(e -> viewRecord());
        jButton2.addActionListener(e -> insertRecord());
        jButton3.addActionListener(e -> updateRecord());
        jButton4.addActionListener(e -> clearFields());
    }

    private void initializeDB() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Updated driver name
            connection = DriverManager.getConnection("jdbc:mysql://localhost/java-book", "root", "root");
        } catch (Exception ex) {
            ex.printStackTrace();
            showErrorDialog("Database initialization failed!");
        }
    }

    private void viewRecord() {
        String queryString = "select lastName, firstName, mi, address, city, state, telephone, email from Staff where id = ?;";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            preparedStatement.setString(1, textFields[0].getText());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    for (int i = 1; i < textFields.length; i++) {
                        textFields[i].setText(resultSet.getString(i));
                    }
                } else {
                    clearFields();
                }
            }
        } catch (SQLException e2) {
            e2.printStackTrace();
            showErrorDialog("Failed to view record!");
        }
    }


    private void insertRecord() {
        String queryString = "insert into Staff (id, lastName, firstName, mi, address, city, state, telephone, email) values (?, ?, ?, ?, ?, ?, ?, ?, ?);";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            for (int i = 0; i < textFields.length; i++) {
                preparedStatement.setString(i + 1, textFields[i].getText());
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e2) {
            e2.printStackTrace();
            showErrorDialog("Failed to insert record!");
        }
    }

    private void updateRecord() {
        String queryString = "update Staff set lastName = ?, firstName = ?, mi = ?, address = ?, city = ?, state = ?, telephone = ?, email = ? where id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)) {
            for (int i = 1; i < textFields.length; i++) {
                preparedStatement.setString(i, textFields[i].getText());
            }
            preparedStatement.setString(9, textFields[0].getText());
            preparedStatement.executeUpdate();
        } catch (SQLException e2) {
            e2.printStackTrace();
            showErrorDialog("Failed to update record!");
        }
    }

    private void clearFields() {
        for (JTextField textField : textFields) {
            textField.setText("");
        }
    }

    private void showErrorDialog(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** Main method */
    public static void main(String[] args) {
        Ex34 applet = new Ex34();
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Exercise01");
        frame.getContentPane().add(applet, BorderLayout.CENTER);
        applet.init();
        applet.start();
        frame.setSize(540, 200);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
