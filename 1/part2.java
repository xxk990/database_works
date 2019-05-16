//Ke Xu

//This is the program for part2. Before execute the program you need to modify the username and password to yours.
//I use a hashMap to store all the information I need. I use the combination of customer and product as a key in the map.
//In the hashMap there are two function called equals(object obj) and hashcode() if there are exist two objects are same, they will have same
//hashcode. So it is easy to check each combination object in the map is different or not. I use this way to create a map for each different combination.
//Then I set the combination(custProd) as key or pointer in the map, this will easy to find any element in this map. Thus, I use hashMap for his part.
//For the max and min I initial them to a value(max = smallest value and min = biggest value from the database. Max: if the quant. > Max.
//then set Max = quant. Min: if quant < Min. then set Min. = quant. until scan every line in the database.(in different state : NJ, NY and CT)
//object 'sales' is I created to store the information except customer name.
//object 'custProd' is I created to store each different combination between customer and product.

import java.sql.*;
import java.util.*;

public class part2 {
    // object to store all information
    static class sales {
        // NJ_Max
        int NJMaxQuant;
        int NJMaxMonth;
        int NJMaxDay;
        int NJMaxYear;
        boolean NJHasValue = false;     //check the value exist or not

        // NY_Min
        int NYMinQuant;
        int NYMinMonth;
        int NYMinDay;
        int NYMinYear;
        boolean NYHasValue = false;     //check the value exist or not

        // CT_Min
        int CTMinQuant;
        int CTMinMonth;
        int CTMinDay;
        int CTMinYear;
        boolean CTHasValue = false;     //check the value exist or not

        //print object
        public String toString() {
            String s = "";
            String NJdate = String.format("%02d", NJMaxMonth)+ "/" +String.format("%02d", NJMaxDay) + "/" + NJMaxYear;
            String NYdate = String.format("%02d", NYMinMonth)+ "/" +String.format("%02d", NYMinDay) + "/" + NYMinYear;
            String CTdate = String.format("%02d", CTMinMonth)+ "/" +String.format("%02d", CTMinDay) + "/" + CTMinYear;
            //If the 'hasValue' is false(that mean there is no value) print null
            if(!NJHasValue) {
                s = String.format("%5d", null) + " " + String.format("%02d", null)+ "/" +String.format("%02d", null) + "/" + "null";
            }else if(NJHasValue)
                s = String.format("%5d", NJMaxQuant) + "  " + NJdate;
            if(!NYHasValue) {
                s = s+ String.format("%10d", null) + " " + String.format("%02d", null)+ "/" +String.format("%02d", null) + "/" + "null";
            }else if(NYHasValue)
                s = s +String.format("%10d", NYMinQuant) + "  " + NYdate;
            if(!CTHasValue) {
                s = s+String.format("%10d", null) + " " + String.format("%02d", null)+ "/" +String.format("%02d", null) + "/" + "null";
            }else if(CTHasValue)
                s = s+String.format("%10d", CTMinQuant) + "  " + CTdate;

            return s;
        }
    }

    // product and customer combination
    static class custProd {
        String customer;
        String product;

        //find the hashcode for the combination
        public int hashCode() {
            return (customer.hashCode() + product.hashCode());
        }

        //check the combination of the product and customer is same or not
        public boolean equals(Object obj) {
            if (obj instanceof custProd) {
                custProd combi1 = (custProd)obj;
                if (customer.equals(combi1.customer) && product.equals(combi1.product))
                    return true;
            }
            return false;
        }
    }

    public static void main(String[] args) {
        String usr = "postgres";
        String pwd = "dante110";
        String url = "jdbc:postgresql://localhost:5432/postgres";
        // the map to store the final result
        HashMap<custProd, sales> salesMap = new HashMap<custProd, sales>();
        //label
        String label = String.format("%-11s|", "Customer")+  String.format("%-10s|", "Product") + String.format("%-8s|", "NJ_Max")
                + String.format("%-10s|", "NJ_Date") + String.format("%-8s|", "NY_Min")
                + String.format("%-13s|", "NY_Date")+ String.format("%-7s|", "CT_Min")
                + String.format("%-10s", "CT_date") ;


        try {
            Class.forName("org.postgresql.Driver");
            System.out.println("Success loading Driver!");
        } catch (Exception e) {
            System.out.println("Fail loading Driver!");
            e.printStackTrace();
        }

        try {
            Connection conn = DriverManager.getConnection(url, usr, pwd);
            System.out.println("Success connecting server!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Sales");


            while (rs.next()) {
                sales sales1;

                String customer = rs.getString("cust");
                String product = rs.getString("prod");

                //create a combination
                custProd comb = new custProd();
                comb.product = product;
                comb.customer = customer;

                //if the map doesn't contain the combination, create one .
                if (!salesMap.containsKey(comb)) {
                    sales1 = new sales();
                    sales1.NJMaxQuant = 0;                   //set NJMax to be a smallest value
                    sales1.NYMinQuant = 9999999;            //set NYMin to be a biggest value
                    sales1.CTMinQuant = 9999999;            //set CTMin to be a biggest value
                    salesMap.put(comb, sales1);
                } else {
                    sales1 = salesMap.get(comb);
                }

                int year = rs.getInt("year");
                int month = rs.getInt("month");
                int day = rs.getInt("day");
                int quant = rs.getInt("quant");
                String state = rs.getString("state");

                // find the maximum in NJ before 2009 for every combination
                if (state.equals("NJ") && quant > sales1.NJMaxQuant && year < 2009) {
                    sales1.NJMaxQuant = quant;
                    sales1.NJMaxDay = day;
                    sales1.NJMaxMonth = month;
                    sales1.NJMaxYear = year;
                    //set the value check to be true
                    sales1.NJHasValue = true;
                }
                // find the minimum in NY before 2009 for every combination
                if (state.equals("NY") && quant < sales1.NYMinQuant && year < 2009) {
                    sales1.NYMinQuant = quant;
                    sales1.NYMinDay = day;
                    sales1.NYMinMonth = month;
                    sales1.NYMinYear = year;
                    //set the value check to be true
                    sales1.NYHasValue = true;
                }
                // find the minimum in CT for every combination
                if (state.equals("CT") && quant < sales1.CTMinQuant) {
                    sales1.CTMinQuant = quant;
                    sales1.CTMinDay = day;
                    sales1.CTMinMonth = month;
                    sales1.CTMinYear = year;
                    //set the value check to be true
                    sales1.CTHasValue = true;
                }

            }
            System.out.println(label);
            //print the result as a table
            for (custProd key : salesMap.keySet()) {
                sales value = salesMap.get(key);
                System.out.println(String.format("%-10s", key.customer) + " " + String.format("%-10s", key.product) + " " + value.toString());
            }

        } catch (SQLException e) {
            System.out.println("Connection URL or username or password errors!");
            e.printStackTrace();
        }

    }

}
