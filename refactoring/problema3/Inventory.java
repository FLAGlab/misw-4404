package refactoring.problema3;

import refactoring.problema3.Product;
import refactoring.problema3.Sale;
import refactoring.problema3.Order;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Inventory {

    public static void main(String[] args) {
        String csvFileProducts = "./refactoring/problema3/data/products.csv";
        String csvFileSales = "./refactoring/problema3/data/sales.csv";
        String csvFileOrders = "./refactoring/problema3/data/orders.csv";

        System.out.println(csvFileProducts);
        String csvSplitBy = ",";

        ArrayList<Product> products = new ArrayList<Product>();
        ArrayList<Sale> sales = new ArrayList<Sale>();
        ArrayList<Order> orders = new ArrayList<Order>();

        try (BufferedReader br = new BufferedReader(new FileReader(csvFileProducts))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(csvSplitBy);

                // Access the product data
                int itemId = Integer.parseInt(data[0]);
                String item = data[1];
                int quantity = Integer.parseInt(data[2]);

                products.add(new Product(itemId, item, quantity));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFileSales))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                int saleId = Integer.parseInt(data[0].trim());
                String saleDate = data[1].trim();
                int itemId = Integer.parseInt(data[2].trim());
                int quantity = Integer.parseInt(data[3].trim());

                Sale sale = new Sale(saleId, saleDate, itemId, quantity);
                sales.add(sale);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader(csvFileOrders))) {
            String line = br.readLine();

            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                
                int orderId = Integer.parseInt(data[0].trim());
                String orderDate = data[1].trim();
                int itemId = Integer.parseInt(data[2].trim());
                int quantity = Integer.parseInt(data[3].trim());

                Order order = new Order(orderId, orderDate, itemId, quantity);
                orders.add(order);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Order order : orders) {
            Product item = products.get(order.getItemId());
            item.setQuantity(item.getQuantity() + order.getQuantity());
        }

        for (Sale sale : sales) {
            Product item = products.get(sale.getItemId());
            item.setQuantity(item.getQuantity() - sale.getQuantity());
        }

        for (Product product : products) {
            System.out.println(product.getItem() + " " + product.getQuantity());
        }

    }
}
