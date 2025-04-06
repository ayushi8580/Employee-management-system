import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class EmployeePayrollSystem {
    public static void main(String[] args) {
        new LoginFrame();
    }
}

class LoginFrame extends JFrame implements ActionListener {
    JTextField usernameField;
    JPasswordField passwordField;
    JButton loginButton;

    LoginFrame() {
        setTitle("Employee Payroll System - Login");
        setSize(400, 300);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Username:"));
        usernameField = new JTextField();
        add(usernameField);

        add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        add(passwordField);

        loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        add(loginButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        if (authenticateUser(username, password)) {
            JOptionPane.showMessageDialog(this, "Login Successful");
            new EmployeeDashboard();
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Credentials");
        }
    }

    private boolean authenticateUser(String username, String password) {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll", "root", "password");
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM users WHERE username=? AND password=?");
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}

class EmployeeDashboard extends JFrame {
    EmployeeDashboard() {
        setTitle("Employee Payroll System - Dashboard");
        setSize(600, 400);
        setLayout(new FlowLayout());

        JButton addEmployeeBtn = new JButton("Add Employee");
        addEmployeeBtn.addActionListener(e -> new AddEmployeeFrame());
        add(addEmployeeBtn);

        JButton viewPayrollBtn = new JButton("View Payroll");
        viewPayrollBtn.addActionListener(e -> new ViewPayrollFrame());
        add(viewPayrollBtn);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}

class AddEmployeeFrame extends JFrame {
    JTextField nameField, salaryField;
    JButton saveButton;

    AddEmployeeFrame() {
        setTitle("Add Employee");
        setSize(400, 300);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("Name:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Salary:"));
        salaryField = new JTextField();
        add(salaryField);

        saveButton = new JButton("Save");
        saveButton.addActionListener(e -> saveEmployee());
        add(saveButton);

        setVisible(true);
    }

    private void saveEmployee() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll", "root", "password");
            PreparedStatement stmt = con.prepareStatement("INSERT INTO employees (name, salary) VALUES (?, ?)");
            stmt.setString(1, nameField.getText());
            stmt.setDouble(2, Double.parseDouble(salaryField.getText()));
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Employee Added Successfully");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

class ViewPayrollFrame extends JFrame {
    ViewPayrollFrame() {
        setTitle("Payroll Records");
        setSize(500, 400);
        setLayout(new BorderLayout());

        JTextArea textArea = new JTextArea();
        add(new JScrollPane(textArea), BorderLayout.CENTER);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/payroll", "root", "password");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM employees");
            while (rs.next()) {
                textArea.append("ID: " + rs.getInt("id") + ", Name: " + rs.getString("name") + ", Salary: " + rs.getDouble("salary") + "\n");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        setVisible(true);
    }
}
