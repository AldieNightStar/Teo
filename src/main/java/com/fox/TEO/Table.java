package com.fox.TEO;

import com.fox.Dice.Dice;

import static com.fox.TEO.Game.*;

public class Table {
    private Cell[] cells;
    private int size;
    private User[] users;
    private int turn;

    // Constants
    private static final int SKIP = 0;
    private static final int NEXT = 1;
    private static final int BUY = 2;
    private static final int SELL = 3;
    private static final int LEVELUP = 4;
    private static final int GOWORK = 5;
    // Prices
    private final double SKIP_price = 50;

    public Table(Cell[] cells, User[] users){
        this.cells = cells;
        size = cells.length-1;
        this.users = users;
        turn = 0;

        randomizePlayerPositions(this.users, cells.length);
    }

    public static void randomizePlayerPositions(User[] users, int cellsize){
        for (User user : users){
            int randomNumber = (int) (Math.random()*cellsize);
            user.setCellPosition(randomNumber);
        }
    }

    public Cell cell(int n){
        if (!isLimit(n)){
            return cells[n];
        } else {
            return cells[0];
        }
    }

    private boolean isLimit(int n){
        return (!(n < 0 || n > size));
    }

    public int getTurn(){
        return turn;
    }

    public int nextTurn(){
        turn += 1;
        turn %= users.length;
        return turn;
    }

    public User turnUser(){
        return users[turn];
    }

    public Cell getTurnUserCell(){
        int cellPos = users[turn].getCellPosition();
        return cells[cellPos];
    }

    public void turnUserGoNext(int diceNumber){
        turnUser().addCellPosition(diceNumber, cells.length);
    }

    public void sayNoMoney(){
        System.out.println("You have not enough money!");
    }

    public void showInfo(){
        System.out.println("===============================");
        System.out.println("Turn: " + turnUser().getName());
        System.out.println("Money: " + turnUser().getMoney());
        System.out.println("Cell: " + getTurnUserCell().getShowText());
        System.out.println("===============================");
        System.out.println(SKIP + ". Skip "+SKIP_price+"$");
        System.out.println(NEXT + ". Go next");
        System.out.println(BUY + ". Buy (-"+getTurnUserCell().getPrice()+"$)");
        System.out.println(SELL + ". Sell (+"+getTurnUserCell().sellPrice()+"$)");
        System.out.println(LEVELUP + ". Level Up (-"+getTurnUserCell().levelUpPrice()+"$)");
        System.out.println(GOWORK + ". Go work (+"+workPay(turnUser()) +"$)");
    }

    public double workPay(User user){
        double summ = 0;
        for (Cell cell : cells){
            if (cell.isAuthor(user)){
                summ += workPayOf(cell);
            }
        }
        return summ;
    }

    public double workPayOf(Cell cell){
        return percent(cell.bonus(), 1);
    }

    public void play(int num){

        // Cheat Code
        if (num == 9999){
            turnUser().summMoney(100000);
        }
        // ----------

        if (num == SKIP) {
            if (turnUser().hasEnoughMoney(SKIP_price)){
                turnUser().summMoney(-SKIP_price);
                message("   Next Turn");
                nextTurn();
            } else {
                sayNoMoney();
            }
        }

        else if (num == NEXT){
            int number = Dice.rollDice();
            turnUserGoNext(number);
            Cell cell = getTurnUserCell();
            cell.onStep(turnUser());
            nextTurn();
        }

        else if (num == BUY){
            boolean success = getTurnUserCell().buyCell(turnUser());
            goodOrBad(success);
            if (success) nextTurn();
        }

        else if (num == SELL){
            boolean success = getTurnUserCell().sell(turnUser());
            goodOrBad(success);
            if (success) nextTurn();
        }

        else if (num == LEVELUP){
            boolean success = getTurnUserCell().levelUp(turnUser());
            goodOrBad(success);
            if (success) nextTurn();
        }

        else if (num == GOWORK){
            turnUser().summMoney(workPay(turnUser()));
            nextTurn();
        }


        // Info
        showInfo();
        showStatus();

        // Losed
        if (turnUser().isUserLosed()){
            System.out.println("==========" + turnUser().getName() + " LOSED! ========");
            nextTurn();
        }

    }

    private void goodOrBad(boolean bool){
        if (bool){
            message("+++ GOOD +++");
        } else {
            message("--- BAD  ---");
        }
    }

    private void message(String str){
        System.out.println(turnUser().getName() + ">>> " + str);
    }

    private void showStatus(){
        System.out.println("");
        System.out.println("### TABLE STATUS: ");
        int currentPlayerPos = turnUser().getCellPosition();
        for (int i = 0; i < cells.length; i++){
            Cell cell = cells[i];
            if (cell.isOwnedBySomeone()){
                char ch = cell.author.getName().charAt(0);
                System.out.print(ch);
            } else {
                System.out.print("*");
            }
        }
        System.out.println("");
        for (int i = 0; i < cells.length; i++){
            if (currentPlayerPos != i){
                System.out.print(" ");
            } else {
                System.out.print("^");
            }
        }
        System.out.println("");
        System.out.println("");
    }





    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-
    //          CLASS: Cell
    // =-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-

    public static class Cell{
        private String name;
        private double price;
        private int level;
        private User author;

        public Cell(String name, double price){
            this.name = name;
            this.price = price;
            this.level = 1;
            this.author = null;
        }

        private String getLevelShow(){
            String str = "<<Level: ";
            String levelUpString = " ^" + levelUpPrice() + "$";
            str += level + levelUpString + ">>";
            return str;
        }

        private String getShowText(){
            String _author = (this.author != null) ? this.author.getName() : "Nobody";
            return this.name +" (price: "+this.price+"$) [bonus: "+this.bonus()+"] " + getLevelShow() + " :: " + _author;
        }

        public String getName(){
            return name;
        }

        public double getPrice(){
            return price;
        }

        public boolean buyCell(User user){
            if (isOwnedBySomeone()){
                return false;
            }
            if (!user.hasEnoughMoney(this.price)){
                return false;
            }

            this.author = user;
            this.level = 1;
            user.summMoney(-price);
            return true;
        }

        public boolean isOwnedBySomeone(){
            return (this.author != null);
        }

        public double sellPrice(){
            return percent(price, 75);
        }

        public boolean isAuthor(User user){
            if (isOwnedBySomeone()){
                return (user.getName().equals(author.getName()));
            }
            return false;
        }

        public boolean sell(User user){
            if (!isAuthor(user)) return false;
            if (isOwnedBySomeone()){
                author.summMoney(sellPrice());
                this.level = 1;
                this.author = null;
                return true;
            }
            return false;
        }

        public double bonus(){
            return percent(price * level, 50);
        }

        public double levelUpPrice(){
            return price * (level + 1);
        }

        public void onStep(User user){
            if (!isOwnedBySomeone()) return;
            if (isAuthor(user)){
                user.summMoney(bonus());
                System.out.printf(":) BONUS [%s] +" + bonus() + "\n", user.getName());
                return;
            } else {
                user.summMoney(-bonus());
                System.out.printf(":( BONUS [%s] - " + bonus() + "\n", user.getName());
                author.summMoney(bonus());
                return;
            }
        }

        public boolean levelUp(User user){
            if (isOwnedBySomeone()){
                if (isAuthor(user)){
                    if (user.hasEnoughMoney(levelUpPrice())){
                        user.summMoney(-levelUpPrice());
                        level += 1;
                        return true;
                    }
                }
            }
            return false;
        }

        public int getLevel(){
            return level;
        }


    }
}
