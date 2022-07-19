package v2;

import java.util.*;

public class Board {

    private int size;
    private Cell[][] board;
    private List<Row> rows;
    private List<Column> columns;
    private Box[][] boxes;
    private List<EliminatedRecord> eliminatedRecords;

    public Board(int[][] intBoard) {
        size = intBoard.length;
        eliminatedRecords = new LinkedList<>();
        buildBoard(intBoard);
        initRows();
        initColumns();
        initBoxes();
    }

    private void buildBoard(int[][] intBoard) {
        board = new Cell[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                board[row][column] = new Cell(intBoard[row][column], size, row, column, eliminatedRecords);
            }
        }
        initCellsIneligibles();
    }

    private void initCellsIneligibles() {
        initRowsIneligibles();
        initColsIneligibles();
        initBoxesIneligibles();
    }

    private void initRowsIneligibles() {
        for (int i = 0; i < size; i++) {
            initRowIneligibles(i);
        }
    }

    private void initRowIneligibles(int i) {
        Set<Integer> rowIneligibles = getRowIneligibles(i);
        for (int j = 0; j < board.length; j++) {
            if(board[i][j].isEmpty()){
                for (Integer ineligible:rowIneligibles) {
                    board[i][j].addIneligible(ineligible, "Initialization");
                }
            }
        }
    }

    private Set<Integer> getRowIneligibles(int i) {
        Set<Integer> ineligibles = new HashSet<>();
        for (int j = 0; j < size; j++) {
            if(!board[i][j].isEmpty()) {
                ineligibles.add(board[i][j].getValue());
            }
        }
        return ineligibles;
    }

    private void initColsIneligibles() {
        for (int j = 0; j < size; j++) {
            initColIneligibles(j);
        }
    }

    private void initColIneligibles(int j) {
        Set<Integer> colIneligibles = getColIneligibles(j);
        for (int i = 0; i < size; i++) {
            if(board[i][j].isEmpty()){
                for (Integer ineligible:colIneligibles) {
                    board[i][j].addIneligible(ineligible, "Initialization");
                }
            }
        }
    }

    private Set<Integer> getColIneligibles(int j) {
        Set<Integer> ineligibles = new HashSet<>();
        for (int i = 0; i < size; i++) {
            if(!board[i][j].isEmpty()) {
                ineligibles.add(board[i][j].getValue());
            }
        }
        return ineligibles;
    }

    private void initBoxesIneligibles() {
        int sqrSize = (int)Math.sqrt(size);
        for (int localBoxRow = 0; localBoxRow < size; localBoxRow += sqrSize) {
            for (int localBoxCol = 0; localBoxCol < size; localBoxCol += sqrSize) {
                initBoxIneligibles(localBoxRow, localBoxCol);
            }
        }
    }

    private void initBoxIneligibles(int localBoxRow, int localBoxCol) {
        int sqrSize = (int)Math.sqrt(size);
        Set<Integer> boxIneligibles = getBoxIneligibles(localBoxRow, localBoxCol);
        for (int i = localBoxRow; i < localBoxRow + sqrSize; i++) {
            for (int j = localBoxCol; j < localBoxCol + sqrSize; j++) {
                if(board[i][j].isEmpty()) {
                    for (Integer ineligible : boxIneligibles) {
                        board[i][j].addIneligible(ineligible, "Initialization");
                    }
                }
            }
        }
    }

    private Set<Integer> getBoxIneligibles(int localBoxRow, int localBoxCol) {
        int sqrSize = (int)Math.sqrt(size);
        Set<Integer> ineligibles = new HashSet<>();
        for (int i = localBoxRow; i < localBoxRow + sqrSize; i++) {
            for (int j = localBoxCol; j < localBoxCol + sqrSize; j++) {
                if(!board[i][j].isEmpty()){
                    ineligibles.add(board[i][j].getValue());
                }
            }
        }
        return ineligibles;
    }

    private void initBoxes() {
        int sqrSize = (int)Math.sqrt(size);
        boxes = new Box[sqrSize][sqrSize];
        for (int localRowIndex = 0; localRowIndex < sqrSize; localRowIndex++) {
            for (int localColumnIndex = 0; localColumnIndex < sqrSize; localColumnIndex++) {
                Cell[][] box = new Cell[sqrSize][sqrSize];
                for (int row = 0; row < sqrSize; row++) {
                    System.arraycopy(board[localRowIndex * sqrSize + row], localColumnIndex * sqrSize, box[row], 0, sqrSize);
                }
                boxes[localRowIndex][localColumnIndex] = new Box(box, localRowIndex, localColumnIndex, eliminatedRecords);
            }
        }
    }

    private List<Box> boxesAsList(){
        List<Box> boxArrayList = new ArrayList<>();
        int sqrtSize = (int) Math.sqrt(size);
        for (int row = 0; row < sqrtSize; row++) {
            for (int column = 0; column < sqrtSize; column++) {
                boxArrayList.add(boxes[row][column]);
            }
        }
        return boxArrayList;
    }

    private void initColumns() {
        columns = new ArrayList<>();
        for (int j = 0; j < size; j++) {
            Cell[] column = new Cell[size];
            for (int i = 0; i < size; i++) {
                column[i] = board[i][j];
            }
            columns.add(new Column(j, size, column, eliminatedRecords));
        }
    }

    private void initRows() {
        rows = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            rows.add(new Row(i, size, board[i], eliminatedRecords));
        }
    }

    private void doBoxEliminations(List<Box> boxesAsList) {
        for (Box box : boxesAsList) {
            box.getNakedPairs();
            revealNumbersOnBox(box);
            box.getHiddenPairs();
            revealNumbersOnBox(box);
            box.getNakedTriple();
            revealNumbersOnBox(box);
            box.getNakedQuad();
            revealNumbersOnBox(box);
        }
    }

    private void revealNumbersOnBox(Box box) {
        revealNumbers(new HashSet<>(box.getNakedSingles()));
        revealNumbers(new HashSet<>(box.getHiddenSingles()));
    }

    private void doLineEliminations(List<? extends Line> lines) {
        for (Line line : lines) {
            line.getNakedPairs();
            revealNumbersOnLine(line);
            line.getHiddenPairs();
            revealNumbersOnLine(line);
            line.getNakedTriple();
            revealNumbersOnLine(line);
            line.getNakedQuad();
            revealNumbersOnLine(line);
        }
    }

    private void revealNumbersOnLine(Line line) {
        revealNumbers(new HashSet<>(line.getNakedSingles()));
        revealNumbers(new HashSet<>(line.getHiddenSingles()));
    }

    private List<EliminatedRecord> eliminateWithXWings(){
        eliminatedRecords.addAll(eliminateWithXWings(rows, columns));
        eliminatedRecords.addAll(eliminateWithXWings(columns, rows));
        return eliminatedRecords;
    }

    private List<EliminatedRecord> eliminateWithXWings(List<? extends Line> linesToLookAt, List<? extends Line> linesToRemoveFrom){
        List<EliminatedRecord> eliminated = new ArrayList<>();
        for (int line1 = 0; line1 < size - 1; line1++) {
            for (int line2 = line1 + 1; line2 < size; line2++) {
                int[] appearanceCountersForNumberArray1 = linesToLookAt.get(line1).getAppearanceCountersForNumberArray();
                int[] appearanceCountersForNumberArray2 = linesToLookAt.get(line2).getAppearanceCountersForNumberArray();
                for (int number = 1; number < appearanceCountersForNumberArray1.length; number++) {
                    if(appearanceCountersForNumberArray1[number] == 2 && appearanceCountersForNumberArray2[number] == 2){
                        Set<Integer> cellsIndexesWithOptions1 = linesToLookAt.get(line1).getCellsIndexesWithOption(number);
                        Set<Integer> cellsIndexesWithOptions2 = linesToLookAt.get(line2).getCellsIndexesWithOption(number);
                        if(cellsIndexesWithOptions1.equals(cellsIndexesWithOptions2)){
                            for (Integer lineIndex : cellsIndexesWithOptions1) {
                                Cell[] line = linesToRemoveFrom.get(lineIndex).getLine();
                                for (int i = 0; i < line.length; i++) {
                                    if(i != line1 && i != line2 && line[i].isEmpty() && line[i].hasOption(number)){
                                        line[i].addIneligible(number, "XWings");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return eliminated;
    }

    private List<EliminatedRecord> eliminateWithClaimingPair(){
        eliminatedRecords.addAll(eliminateWithClaimingPair("vertical"));
        eliminatedRecords.addAll(eliminateWithClaimingPair("horizontal"));
        return eliminatedRecords;
    }

    private List<EliminatedRecord> eliminateWithClaimingPair(String type){
        List<EliminatedRecord> eliminated = new ArrayList<>();
        List<ClaimingPairRecord> claimingPairRecords = new ArrayList<>();
        switch (type){
            case "vertical" -> {
                for (Row row : rows) {
                    claimingPairRecords.addAll(row.lookForVerticalClaimingPair());
                }
            }
            case "horizontal" -> {
                for (Column column : columns) {
                    claimingPairRecords.addAll(column.lookForHorizontalClaimingPair());
                }
            }
        }
        for (ClaimingPairRecord claimingPair : claimingPairRecords) {
            Cell[][] box = boxes[claimingPair.localRow()][claimingPair.localColumn()].getBox();
            for (int row = 0; row < box.length; row++) {
                for (int column = 0; column < box.length; column++) {
                    switch (type) {
                        case "vertical" -> {
                            if (row != claimingPair.lineIndexInBox() && box[row][column].isEmpty()) {
                                box[row][column].addIneligible(claimingPair.value(), "Vertical Claiming Pair");
                            }
                        }
                        case "horizontal" -> {
                            if (column != claimingPair.lineIndexInBox() && box[row][column].isEmpty()) {
                                box[row][column].addIneligible(claimingPair.value(), "Horizontal Claiming Pair");
                            }
                        }
                    }
                }
            }
        }
        return eliminated;
    }

    private List<EliminatedRecord> eliminateWithPointingPairs() {
        int sqrtSize = (int) Math.sqrt(size);
        for (int localRow = 0; localRow < sqrtSize; localRow++) {
            for (int localColumn = 0; localColumn < sqrtSize; localColumn++) {
                eliminatedRecords.addAll(eliminatePointingPairsInColumns(sqrtSize, localRow, localColumn));
                eliminatedRecords.addAll(eliminatePointingPairsInRows(sqrtSize, localRow, localColumn));
            }
        }
        return eliminatedRecords;
    }

    private List<EliminatedRecord> eliminatePointingPairsInRows(int sqrtSize, int localRow, int localColumn) {
        List<EliminatedRecord> eliminated = new ArrayList<>();
        List<PointingPairRecord> pointingPairRecords = boxes[localRow][localColumn].lookForHorizontalPointingPair();
        for (PointingPairRecord pointingPair : pointingPairRecords) {
            for (int column = 0; column < size; column++) {
                if(board[pointingPair.line()][column].isEmpty()){
                    if(column < boxes[localRow][localColumn].getGlobalColumn() || column >= boxes[localRow][localColumn].getGlobalColumn() + sqrtSize){
                        board[pointingPair.line()][column].addIneligible(pointingPair.value(), "Horizontal Pointing Pair");
                    }
                }
            }
        }
        return eliminated;
    }

    private List<EliminatedRecord> eliminatePointingPairsInColumns(int sqrtSize, int localRow, int localColumn) {
        List<EliminatedRecord> eliminated = new ArrayList<>();
        List<PointingPairRecord> pointingPairRecords = boxes[localRow][localColumn].lookForVerticalPointingPair();
        for (PointingPairRecord pointingPair : pointingPairRecords) {
            for (int row = 0; row < size; row++) {
                if(board[row][pointingPair.line()].isEmpty()){
                    if(row < boxes[localRow][localColumn].getGlobalRow() || row >= boxes[localRow][localColumn].getGlobalRow() + sqrtSize){
                        board[row][pointingPair.line()].addIneligible(pointingPair.value(), "Vertical Pointing Pair");
                    }
                }
            }
        }
        return eliminated;
    }

    private boolean revealNumbers(Set<CellRecord> cellsToReveal) {
        for (CellRecord cellRecord : cellsToReveal) {
            putNumber(cellRecord);
        }
        return cellsToReveal.size() > 0;
    }

    private void putNumber(CellRecord cellRecord) {
        Cell cell = cellRecord.cell();
        int value = cellRecord.value();
        int row = cell.getRow();
        int column = cell.getColumn();
        rows.get(row).putNumber(column, value);
        columns.get(column).putNumber(row, value);
        boxes[cell.getBoxIndexes()[0]][cell.getBoxIndexes()[1]].putNumber(row, column, value);
    }

    private Set<CellRecord> lookForHiddenSingles() {
        Set<CellRecord> hiddenSingles = new HashSet<>();
        for (Row row : rows) {
            hiddenSingles.addAll(row.getHiddenSingles());
        }
        for (Column column : columns) {
            hiddenSingles.addAll(column.getHiddenSingles());
        }
        int sqrtSize = (int) Math.sqrt(size);
        for (int i = 0; i < sqrtSize; i++) {
            for (int j = 0; j < sqrtSize; j++) {
                hiddenSingles.addAll(boxes[i][j].getHiddenSingles());
            }

        }
        return hiddenSingles;
    }

    private Set<CellRecord> lookForNakedSingles() {
        Set<CellRecord> nakedSingles = new HashSet<>();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if(board[row][column].isNakedSingle()){
                    try {
                        nakedSingles.add(new CellRecord(board[row][column], board[row][column].getOnlyOption()));
                    } catch (ValueHaveFewOptionsException ignore) {
                    }
                }
            }
        }
        return nakedSingles;
    }

    private boolean isSolved(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if(board[i][j].isEmpty())
                    return false;
            }
        }
        return true;
    }

    private boolean isWrongSolve() {
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if(board[row][column].isEmpty() && board[row][column].getOptions().size() == 0){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        int sqrSize = (int)Math.sqrt(size);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            if(i != 0 && i % sqrSize == 0){
                for (int j = 0; j <= size * 2; j++) {
                    sb.append("--");
                }
                sb.append("\n");
            }
            for (int j = 0; j < size; j++) {
                if(j != 0 && j % sqrSize == 0){
                    sb.append("|");
                }
                sb.append(board[i][j].getValue()).append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public boolean solve() {
        boolean cantSolve = false;
        while (!isSolved()){
            int eliminationBeforeIteration = eliminatedRecords.size();
            while (revealNumbers(lookForNakedSingles()) || revealNumbers(lookForHiddenSingles())){
            }
            doLineEliminations(rows);
            doLineEliminations(columns);
            doBoxEliminations(boxesAsList());
            eliminateWithPointingPairs();
            eliminateWithClaimingPair();
            eliminateWithXWings();
            int eliminationAfterIteration = eliminatedRecords.size();
            if(isWrongSolve()){
                // There are errors on this solve, some cells have 0 options.
                cantSolve = true;
                break;
            }
            if(eliminationBeforeIteration == eliminationAfterIteration && !isSolved()){
                //no elimination made on the current iteration
                cantSolve = true;
                break;
            }
        }
        return !cantSolve;
    }

    public static void main(String[] args) {
        Board board = new Board(BoardUtils.simpleBoard1);
        System.out.println(board);
        System.out.println();
        System.out.println();
        board.solve();
        System.out.println(board);
    }
}
