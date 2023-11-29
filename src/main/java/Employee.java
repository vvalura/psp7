import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import org.apache.commons.dbutils.DbUtils;

public class Employee {
    private JPanel Main;
    private JTextField txtName;
    private JTextField txtSalary;
    private JTextField txtMobile;
    private JButton saveButton;
    private JTable table1;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JTextField txtid;

    public static void main(String args[]){
        JFrame frame = new JFrame ("Employee");
        frame.setContentPane(new Employee().Main);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


    Connection con;
    PreparedStatement pst;

    public void connect(){
        try{
            Class.forName("org.postgresql.Driver");
            con = DriverManager.getConnection("jdbc:postgresql://localhost:5432/lab7_psp","postgres", "root");
            System.out.println("Success");
        } catch (ClassNotFoundException ex){
            ex.printStackTrace();
        }
        catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Employee() {
        connect();
        table_load();
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String empname, salary, mobile;

                empname = txtName.getText();
                salary = txtSalary.getText();
                mobile = txtMobile.getText();

                try{
                    pst = con.prepareStatement("insert into employee(empname, salary, mobile) values (?,?,?)");
                    pst.setString(1, empname);
                    pst.setString(2, salary);
                    pst.setString(3, mobile);
                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record added!");
                    txtName.setText("");
                    txtSalary.setText("");
                    txtMobile.setText("");
                    txtName.requestFocus();
                } catch (SQLException ex){
                    ex.printStackTrace();
                }
            }
        });
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try{
                    String empid = txtid.getText();

                    pst = con.prepareStatement("SELECT empname, salary, mobile FROM employee WHERE id = ?");
                    pst.setInt(1, Integer.parseInt(empid));
                    ResultSet rs = pst.executeQuery();

                    if(rs.next()==true){

                        String empname = rs.getString(1);
                        String emsalary = rs.getString(2);
                        String emmobile = rs.getString(3);

                        txtName.setText(empname);
                        txtSalary.setText(emsalary);
                        txtMobile.setText(emmobile);
                    }
                    else {
                        txtName.setText("");
                        txtSalary.setText("");
                        txtMobile.setText("");
                        JOptionPane.showMessageDialog(null, "Invalid employee number!");
                    }
                } catch (SQLException ex){
                    ex.printStackTrace();
                }
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String empid, empname, salary, mobile;

                empid = txtid.getText();
                empname = txtName.getText();
                salary = txtSalary.getText();
                mobile = txtMobile.getText();


                try {
                    pst = con.prepareStatement("update employee set empname = ?, salary = ?, mobile = ? where id = ?");
                    pst.setString(1, empname);
                    pst.setString(2, salary);
                    pst.setString(3, mobile);
                    pst.setInt(4, Integer.parseInt(empid));

                    pst.executeUpdate();

                    JOptionPane.showMessageDialog(null, "Record update!");
                    table_load();
                    txtName.setText("");
                    txtSalary.setText("");
                    txtMobile.setText("");
                    txtName.requestFocus();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String empid;

                empid = txtid.getText();

                try{
                    pst = con.prepareStatement("delete from employee where id = ?");

                    pst.setInt(1, Integer.parseInt(empid));

                    pst.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Record deleted!");
                    table_load();
                    txtName.setText("");
                    txtSalary.setText("");
                    txtMobile.setText("");
                    txtName.requestFocus();

                } catch (SQLException ex){
                    ex.printStackTrace();
                }
            }
        });
    }

    void table_load() {
        try {
            pst = con.prepareStatement("SELECT * FROM employee");
            ResultSet rs = pst.executeQuery();

            // Get column names
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
            }

            // Create DefaultTableModel with column names
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            // Add rows to the model
            while (rs.next()) {
                Object[] rowData = new Object[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    rowData[i] = rs.getObject(i + 1);
                }
                model.addRow(rowData);
            }

            // Set the model on your table
            table1.setModel(model);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
