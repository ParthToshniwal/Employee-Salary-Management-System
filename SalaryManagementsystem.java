package com.mycompany.salarymanagementsystem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;

public class SalaryManagementsystem extends JFrame {

    private JTextField nameField, salaryField, departmentField, searchField;
    private DefaultTableModel tableModel;
    private JLabel averageLabel;
    private JTable table;
    private java.util.List<Employee> employees = new ArrayList<>();
    private static final String FILE_NAME = "employees.csv";

    public SalaryManagementsystem() {
        setTitle("Salary Management System");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new GridLayout(2, 5, 10, 10));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Employee"));

        inputPanel.add(new JLabel("Name:"));
        nameField = new JTextField();
        inputPanel.add(nameField);

        inputPanel.add(new JLabel("Salary:"));
        salaryField = new JTextField();
        inputPanel.add(salaryField);

        inputPanel.add(new JLabel("Department:"));
        departmentField = new JTextField();
        inputPanel.add(departmentField);

        JButton addButton = new JButton("Add Employee");
        inputPanel.add(addButton);

        JButton sortButton = new JButton("Sort by Salary");
        inputPanel.add(sortButton);

        JButton deleteButton = new JButton("Delete Employee");
        inputPanel.add(deleteButton);

        averageLabel = new JLabel("Average Salary: N/A");
        inputPanel.add(averageLabel);

        add(inputPanel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"Name", "Salary", "Department"}, 0);
        table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        searchField = new JTextField(10);
        JButton searchButton = new JButton("Search");
        JButton saveButton = new JButton("Save Data");
        JButton loadButton = new JButton("Load Data");

        bottomPanel.add(new JLabel("Search:"));
        bottomPanel.add(searchField);
        bottomPanel.add(searchButton);
        bottomPanel.add(saveButton);
        bottomPanel.add(loadButton);

        add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addEmployee());
        sortButton.addActionListener(e -> sortEmployees());
        deleteButton.addActionListener(e -> deleteEmployee());
        searchButton.addActionListener(e -> searchEmployee());
        saveButton.addActionListener(e -> saveData());
        loadButton.addActionListener(e -> loadData());

        setVisible(true);
    }

    private void addEmployee() {
        String name = nameField.getText().trim();
        String salaryText = salaryField.getText().trim();
        String department = departmentField.getText().trim();

        if (name.isEmpty() || salaryText.isEmpty() || department.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter all fields.");
            return;
        }

        try {
            double salary = Double.parseDouble(salaryText);
            Employee emp = new Employee(name, salary, department);
            employees.add(emp);
            tableModel.addRow(new Object[]{name, salary, department});
            updateAverage();

            nameField.setText("");
            salaryField.setText("");
            departmentField.setText("");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid salary.");
        }
    }

    private void sortEmployees() {
        employees.sort(Comparator.comparingDouble(Employee::getSalary));
        updateTable();
    }

    private void deleteEmployee() {
        int row = table.getSelectedRow();
        if (row != -1) {
            employees.remove(row);
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "Select employee first.");
        }
    }

    private void searchEmployee() {
        String key = searchField.getText().toLowerCase();
        for (int i = 0; i < employees.size(); i++) {
            if (employees.get(i).getName().toLowerCase().contains(key)) {
                table.setRowSelectionInterval(i, i);
                return;
            }
        }
        JOptionPane.showMessageDialog(this, "Not found.");
    }

    private void saveData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Employee e : employees) {
                bw.write(e.getName() + "," + e.getSalary() + "," + e.getDepartment());
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "Saved!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving.");
        }
    }

    private void loadData() {
        employees.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] p = line.split(",");
                employees.add(new Employee(p[0], Double.parseDouble(p[1]), p[2]));
            }
            updateTable();
            JOptionPane.showMessageDialog(this, "Loaded!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading.");
        }
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (Employee e : employees) {
            tableModel.addRow(new Object[]{e.getName(), e.getSalary(), e.getDepartment()});
        }
        updateAverage();
    }

    private void updateAverage() {
        if (employees.isEmpty()) {
            averageLabel.setText("Average Salary: N/A");
            return;
        }

        double sum = 0;
        for (Employee e : employees) sum += e.getSalary();
        averageLabel.setText("Average Salary: " + (sum / employees.size()));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(SalaryManagementsystem::new);
    }

    static class Employee {
        private String name;
        private double salary;
        private String department;

        public Employee(String n, double s, String d) {
            name = n;
            salary = s;
            department = d;
        }

        public String getName() { return name; }
        public double getSalary() { return salary; }
        public String getDepartment() { return department; }
    }
}