package com.fox.TEO;

public class User {
    private String name;
    private double money;
    private int cellPosition = 0;
    public boolean bot = false;

    // Laps bonus
    private static final int LAPBONUS = 1000;

    public int getCellPosition(){
        return cellPosition;
    }

    public void setCellPosition(int cellPosition){
        this.cellPosition = cellPosition;
    }

    public int addCellPosition(int n, int limit){
        cellPosition += n;
        if (cellPosition >= limit){
            cellPosition %= limit;
            System.out.printf("New LAP [%s] + "+ LAPBONUS + "$\n", name);
            this.money += LAPBONUS;
        }

        return cellPosition;
    }

    public boolean isUserLosed(){
        return (money <= 0);
    }

    public User(String name, double money){
        this.name = name;
        this.money = money;
    }

    public double getMoney(){
        return money;
    }

    public double summMoney(double num){
        return money += num;
    }

    public String getName(){
        return name;
    }

    public boolean hasEnoughMoney(double money){
        return (this.money >= money);
    }
}
