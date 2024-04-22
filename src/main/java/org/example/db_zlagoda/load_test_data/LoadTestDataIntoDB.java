package org.example.db_zlagoda.load_test_data;

import org.example.db_zlagoda.DatabaseConnection;

import java.lang.runtime.TemplateRuntime;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import static org.example.db_zlagoda.login_page.RegistrationController.*;

public class LoadTestDataIntoDB {

    public void main(String[] args) {
        clearDatabase("store_product");
//        employees();
//        categories();
//        customers();
//        products();
        productsInStore();
    }

    private final SecureRandom random = new SecureRandom();
    public void employees(){
        String[] lastNames = {"Петренко", "Іваненко", "Коваленко", "Сидоренко", "Мельник", "Ковальчук", "Павленко",
                "Кравченко", "Шевченко", "Бойко", "Кузьменко", "Шевчук", "Савченко", "Мороз", "Бондаренко", "Ткаченко",
                "Кучеренко", "Захаренко", "Коваленко", "Романенко"};
        String[] firstNames = {"Олександр", "Іван", "Софія", "Марія", "Максим", "Анна", "Віктор", "Ольга", "Артем", "Юлія",
                "Василь", "Катерина", "Дмитро", "Тетяна", "Анастасія", "Петро", "Христина", "Сергій", "Наталія", "Андрій"};
        String[] middleNames = {"Олександрович", "Іванович", "Володимирівна", "Петрівна", "Миколайович", "Андріївна",
                "Сергійович", "Ярославівна", "Артемович", "Дмитрівна", "Васильович", "Віталіївна", "Ігорович",
                "Олегівна", "Владиславівна", "Романович", "Тарасівна", "Богданович", "Григоріївна", "Максимович"};
        String[] roles = {"Менеджер", "Касир"};
        double[] salaries = {8000.0, 8200.0, 8500.0, 8700.0, 9000.0, 9200.0, 9400.0, 9600.0, 9800.0, 10000.0, 6000.0, 6200.0, 6400.0, 6600.0, 6800.0, 7000.0, 7200.0, 7400.0, 7600.0, 7800.0};
        LocalDate[] birthDates = {
                LocalDate.of(1990, 5, 15), LocalDate.of(1985, 8, 25), LocalDate.of(1987, 12, 10), LocalDate.of(1992, 3, 6),
                LocalDate.of(1988, 9, 18), LocalDate.of(1995, 2, 3), LocalDate.of(1983, 7, 22), LocalDate.of(1997, 11, 14),
                LocalDate.of(1991, 4, 30), LocalDate.of(1986, 10, 8), LocalDate.of(1993, 6, 12), LocalDate.of(1989, 1, 28),
                LocalDate.of(1994, 5, 7), LocalDate.of(1984, 11, 20), LocalDate.of(1999, 8, 2), LocalDate.of(1982, 12, 17),
                LocalDate.of(1996, 3, 25), LocalDate.of(1980, 6, 5), LocalDate.of(1981, 9, 9), LocalDate.of(1998, 1, 1)
        };
        LocalDate[] firstWorkDays = {
                LocalDate.of(2010, 9, 20), LocalDate.of(2012, 7, 14), LocalDate.of(2008, 11, 30), LocalDate.of(2015, 4, 5),
                LocalDate.of(2007, 3, 12), LocalDate.of(2018, 5, 28), LocalDate.of(2004, 10, 3), LocalDate.of(2017, 9, 16),
                LocalDate.of(2009, 8, 8), LocalDate.of(2013, 8, 21), LocalDate.of(2017, 6, 19), LocalDate.of(2016, 12, 10),
                LocalDate.of(2023, 1, 22), LocalDate.of(2019, 11, 7), LocalDate.of(2023, 7, 2), LocalDate.of(2014, 10, 14),
                LocalDate.of(2022, 4, 18), LocalDate.of(2020, 2, 29), LocalDate.of(2001, 9, 4), LocalDate.of(2021, 3, 8)
        };
        String[] phoneNumbers = {
                "+380661234567", "+380990123456", "+380951234567", "+380981234567", "+380664567890", "+380997654321",
                "+380955432109", "+380986543210", "+380669876543", "+380992345678", "+380954321098", "+380983456789",
                "+380667890123", "+380998765432", "+380958765432", "+380987654321", "+380662345678", "+380994567890",
                "+380953210987", "+380982109876"
        };
        String[] cities = {
                "Київ", "Харків", "Одеса", "Дніпро", "Львів", "Запоріжжя", "Кривий Ріг", "Миколаїв", "Вінниця", "Херсон",
                "Полтава", "Чернігів", "Черкаси", "Житомир", "Суми", "Івано-Франківськ", "Тернопіль", "Кропивницький",
                "Ужгород", "Маріуполь"
        };
        String[] streets = {
                "Вулиця Шевченка", "Проспект Незалежності", "Вулиця Лесі Українки", "Вулиця Воздвиженська", "Вулиця Тараса Шевченка",
                "Вулиця Івана Франка", "Проспект Гагаріна", "Вулиця Миру", "Проспект Коцюбинського", "Вулиця Арнаутська",
                "Вулиця Героїв Крут", "Вулиця Джеймса Мейса", "Вулиця Степана Бандери", "Вулиця Юрія Вороного", "Вулиця Київська",
                "Вулиця Незалежності", "Вулиця Гетьмана Мазепи", "Вулиця Вокзальна", "Вулиця Івана Франка", "Вулиця М. Грушевського"
        };
        String[] zipCodes = {
                "01001", "02000", "03000", "04000", "05000", "06000", "07000", "08000", "09000", "10000", "11000", "12000",
                "13000", "14000", "15000", "16000", "17000", "18000", "19000", "20000"
        };
        String[] usernames = {
                "oleksandr_petrenko", "ivan_ivanenko", "sofiia_kovalenko", "maria_sydorenko", "maxym_melnyk", "anna_kovalchuk",
                "viktor_pavlenko", "olha_kravchenko", "artem_shevchenko", "yuliia_boiko", "vasyl_kuzmenko", "kateryna_shevchuk",
                "dmytro_savchenko", "tetiana_moroz", "anastasiia_bondarenko", "petro_tkachenko", "hryhorii_kucherenko",
                "nataliia_zakharenko", "andrii_kovalenko", "serhii_romanenko"
        };
        String[] passwords = {
                "qwerty123", "password123", "123456789", "letmein", "welcome123", "abc123", "123456", "password1", "passw0rd",
                "admin123", "qwertyuiop", "iloveyou", "football", "1234567", "123123", "111111", "12345678", "qwerty",
                "password", "1234"
        };
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            for (int i = 0; i < lastNames.length; i++) {
                String id_gen = generateRandomId(10, false);
                int randomRole = random.nextInt(roles.length);

                String insertQuery = "INSERT INTO employee (id_employee, empl_surname, empl_name, empl_patronymic, empl_role, salary, date_of_birth, date_of_start, phone_number, city, street, zip_code) " +
                        "VALUES ('" + id_gen + "', '" + lastNames[i] + "', '" + firstNames[i] + "', '" + middleNames[i] + "', '" + roles[randomRole] + "', " + salaries[i] + ", '" +
                        birthDates[i] + "', '" + firstWorkDays[i] + "', '" + phoneNumbers[i] + "', '" + cities[i] + "', '" + streets[i] + "', '" + zipCodes[i] + "')";


                String insertLoginInfo = "INSERT INTO login_table (username, password, id_employee) " +
                        "VALUES ('" + usernames[i] + "', '" + hashPassword(passwords[i]) + "', '" + id_gen + "')";

                statement.executeUpdate(insertQuery);
                statement.executeUpdate(insertLoginInfo);
            }

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void categories() {
        String[] categories = {
                "Молочні продукти", "Овочі та фрукти", "Риба", "М\''ясо", "Прянощі", "Соуси", "Харчові добавки",
                "Хлібобулочні вироби", "Макаронні вироби", "Крупи", "Напої"};
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            for (int i = 0; i < categories.length; i++) {
                String id_gen = generateRandomId(5, true);

                String insertQuery = "INSERT INTO category (category_number, category_name) " +
                        "VALUES ('" + id_gen + "', '" + categories[i] + "')";
                statement.executeUpdate(insertQuery);
            }

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void customers(){
        String[] surnames = {"Іванов", "Петрова", "Сидоров", "Коваленко", "Мельник",
                "Ковальчук", "Бойко", "Коваль", "Лисенко", "Мороз",
                "Шевченко", "Кравчук", "Бондаренко", "Кучер", "Ткач",
                "Савченко", "Павленко", "Іщенко", "Захарченко", "Грищенко"};

        String[] names = {"Олег", "Анна", "Михайло", "Ірина", "Віктор",
                "Оксана", "Андрій", "Марія", "Василь", "Тетяна",
                "Ігор", "Олена", "Сергій", "Наталія", "Дмитро",
                "Надія", "Павло", "Тетяна", "Максим", "Юлія"};

        String[] patronymics = {"Олександрович", "Вікторівна", "Іванович", "Михайлівна", "Васильович",
                "Анатоліївна", "Петрович", "Василівна", "Іванович", "Миколаївна",
                "Олександрович", "Сергіївна", "Олександрович", "Вікторівна", "Васильович",
                "Олександрівна", "Павлович", "Віталіївна", "Олександрович", "Вікторівна"};

        String[] phone_numbers = {"+380501234567", "+380671234567", "+380631234567", "+380681234567", "+380991234567",
                "+380961234567", "+380931234567", "+380991234567", "+380931234567", "+380501234567",
                "+380681234567", "+380501234567", "+380631234567", "+380961234567", "+380991234567",
                "+380671234567", "+380991234567", "+380501234567", "+380681234567", "+380931234567"};

        String[] cities = {"Київ", "Харків", "Львів", "Одеса", "Дніпро",
                "Запоріжжя", "Івано-Франківськ", "Кропивницький", "Херсон", "Черкаси",
                "Полтава", "Чернігів", "Вінниця", "Житомир", "Суми",
                "Тернопіль", "Рівне", "Луцьк", "Миколаїв", "Ужгород"};

        String[] streets = {"Центральна", "Соборна", "Лесі Українки", "Гагаріна", "Пушкінська",
                "Шевченка", "Бандери", "Шевченка", "Шевченка", "Проспект Миру",
                "Київська", "Леніна", "Володимирська", "Шевченка", "Грушевського",
                "Сагайдачного", "Соборна", "Карпатська", "Велика Васильківська", "Корзо"};

        String[] zip_codes = {"01001", "61001", "79000", "65000", "49000",
                "69000", "76000", "25000", "73000", "18000",
                "36000", "14000", "21000", "10000", "40000",
                "46000", "33000", "43000", "54000", "88000"};

        double[] percent = {10.0, 5.0, 15.0, 20.0, 10.0, 7.5, 12.0, 8.0, 10.0, 5.0, 10.0, 7.5, 10.0, 15.0, 10.0,
                5.0, 10.0, 7.5, 10.0, 12.0};

        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            for (int i = 0; i < surnames.length; i++) {
                String id_gen = generateRandomId(10, false);

                String insertQuery = "INSERT INTO customer_card (card_number, cust_surname, cust_name, cust_patronymic, phone_number, city, street, zip_code, percent) " +
                        "VALUES ('" + id_gen + "', '" + surnames[i] + "', '" + names[i] + "', '" + patronymics[i] + "', '" + phone_numbers[i] + "', '" +
                        cities[i] + "', '" + streets[i] + "', '" + zip_codes[i] + "', '" + percent[i] + "')";

                statement.executeUpdate(insertQuery);
            }

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void products(){
        String[] productNames = {
                "Молоко \"Добробут\", 3,2%", "Кефір \"Здоров''я\", 2,5%", "Сир \"Пирятинський\", 45%",
                "Сметана \"Смакота\", 20%", "Йогурт \"Натуральний\", 2,8%", "Масло вершкове \"Молокія\", 82%",
                "Кефір пісочний \"Сонечко\", 2,8%", "Сир \"Бринза\", 25%", "Йогурт з фруктовими додатками \"Фрутелла\", 4,5%",
                "Ряжанка \"Традиція\", 2,2%"
        };

        String[] productCharacteristics = {
                "Об''єм - 1 літр", "Об''єм - 900 мл", "Вага - 200 г", "Об''єм - 400 г", "Об''єм - 150 г",
                "Вага - 200 г", "Об''єм - 1 літр", "Вага - 250 г", "Об''єм - 200 г", "Об''єм - 500 мл"
        };

        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            ResultSet id_category = statement.executeQuery(
                    "SELECT category_number " +
                    "FROM category " +
                    "WHERE category_name = '" + "Молочні продукти" + "'");
            int categoryNum = -1;
            if (id_category.next()) {
                categoryNum = id_category.getInt("category_number");
            }
            products(productNames, productCharacteristics, categoryNum);


            productNames = new String[]{
                    "Яблука Голден", "Помідори Черрі", "Банани", "Огірки", "Морква",
                    "Картопля Нова", "Помаранчі", "Брокколі", "Кавуни", "Цибуля"
            };
            productCharacteristics = new String[]{
                    "Вага - 1 кг", "Вага - 250 г", "Вага - 500 г", "Вага - 300 г", "Вага - 1 кг",
                    "Вага - 2 кг", "Вага - 1 кг", "Вага - 500 г", "Вага - 1 шт", "Вага - 500 г"
            };

            id_category = statement.executeQuery(
                    "SELECT category_number " +
                            "FROM category " +
                            "WHERE category_name = '" + "Овочі та фрукти" + "'");
            categoryNum = -1;
            if (id_category.next()) {
                categoryNum = id_category.getInt("category_number");
            }
            products(productNames, productCharacteristics, categoryNum);

            productNames = new String[]{
                    "Сом", "Лосось", "Тріска", "Окунь", "Скумбрія", "Судак", "Хек", "Морська форель", "Кета", "Ставрида"
            };
            productCharacteristics = new String[]{
                    "Вага - 1 кг", "Вага - 500 г", "Вага - 800 г", "Вага - 600 г", "Вага - 700 г",
                    "Вага - 400 г", "Вага - 1.5 кг", "Вага - 900 г", "Вага - 1 шт", "Вага - 550 г"
            };

            id_category = statement.executeQuery(
                    "SELECT category_number " +
                            "FROM category " +
                            "WHERE category_name = '" + "Риба" + "'");
            categoryNum = -1;
            if (id_category.next()) {
                categoryNum = id_category.getInt("category_number");
            }
            products(productNames, productCharacteristics, categoryNum);

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void products(String[] names, String[] characteristics, int categoryNum){
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();

            for (int i = 0; i < names.length; i++) {
                String id_gen = generateRandomId(9, true);

                String insertQuery = "INSERT INTO product (id_product, category_number, product_name, characteristics) " +
                        "VALUES ('" + id_gen + "', '" + categoryNum + "', '" + names[i] + "', '" + characteristics[i] +  "')";

                statement.executeUpdate(insertQuery);
            }

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void productsInStore(){
        try {
            DatabaseConnection connection = new DatabaseConnection();
            Connection connectDB = connection.getConnection();
            Statement statement = connectDB.createStatement();


            String insertQuery = "INSERT INTO store_product (UPC, UPC_prom, id_product, selling_price, products_number, promotional_product) " +
                    "VALUES ('" + 111111111 + "', null, '" + "300617681" + "', '" + 700 + "', '" + 15 + "', '" +
                    0 + "')";

            statement.executeUpdate(insertQuery);

            insertQuery = "INSERT INTO store_product (UPC, UPC_prom, id_product, selling_price, products_number, promotional_product) " +
                    "VALUES ('" + 22222222 + "', null, '" + "71435173" + "', '" + 200 + "', '" + 222 + "', '" +
                    1 + "')";

            statement.executeUpdate(insertQuery);

            insertQuery = "INSERT INTO store_product (UPC, UPC_prom, id_product, selling_price, products_number, promotional_product) " +
                    "VALUES ('" + 21212121 + "', null, '" + "71435173" + "', '" + 250 + "', '" + 13 + "', '" +
                    0 + "')";

            statement.executeUpdate(insertQuery);
//            for (int i = 0; i < surnames.length; i++) {
//                String id_gen = generateRandomId(10, false);
//
//
//            }

            statement.close();
            connectDB.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearDatabase(String name) {
        DatabaseConnection connection = new DatabaseConnection();
        Connection connectDB = connection.getConnection();

        try {
            String clearDatabaseQuery = "DELETE FROM " + name;

            Statement statement = connectDB.createStatement();
            statement.executeUpdate(clearDatabaseQuery);

        } catch (SQLException error) {
            error.printStackTrace();
        } finally {
            try {
                connectDB.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}