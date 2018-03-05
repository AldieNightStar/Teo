package com.fox.TEO;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Game {

    private Table table;
    private static String cellsFile = "./cells.txt";

    public Game(User...users){
        // Load Cells from file
        table = new Table(loadCells(), users);
    }

    public void play(){
        table.play(-1);
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.println("");
            System.out.println("");
            System.out.print("Number: ");
            int number;
            try {
                number = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("Number error!");
                continue;
            }
            table.play(number);
        }

    }

    public static User[] loadUsers(){
        List<User> userList = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get("./users.txt"));
            for (String line : lines){
                String[] arr = splitTwoString(line);
                String userName = arr[0];
                double userMoney = Double.valueOf(arr[1]);
                User user = new User(userName, userMoney);
                userList.add(user);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return userList.toArray(new User[0]);
    }

    public static Table.Cell[] loadCells(){
        List<Table.Cell> cells = new ArrayList<>();
        try {
            List<String> lines = Files.readAllLines(Paths.get(cellsFile));
            for (String line : lines){
                String[] strings = splitTwoString(line);
                cells.add(new Table.Cell(strings[0], Double.valueOf(strings[1])));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cells.toArray(new Table.Cell[0]);
    }

    private static String[] splitTwoString(String str){
        String str1 = str.substring(0, str.indexOf("=")).trim();
        String str2 = str.substring(str.indexOf("=")+1).trim();
        return new String[]{str1, str2, "error", "0"};
    }


    public static double percent(double number, double per){
        return (number / 100) * per;
    }
}
