package project;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.io.*;
import java.util.Scanner;
import java.time.LocalDate;

class MenuItem 
{
    double price;
    int stock;

    public MenuItem(double price, int stock) 
    {
        this.price = price;
        this.stock = stock;
    }

    public double getPrice() 
    {
        return price;
    }

    public int getStock() 
    {
        return stock;
    }

    public void addStock(int amount) 
    {
        this.stock += amount;
    }

    public void reduceStock(int amount) 
    {
        this.stock -= amount;
    }
}

class Order 
{
    String orderId;
    String date;
    double totalAmount;

    public Order(String orderId, double totalAmount, String date) 
    {
        this.orderId = orderId;
        this.totalAmount = totalAmount;
        this.date = date;
    }

    public String getOrderId() 
    {
        return orderId;
    }

    public double getTotalAmount() 
    {
        return totalAmount;
    }

    public String getDate() 
    {
        return date;
    }
}

public class Main extends JFrame 
{
    private JPanel contentPane;
    private JTable table;
    
    ArrayList<Order> salesData;
    Map<String, MenuItem> menuItems;
    Map<String, String> tableStatus;
    Map<String, Double> tableOrders;
        
    public Main() 
    {
    	setBackground(new Color(0, 0, 128));
    	setTitle("F&B Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 600, 503);

        //initialize data
        menuItems = new HashMap<>();
        ReadMenuFromFile();

        salesData = new ArrayList<>();
        
        tableStatus = new HashMap<>();
        for (int i = 1; i <= 7; i++) 
        {
            tableStatus.put(String.valueOf(i), "Available");
        }
        
        tableOrders = new HashMap<>();
        for (int i = 1; i <= 7; i++)
        {
            tableOrders.put(String.valueOf(i), 0.0); //set all tables with $0.0 total.
        }
        
        ReadSalesFromFile();
        
        //setup menu bar
        JMenuBar menuBar = new JMenuBar();
        menuBar.setMargin(new Insets(5, 5, 5, 5));
        menuBar.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        menuBar.setBackground(new Color(128, 128, 128));
        setJMenuBar(menuBar);

        JMenu menuDisplay = new JMenu("Display");
        menuDisplay.setFont(new Font("Cambria", Font.BOLD, 15));
        menuDisplay.setForeground(new Color(0, 0, 0));
        menuDisplay.setBackground(new Color(0, 0, 0));
        menuBar.add(menuDisplay);

        JMenuItem listTables = new JMenuItem("List Tables");
        listTables.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        listTables.setBackground(new Color(192, 192, 192));
        listTables.setForeground(new Color(0, 0, 0));
        menuDisplay.add(listTables);

        JMenuItem listMenu = new JMenuItem("List Menu");
        listMenu.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        listMenu.setForeground(new Color(0, 0, 0));
        listMenu.setBackground(new Color(192, 192, 192));
        menuDisplay.add(listMenu);

        JMenuItem salesReport = new JMenuItem("Sales Report");
        salesReport.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        salesReport.setForeground(new Color(0, 0, 0));
        salesReport.setBackground(new Color(192, 192, 192));
        menuDisplay.add(salesReport);

        JMenu menuActions = new JMenu("Actions");
        menuActions.setFont(new Font("Cambria", Font.BOLD, 15));
        menuActions.setForeground(new Color(0, 0, 0));
        menuBar.add(menuActions);

        JMenuItem placeOrder = new JMenuItem("Place Order");
        placeOrder.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        placeOrder.setBackground(new Color(192, 192, 192));
        placeOrder.setForeground(new Color(0, 0, 0));
        menuActions.add(placeOrder);

        JMenuItem addStock = new JMenuItem("Add Stock");
        addStock.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        addStock.setBackground(new Color(192, 192, 192));
        addStock.setForeground(new Color(0, 0, 0));
        menuActions.add(addStock);

        JMenuItem addNewMenu = new JMenuItem("Add New Menu");
        addNewMenu.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        addNewMenu.setBackground(new Color(192, 192, 192));
        addNewMenu.setForeground(new Color(0, 0, 0));
        menuActions.add(addNewMenu);

        JMenu menuManage = new JMenu("Manage");
        menuManage.setFont(new Font("Cambria", Font.BOLD, 15));
        menuManage.setForeground(new Color(0, 0, 0));
        menuBar.add(menuManage);

        JMenuItem checkoutTable = new JMenuItem("Checkout Table");
        checkoutTable.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        checkoutTable.setBackground(new Color(192, 192, 192));
        checkoutTable.setForeground(new Color(0, 0, 0));
        menuManage.add(checkoutTable);

        contentPane = new JPanel();
        contentPane.setBackground(new Color(0, 0, 102));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(5, 5, 574, 426);
        contentPane.add(scrollPane);

        table = new JTable();
        table.setFont(new Font("Tahoma", Font.PLAIN, 11));
        table.setBackground(new Color(255, 255, 255));
        scrollPane.setViewportView(table);

        //display status of tables
        listTables.addActionListener(e -> 
        {
            String[] columnNames = {"Table Number", "Status"};
            Object[][] data = new Object[tableStatus.size()][2];
            int index = 0;
            for (Map.Entry<String, String> entry : tableStatus.entrySet()) 
            {
                data[index][0] = entry.getKey();
                data[index][1] = entry.getValue();
                index++;
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));//setModel used to refreshed the content
        });

        //display the list menu
        listMenu.addActionListener(e -> 
        {
            String[] columnNames = {"Menu Item", "Price", "Stock"};
            Object[][] data = new Object[menuItems.size()][3];
            int index = 0;
            for (Map.Entry<String, MenuItem> entry : menuItems.entrySet()) 
            {
                data[index][0] = entry.getKey();
                data[index][1] = "$" + String.format("%.2f", entry.getValue().getPrice());
                data[index][2] = entry.getValue().getStock();
                index++;
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        });

        //display sales report in table
        salesReport.addActionListener(e -> 
        {
            String[] columnNames = {"Order ID", "Total Amount", "Date"};
            Object[][] data = new Object[salesData.size()][3];
            int index = 0;
            for (Order order : salesData) 
            {
                data[index][0] = order.getOrderId();
                data[index][1] = "$" + order.getTotalAmount();
                data[index][2] = order.getDate();
                index++;
            }
            table.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        });
        
        //placing order
        placeOrder.addActionListener(e -> {
            String tableNumber = JOptionPane.showInputDialog("Enter Table Number:");
            
            //containsKey is to check whether specific key is exist or not
            if (tableNumber == null || !tableStatus.containsKey(tableNumber) || tableStatus.get(tableNumber).equals("Occupied")) 
            {
                JOptionPane.showMessageDialog(null, "Invalid or occupied table.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JPanel menuPanel = new JPanel();
            menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
            JCheckBox[] checkBoxes = new JCheckBox[menuItems.size()];
            int i = 0;
               //what to display : iterates over what
            for (String item : menuItems.keySet()) 
            {
                checkBoxes[i] = new JCheckBox(item + " - $" + menuItems.get(item).getPrice());
                menuPanel.add(checkBoxes[i]);
                i++;
            }

            //tak faham camne checkbox boleh combine dengan confirm dialog
            int option = JOptionPane.showConfirmDialog(null, menuPanel, "Select Menu Items", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            StringBuilder orderDetails = new StringBuilder("Order for Table " + tableNumber + ":\n");
            double total = 0.0;
            for (i = 0; i < checkBoxes.length; i++) 
            {
                if (checkBoxes[i].isSelected()) 
                {
                    String item = checkBoxes[i].getText().split(" - ")[0];
                    MenuItem menuItem = menuItems.get(item);
                    if (menuItem.getStock() > 0) 
                    {
                        orderDetails.append(item).append("\n");
                        total += menuItem.getPrice();
                        menuItem.reduceStock(1);
                        WriteMenuToFile(); //update stock to MenuFile
                    } 
                    else 
                    {
                        JOptionPane.showMessageDialog(null, item + " is out of stock.", "Stock Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            
            if (total > 0) //if order placed, the Sale Report will be updated 
            {
                String orderId = String.valueOf(salesData.size() + 1);
                
                LocalDate currentDate = LocalDate.now();
                String dateString = currentDate.toString();
                
                salesData.add(new Order(orderId, total, dateString));
                tableStatus.put(tableNumber, "Occupied");
                double currentTableTotal = tableOrders.getOrDefault(tableNumber, 0.0);
                tableOrders.put(tableNumber, currentTableTotal + total); //update the table's total.

                WriteSalesToFile(); //update sales to SaleFile

                JOptionPane.showMessageDialog(null, orderDetails.append("\nTotal: $").append(total).toString(), "Order Summary", JOptionPane.INFORMATION_MESSAGE);
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "No valid items selected.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        //adding stock
        addStock.addActionListener(e -> {
            String item = JOptionPane.showInputDialog("Enter Menu Item:");
            if (menuItems.containsKey(item)) {
                String amountStr = JOptionPane.showInputDialog("Enter Amount to Add:");
                try {
                    int amount = Integer.parseInt(amountStr);
                    menuItems.get(item).addStock(amount);
                    WriteMenuToFile(); //update stock to MenuFile
                    JOptionPane.showMessageDialog(null, "Stock Updated.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Item not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        //adding new item in menu
        addNewMenu.addActionListener(e -> 
        {
            String item = JOptionPane.showInputDialog("Enter New Menu Item:");
            if (item != null && !item.isEmpty()) 
            {
                try 
                {
                	double price = Double.parseDouble(JOptionPane.showInputDialog("Enter Price:"));
                    int stock = Integer.parseInt(JOptionPane.showInputDialog("Enter Stock:"));
                    menuItems.put(item, new MenuItem(price, stock));
                    WriteMenuToFile(); //update the new menu to MenuFile
                    JOptionPane.showMessageDialog(null, "Item Added Successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                } 
                catch (NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(null, "Invalid input.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        //checkout occupied table
        checkoutTable.addActionListener(e -> 
        {
            String tableNumber = JOptionPane.showInputDialog("Enter Table Number to Checkout:");
            if (tableNumber != null && tableStatus.containsKey(tableNumber) && tableStatus.get(tableNumber).equals("Occupied")) 
            {
                double total = tableOrders.getOrDefault(tableNumber, 0.0);
                tableOrders.put(tableNumber, 0.0); //reset the table's total back to $0.0
                tableStatus.put(tableNumber, "Available"); //mark the table back as available
                JOptionPane.showMessageDialog(null, "Table " + tableNumber + " has been checked out. Total: $" + total, "Checkout Complete", JOptionPane.INFORMATION_MESSAGE);
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "Invalid table number or table not occupied.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    //List menu Read and Write
    public void ReadMenuFromFile() 
    {
        File menuFile = new File("C:\\Users\\User\\Desktop\\MenuFile.txt");
        try (Scanner scanner = new Scanner(menuFile)) 
        {
            while (scanner.hasNextLine()) 
            {
                String[] line = scanner.nextLine().split(",");
                String itemName = line[0].trim();
                double price = Double.parseDouble(line[1].trim());
                int stock = Integer.parseInt(line[2].trim());
                menuItems.put(itemName, new MenuItem(price, stock));
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Error reading menu file: " + e.getMessage());
        }
    }
    
    public void WriteMenuToFile() 
    {
        try 
        {
            PrintWriter menuWriter = new PrintWriter(new FileWriter("C:\\Users\\User\\Desktop\\MenuFile.txt"));
            for (Map.Entry<String, MenuItem> entry : menuItems.entrySet()) 
            {
                menuWriter.println(entry.getKey() + "," + entry.getValue().getPrice() + "," + entry.getValue().getStock());
            }
            menuWriter.close();

        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    //Sales data Read and Write
    public void ReadSalesFromFile() 
    {
        try 
        {
            File salesFile = new File("C:\\Users\\User\\Desktop\\SalesFile.txt");
            if (salesFile.exists()) 
            {
                Scanner scanner = new Scanner(salesFile);
                while (scanner.hasNextLine()) 
                {
                    String[] line = scanner.nextLine().split(",");
                    String orderId = line[0];
                    double totalAmount = Double.parseDouble(line[1]);
                    String date = line[2];
                    salesData.add(new Order(orderId, totalAmount, date)); // Add as double
                }
                scanner.close();
            }
        } 
        catch (IOException e) 
        {
            System.out.println("Sales data file not found, starting fresh.");
        }
    }


    private void WriteSalesToFile() 
    {
        try 
        {
            PrintWriter salesWriter = new PrintWriter(new FileWriter("C:\\Users\\User\\Desktop\\SalesFile.txt"));
            for (Order order : salesData) 
            {
                salesWriter.println(order.getOrderId() + "," + order.getTotalAmount() + "," + order.getDate());
            }
            salesWriter.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
    
    //starting a GUI
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Main frame = new Main();
                frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
}