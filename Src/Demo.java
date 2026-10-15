
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
class AWT extends JFrame {
ArrayList<String[]> students = new ArrayList<>();
    AWT() {
        JLabel title = new JLabel("Student Management System", JLabel.CENTER);
        JButton b1 = new JButton("Add Student");
        JButton b2 = new JButton("View Students");
        JButton b3 = new JButton("Update Student");
        JButton b4 = new JButton("Delete Student");
        JLabel welcome = new JLabel("Welcome! Manage your student records here.", JLabel.CENTER);
        setTitle("Student Management System");
        setLayout(new BorderLayout(10, 10));
        JPanel topPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        topPanel.add(title);
        topPanel.add(welcome);
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        b1.setPreferredSize(new Dimension(180, 60));
        b2.setPreferredSize(new Dimension(180, 60));
        b3.setPreferredSize(new Dimension(180, 60));
        b4.setPreferredSize(new Dimension(180, 60));
        JPanel buttonContainer = new JPanel(new GridBagLayout());
        buttonContainer.add(buttonPanel);
        buttonPanel.add(b1);
        buttonPanel.add(b2);
        buttonPanel.add(b3);
        buttonPanel.add(b4);
        add(topPanel, BorderLayout.NORTH);
        add(buttonContainer, BorderLayout.CENTER);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
b1.addActionListener(e -> {
    JFrame form = new JFrame("Add Student");

    form.setSize(400, 350);
    form.setLocationRelativeTo(this);
    form.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    form.setLayout(new GridLayout(5, 2, 10, 10));

    JTextField idField = new JTextField();
    JTextField nameField = new JTextField();
    JTextField courseField = new JTextField();
    JTextField marksField = new JTextField();

    form.add(new JLabel("Student ID:"));
    form.add(idField);

    form.add(new JLabel("Student Name:"));
    form.add(nameField);

    form.add(new JLabel("Course:"));
    form.add(courseField);

    form.add(new JLabel("Marks:"));
    form.add(marksField);

    JButton submit = new JButton("Submit");

    form.add(new JLabel(""));
    form.add(submit);

    submit.addActionListener(event -> {
        if (idField.getText().trim().isEmpty() ||
            nameField.getText().trim().isEmpty() ||
            courseField.getText().trim().isEmpty() ||
            marksField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                form,
                "Please fill in all fields!"
            );

        }   else {
    String id = idField.getText().trim();
    String name = nameField.getText().trim();
    String course = courseField.getText().trim();
    String marks = marksField.getText().trim();


try {
    double marksValue = Double.parseDouble(marks);

    if (marksValue < 0 || marksValue > 100) {
        JOptionPane.showMessageDialog(
            form, "Marks must be between 0 and 100!"
        );
        return;
    }

    String sql = "INSERT INTO students "
               + "(student_id, name, course, marks) "
               + "VALUES (?, ?, ?, ?)";

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {

        ps.setString(1, id);
        ps.setString(2, name);
        ps.setString(3, course);
        ps.setDouble(4, marksValue);

        ps.executeUpdate();

        JOptionPane.showMessageDialog(
            form, "Student added successfully!"
        );

        form.dispose();
    }

} catch (NumberFormatException ex) {
    JOptionPane.showMessageDialog(
        form, "Please enter valid numeric marks!"
    );
} catch (SQLException ex) {
    if (ex.getErrorCode() == 1062) {
        JOptionPane.showMessageDialog(
            form, "This Student ID already exists!"
        );
    } else {
        JOptionPane.showMessageDialog(
            form,
            "Could not save student: " + ex.getMessage()
        );
        ex.printStackTrace();
    }
}

}  });

    form.setVisible(true);
});

b2.addActionListener(e -> {
    JFrame viewFrame = new JFrame("View Students");

    String[] columns = {
        "Student ID", "Name", "Course", "Marks"
    };

    java.util.ArrayList<Object[]> rows =
            new java.util.ArrayList<>();

    String sql = "SELECT student_id, name, course, marks "
               + "FROM students";

    try (java.sql.Connection con =
                 DBConnection.getConnection();
         java.sql.PreparedStatement ps =
                 con.prepareStatement(sql);
         java.sql.ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Object[] row = {
                rs.getString("student_id"),
                rs.getString("name"),
                rs.getString("course"),
                rs.getBigDecimal("marks")
            };

            rows.add(row);
        }

        Object[][] data = rows.toArray(new Object[0][]);

        JTable table = new JTable(data, columns);
        JScrollPane scrollPane = new JScrollPane(table);

        viewFrame.add(scrollPane);
        viewFrame.setSize(600, 300);
        viewFrame.setLocationRelativeTo(this);
        viewFrame.setDefaultCloseOperation(
                JFrame.DISPOSE_ON_CLOSE);
        viewFrame.setVisible(true);

    } catch (java.sql.SQLException ex) {
        JOptionPane.showMessageDialog(
            this,
            "Could not load students: " + ex.getMessage()
        );
        ex.printStackTrace();
    }
});



