package v2;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Cell {

    private int value;
    private int row;
    private int column;
    private int boardSize;
    private Set<Integer> ineligibles;
    private Set<Integer> options;
    private List<EliminatedRecord> eliminated;

    public Cell(int value, int boardSize, int row, int column, List<EliminatedRecord> eliminatedRecords) {
        this.value = value;
        this.row = row;
        this.column = column;
        this.boardSize = boardSize;
        ineligibles = new HashSet<>();
        options = new HashSet<>();
        eliminated = eliminatedRecords;
        if(value == 0){
            for (int number = 1; number <= boardSize; number++) {
                options.add(number);
            }
        }
        else {
            options.add(value);
            for (int number = 1; number <= boardSize; number++) {
                if(number != value){
                    ineligibles.add(number);
                }
            }
        }
    }

    public int getValue() {
        return value;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public int[] getBoxIndexes(){
        int sqrtSize = (int) Math.sqrt(boardSize);
        return new int[]{row / sqrtSize, column / sqrtSize};
    }

    public Set<Integer> getIneligibles() {
        return ineligibles;
    }

    public Set<Integer> getOptions() {
        return options;
    }

    public void setValue(int value) {
        if(this.value != 0)
            return;
        this.value = value;
        if(value != 0){
            options.clear();
            options.add(value);
            ineligibles.clear();
            for (int number = 1; number <= boardSize; number++) {
                if(number != value){
                    ineligibles.add(number);
                }
            }
        }
    }

    public void addIneligible(int ineligible, String method){
        if(isEmpty() && !ineligibles.contains(ineligible)){
            ineligibles.add(ineligible);
            options.remove(ineligible);
            eliminated.add(new EliminatedRecord(method, this, ineligible));
        }
    }

    public boolean isEmpty(){
        return value == 0;
    }

    public boolean isNakedSingle() {
        return isEmpty() && options.size() == 1;
    }

    public boolean hasOption(int num){
        return options.contains(num);
    }

    public int getOnlyOption() throws ValueHaveFewOptionsException {
        if(options.size() > 1){
            throw new ValueHaveFewOptionsException(ineligibles.toString());
        }
        return options.iterator().next();
    }

    @Override
    public String toString() {
        return "Value=" + value +
                ", Options=" + options +
                ", Ineligibles: " + ineligibles;
    }

}
