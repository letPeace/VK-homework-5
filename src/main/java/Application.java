import database.JDBCCredentials;
import database.MigrationsInitializer;
import dao.*;
import entities.*;
import org.jetbrains.annotations.NotNull;
import utils.Date;
import utils.Pair;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class Application {

    public static void main(@NotNull String[] args) {
        MigrationsInitializer.initialize();
        try (var connection = JDBCCredentials.getDefaultConnection()) {
            /* 1 */
            query1(connection);
            /* 2 */
            query2(connection);
            /* 3 */
            query3(connection);
            /* 4 */
            query4(connection);
            /* 5 */
            query5(connection);
            /*   */
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    private static void query1(@NotNull Connection connection){
        System.out.println("\n1) Выбрать первые 10 поставщиков по количеству поставленного товара.");
        final OrganizationDAO organizationDAO = new OrganizationDAO(connection);
        final int code = 2;
        final int limit = 10;
        organizationDAO.getOrganizationsSuppliedProduct(code, limit).forEach((product, sum) -> System.out.println(product+" -> "+sum));
    }

    private static void query2(@NotNull Connection connection){
        System.out.println("\n2) Выбрать поставщиков с количеством поставленного товара выше указанного значения (товар и его количество должны допускать множественное указание).");
        final OrganizationDAO organizationDAO = new OrganizationDAO(connection);
        final Map<Integer, Integer> map = new HashMap<>();
        map.put(1, 0);
        map.put(2, 1);
        map.put(3, 2);
        organizationDAO.getOrganizationsSuppliedProductsMoreQuantity(map).forEach((product, sum) -> System.out.println(product+" -> "+sum));
    }

    private static void query3(@NotNull Connection connection){
        System.out.println("\n3) За каждый день для каждого товара рассчитать количество и сумму полученного товара в указанном периоде, посчитать итоги за период.");
        final ProductDAO productDAO = new ProductDAO(connection);
        final Date start = new Date(1);
        final Date end = new Date(120000000);
        final Map<Date, Map<Product, Pair<Integer, Integer>>> mapWithDate = productDAO.getProductQuantityAndTotalWithDate(start, end);
        mapWithDate.forEach((date, map) -> System.out.println(date+" -> "+map));
        final Map<Product, Pair<Integer, Integer>> mapProducts = new HashMap<>();
        for(var mapWithDateNested : mapWithDate.values()){
            for(Product product : mapWithDateNested.keySet()){
                var currentPair = mapWithDateNested.get(product);
                var pair = mapProducts.getOrDefault(product, new Pair<>(0, 0));
                mapProducts.put(product, new Pair<>(currentPair.value1()+pair.value1(), currentPair.value2()+pair.value2()));
            }
        }
        mapProducts.forEach((product, pair) -> System.out.println(product+" -> "+pair));
    }

    private static void query4(@NotNull Connection connection){
        System.out.println("\n4) Рассчитать среднюю цену по каждому товару за период.");
        final ProductDAO productDAO = new ProductDAO(connection);
        final Date start = new Date(1);
        final Date end = new Date(120000000);
        productDAO.getProductQuantityAndTotal(start, end).forEach((product, average) -> System.out.println(product+" -> "+average));
    }

    private static void query5(@NotNull Connection connection){
        System.out.println("\n5) Вывести список товаров, поставленных организациями за период. Если организация товары не поставляла, то она все равно должна быть отражена в списке.");
        final OrganizationDAO organizationDAO = new OrganizationDAO(connection);
        final Date start = new Date(1);
        final Date end = new Date(120000000);
        organizationDAO.getOrganizationsWithSuppliedProducts(start, end).forEach((organization, list) -> System.out.println(organization+" -> "+list));
    }

}