b3.addActionListener(e -> {
    String id = JOptionPane.showInputDialog(
        this, "Enter Student ID to update:"
    );

    if (id == null || id.trim().isEmpty()) {
        return;
    }

    id = id.trim();

    String name;
    String course;
    java.math.BigDecimal marks;

    String selectSql =
        "SELECT name, course, marks FROM students WHERE student_id = ?";

    try (java.sql.Connection con = DBConnection.getConnection();
         java.sql.PreparedStatement ps =
             con.prepareStatement(selectSql)) {

        ps.setString(1, id);

        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                JOptionPane.showMessageDialog(
                    this, "Student not found!"
                );
                return;
            }

            name = rs.getString("name");
            course = rs.getString("course");
            marks = rs.getBigDecimal("marks");
        }

    } catch (java.sql.SQLException ex) {
        JOptionPane.showMessageDialog(
            this, "Could not find student: " + ex.getMessage()
        );
        return;
    }

    JTextField nameField = new JTextField(name);
    JTextField courseField = new JTextField(course);
    JTextField marksField = new JTextField(marks.toPlainString());

    Object[] fields = {
        "Student Name:", nameField,
        "Course:", courseField,
        "Marks:", marksField
    };

    int result = JOptionPane.showConfirmDialog(
        this, fields, "Update Student Details",
        JOptionPane.OK_CANCEL_OPTION
    );

    if (result != JOptionPane.OK_OPTION) {
        return;
    }

    String newName = nameField.getText().trim();
    String newCourse = courseField.getText().trim();
    String marksText = marksField.getText().trim();

    if (newName.isEmpty() || newCourse.isEmpty()
            || marksText.isEmpty()) {
        JOptionPane.showMessageDialog(
            this, "Please fill in all fields!"
        );
        return;
    }

    try {
        double newMarks = Double.parseDouble(marksText);

        if (!Double.isFinite(newMarks)
                || newMarks < 0 || newMarks > 100) {
            JOptionPane.showMessageDialog(
                this, "Marks must be between 0 and 100!"
            );
            return;
        }

        String updateSql =
            "UPDATE students SET name = ?, course = ?, marks = ? "
            + "WHERE student_id = ?";

        try (java.sql.Connection con = DBConnection.getConnection();
             java.sql.PreparedStatement ps =
                 con.prepareStatement(updateSql)) {

            ps.setString(1, newName);
            ps.setString(2, newCourse);
            ps.setDouble(3, newMarks);
            ps.setString(4, id);

            int updated = ps.executeUpdate();

            if (updated > 0) {
                JOptionPane.showMessageDialog(
                    this, "Student updated successfully!"
                );
            } else {
                JOptionPane.showMessageDialog(
                    this, "Student not found!"
                );
            }
        }

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(
            this, "Please enter valid numeric marks!"
        );
    } catch (java.sql.SQLException ex) {
        JOptionPane.showMessageDialog(
            this, "Could not update student: " + ex.getMessage()
        );
        ex.printStackTrace();
    }
});

b4.addActionListener(e -> {
    String id = JOptionPane.showInputDialog(
        this, "Enter Student ID to delete:"
    );

    if (id == null || id.trim().isEmpty()) {
        return;
    }

    String searchId = id.trim();

    String selectSql =
        "SELECT name FROM students WHERE student_id = ?";

    try (java.sql.Connection con = DBConnection.getConnection();
         java.sql.PreparedStatement ps =
             con.prepareStatement(selectSql)) {

        ps.setString(1, searchId);

        try (java.sql.ResultSet rs = ps.executeQuery()) {
            if (!rs.next()) {
                JOptionPane.showMessageDialog(
                    this, "Student not found!"
                );
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete student " + rs.getString("name")
                    + " (ID: " + searchId + ")?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION
            );

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }
        }

        String deleteSql =
            "DELETE FROM students WHERE student_id = ?";

        try (java.sql.PreparedStatement deletePs =
                 con.prepareStatement(deleteSql)) {

            deletePs.setString(1, searchId);

            int deleted = deletePs.executeUpdate();

            if (deleted > 0) {
                JOptionPane.showMessageDialog(
                    this, "Student deleted successfully!"
                );
            } else {
                JOptionPane.showMessageDialog(
                    this, "Student was not found or was already deleted!"
                );
            }
        }

    } catch (java.sql.SQLException ex) {
        JOptionPane.showMessageDialog(
            this, "Could not delete student: " + ex.getMessage()
        );
        ex.printStackTrace();
    }
});



        setVisible(true);
    }}
public class Demo {
    public static void main(String[] args) {
        new AWT();
    }}

