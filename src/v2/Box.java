package v2;

import java.util.*;
import java.util.stream.Collectors;

public class Box implements SudokuFrame{

    private int size;
    private Cell[][] box;
    private int localRow;
    private int localColumn;
    private int globalRow;
    private int globalColumn;
    private List<EliminatedRecord> eliminated;

    public Box(Cell[][] box, int localRow, int localColumn, List<EliminatedRecord> eliminatedRecords) {
        this.size = box.length;
        this.box = box;
        this.localRow = localRow;
        this.localColumn = localColumn;
        globalRow = localRow * size;
        globalColumn = localColumn * size;
        eliminated = eliminatedRecords;
    }

    public int size() {
        return size;
    }

    public int getLocalRow() {
        return localRow;
    }

    public int getLocalColumn() {
        return localColumn;
    }

    public int getGlobalRow() {
        return globalRow;
    }

    public int getGlobalColumn() {
        return globalColumn;
    }

    public Cell[][] getBox() {
        return box;
    }

    public List<EliminatedRecord> putNumber(int globalRow, int globalColumn, int number){
        int localRowIndex = globalRow % size;
        int localColumnIndex = globalColumn % size;
        box[localRowIndex][localColumnIndex].setValue(number);
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if(row != localRowIndex || column != localColumnIndex) {
                    box[row][column].addIneligible(number, "Box Put Number");
                }
            }
        }
        return eliminated;
    }

    private Cell[] boxToArray() {
        Cell[] boxRepresentedByArray = new Cell[size * size];
        int index = 0;
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                boxRepresentedByArray[index++] = box[row][column];
            }
        }
        return boxRepresentedByArray;
    }

    /**
     * A cell with only one candidate is called a Naked Single.
     * The sole candidate is the solution to the cell.
     * All other appearances of the same candidate, if any, are eliminated if they can be
     * seen by the Single.
     * The method will update the Row!
     * @return List<Cell> all naked single cells in the row
     */
    public List<CellRecord> getNakedSingles() {
        List<CellRecord> res = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if(box[row][column].isNakedSingle()) {
                    try {
                        res.add(new CellRecord(box[row][column], box[row][column].getOnlyOption()));
                    } catch (ValueHaveFewOptionsException ignore) {
                    }
                }
            }
        }
        return res;
    }

    /**
     * A cell with multiple candidates is called a Hidden Single if one of the candidate
     * is the only candidate in a line, or a box.
     * The single candidate is the solution to the cell.
     * All other appearances of the same candidate, if any, are eliminated if they can be
     * seen by the Single.
     * The method will update the Line!
     * @return List<Cell> all the hidden singles (after update) in the line
     */
    public List<CellRecord> getHiddenSingles() {
        int[] countersForNumberArray = getAppearanceCountersForNumberArray();
        ArrayList<Integer> hiddenNumbersCandidates = getHiddenNumbersCandidates(countersForNumberArray, 1);
        List<CellRecord> res = new ArrayList<>();
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                for (Integer hiddenNumber : hiddenNumbersCandidates) {
                    if(box[row][column].hasOption(hiddenNumber)){
                        res.add(new CellRecord(box[row][column], hiddenNumber));
                        break;
                    }
                }
            }
        }
        return res;
    }

    private ArrayList<Integer> getHiddenNumbersCandidates(int[] countersForNumberArray, int size) {
        ArrayList<Integer> hiddenNumberCandidate = new ArrayList<>();
        for (int number = 1; number < countersForNumberArray.length; number++) {
            if(countersForNumberArray[number] == size)
                hiddenNumberCandidate.add(number);
        }
        return hiddenNumberCandidate;
    }

    private int[] getAppearanceCountersForNumberArray() {
        int[] numberOfAppearance = new int[size * size + 1];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Cell cell = box[row][col];
                if(!cell.isEmpty())
                    continue;
                for (Integer option : cell.getOptions()) {
                    numberOfAppearance[option]++;
                }
            }
        }
        return numberOfAppearance;
    }

    /**
     * Two cells in a line, or a block having only the same pair of candidates are
     * called a Naked Pair.
     * All other appearances of the two candidates in the same line, or box can be
     * eliminated.
     * The method will update the Line!
     * @return List<Cell[]> list of naked pairs
     */
    public List<Cell[]> getNakedPairs() {
        List<Cell[]> res = new ArrayList<>();
        Cell[] boxRepresentedByArray = boxToArray();
        for (int index1 = 0; index1 < size * size - 1; index1++) {
            for (int index2 = index1 + 1; index2 < size * size; index2++) {
                if(boxRepresentedByArray[index1].getOptions().size() == 2 && boxRepresentedByArray[index1].getOptions().equals(boxRepresentedByArray[index2].getOptions())){
                    Cell[] pair = {boxRepresentedByArray[index1], boxRepresentedByArray[index2]};
                    res.add(pair);
                }
            }
        }
        updateAfterNakedPair(res);
        return res;
    }

    private List<EliminatedRecord> updateAfterNakedPair(List<Cell[]> res){
        for (Cell[] pair : res) {
            Set<Integer> pairOptions = pair[0].getOptions();
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    if(box[row][column].isEmpty() && box[row][column] != pair[0] && box[row][column] != pair[1]){
                        for (Integer option : pairOptions) {
                            box[row][column].addIneligible(option, "Box Naked Pair");
                        }
                    }
                }
            }
        }
        return eliminated;
    }

    /**
     * When a pair of candidates appears in only two cells in a line, or a box,
     * but they aren't the only candidates in the cells, they are called a Hidden Pair.
     * All candidates other than the pair in the cells can be eliminated, yielding a
     * Naked Pair.
     * The method will update the Line!
     * @return List<Cell[]> list of hidden pairs
     */
    public List<Cell[]> getHiddenPairs() {
        int[] numberOfAppearance = getAppearanceCountersForNumberArray();
        ArrayList<Integer> hiddenNumberCandidate = new ArrayList<>();
        for (int number = 1; number < numberOfAppearance.length; number++) {
            if(numberOfAppearance[number] == 2)
                hiddenNumberCandidate.add(number);
        }
        List<Set<Integer>> hiddenNumbersForCell = Arrays.stream(boxToArray()).map(cell -> hiddenNumbersInCell(hiddenNumberCandidate, cell.getRow(), cell.getColumn())).collect(Collectors.toList());
        List<Cell[]> pairs = new ArrayList<>();
        for (int index1 = 0; index1 < size * size - 1; index1++) {
            for (int index2 = index1 + 1; index2 < size * size; index2++) {
                if(hiddenNumbersForCell.get(index1).size() == 2 && hiddenNumbersForCell.get(index1).equals(hiddenNumbersForCell.get(index2))) {
                    Cell[] pair = {box[index1 / size][index1 % size], box[index2 / size][index2 % size]};
                    pairs.add(pair);
                }
            }
        }
        updateAfterHiddenPair(pairs, hiddenNumbersForCell);
        return pairs;
    }

    protected Set<Integer> hiddenNumbersInCell(ArrayList<Integer> hiddenNumberCandidate, int globalRow, int globalColumn) {
        Set<Integer> res = new HashSet<>();
        int row = globalRow % size;
        int column = globalColumn % size;
        if(!box[row][column].isEmpty()){
            return res;
        }
        for (Integer number : hiddenNumberCandidate) {
            if(box[row][column].hasOption(number))
                res.add(number);
        }
        return res;
    }

    private List<EliminatedRecord> updateAfterHiddenPair(List<Cell[]> pairs, List<Set<Integer>> hiddenNumbersForCell) {
        Cell[] cells = boxToArray();
        for (Cell[] pair : pairs) {
            int index = 0;
            for (int i = 0; i < size * size; i++) {
                if(cells[i] == pair[0]){
                    index = i;
                    break;
                }
            }
            for (int number = 1; number <= size * size; number++) {
                if(!hiddenNumbersForCell.get(index).contains(number)){
                    pair[0].addIneligible(number, "Box Hidden Pair");
                    pair[1].addIneligible(number, "Box Hidden Pair");
                }
            }
        }
        getNakedPairs();
        return eliminated;
    }

    /**
     * Three cells in a line, or a box, having only the same three candidates,
     * or their subset, are called a Naked Triple.
     * All other appearances of the same candidates can be eliminated if they are in the same line
     * or box.
     * The method will update the Line!
     * @return List<Cell[]> list of naked triple
     */
    public List<Cell[]> getNakedTriple() {
        List<Cell> haveUpTo3options = Arrays.stream(boxToArray()).filter(cell -> cell.isEmpty() && cell.getOptions().size() > 1 && cell.getOptions().size() < 4).collect(Collectors.toList());
//        lookForTripleNaked(haveUpTo3options, 0, 1, 2, res);
        List<Cell[]> res = lookForTripleNaked(haveUpTo3options);
        updateAfterNakedTriple(res);
        return res;
    }

    private List<EliminatedRecord> updateAfterNakedTriple(List<Cell[]> res) {
        for (Cell[] triple : res) {
            ArrayList<Integer> tripleOptions = new ArrayList<>(triple[0].getOptions());
            tripleOptions.addAll(triple[1].getOptions());
            tripleOptions.addAll(triple[2].getOptions());
            for (int row = 0; row < size; row++) {
                for (int column = 0; column < size; column++) {
                    if (box[row][column] != triple[0]
                            && box[row][column] != triple[1]
                            && box[row][column] != triple[2]
                            && box[row][column].isEmpty()) {
                        for (Integer option : tripleOptions) {
                            box[row][column].addIneligible(option, "Box Naked Triple");
                        }
                    }
                }
            }
        }
        return eliminated;
    }

    // Recursive solution
    private void lookForTripleNaked(List<Cell> haveUpTo3options, int i, int j, int k, List<Cell[]> triples) {
        if(haveUpTo3options.size() < 3 || i == haveUpTo3options.size() - 2){
            return;
        }
        if(j == haveUpTo3options.size() - 1) {
            lookForTripleNaked(haveUpTo3options, i + 1, i + 2, i + 3, triples);
        }
        else if(k == haveUpTo3options.size()) {
            lookForTripleNaked(haveUpTo3options, i, j + 1, j + 2, triples);
        }
        else{
            HashSet<Integer> options = new HashSet<>();
            options.addAll(haveUpTo3options.get(i).getOptions());
            options.addAll(haveUpTo3options.get(j).getOptions());
            options.addAll(haveUpTo3options.get(k).getOptions());
            if(options.size() == 3){
                Cell[] triple = {haveUpTo3options.get(i), haveUpTo3options.get(j), haveUpTo3options.get(k)};
                triples.add(triple);
                lookForTripleNaked(haveUpTo3options, i + 1, i + 2, i + 3, triples);
            }
            else{
                lookForTripleNaked(haveUpTo3options, i, j, k + 1, triples);
            }
        }
    }

    // Iterative solution
    private List<Cell[]> lookForTripleNaked(List<Cell> haveUpTo3options){
        List<Cell[]> triples = new ArrayList<>();
        for (int i = 0; i < haveUpTo3options.size() - 2; i++) {
            outerLoop:
            for (int j = i + 1; j < haveUpTo3options.size() - 1; j++) {
                for (int k = j + 1; k < haveUpTo3options.size(); k++) {
                    HashSet<Integer> options = new HashSet<>();
                    options.addAll(haveUpTo3options.get(i).getOptions());
                    options.addAll(haveUpTo3options.get(j).getOptions());
                    options.addAll(haveUpTo3options.get(k).getOptions());
                    if(options.size() == 3){
                        Cell[] triple = {haveUpTo3options.get(i), haveUpTo3options.get(j), haveUpTo3options.get(k)};
                        triples.add(triple);
                        break outerLoop;
                    }
                }
            }
        }
        return triples;
    }

    /**
     * Four cells in a line, or a box, having only the same four candidates,
     * or their subset, are called a Naked Quad.
     * All other appearances of the same candidates can be eliminated if they are in the same line
     * or box.
     * The method will update the Line!
     * @return List<Cell[]> list of naked triple
     */
    public List<Cell[]> getNakedQuad() {
        List<Cell> haveUpTo4options = Arrays.stream(boxToArray()).filter(cell -> cell.isEmpty() && cell.getOptions().size() > 1 && cell.getOptions().size() < 5).collect(Collectors.toList());
//        lookForQuadNaked(haveUpTo4options, 0, 1, 2, 3, res);
        List<Cell[]> res = lookForQuadNaked(haveUpTo4options);
        updateAfterNakedQuad(res);
        return res;
    }

    private List<EliminatedRecord> updateAfterNakedQuad(List<Cell[]> res) {
        for (Cell[] quads : res) {
            ArrayList<Integer> quadOptions = new ArrayList<>(quads[0].getOptions());
            quadOptions.addAll(quads[1].getOptions());
            quadOptions.addAll(quads[2].getOptions());
            quadOptions.addAll(quads[3].getOptions());
            for (int row = 0; row < 3; row++) {
                for (int column = 0; column < 3; column++) {
                    if (box[row][column] != quads[0]
                            && box[row][column] != quads[1]
                            && box[row][column] != quads[2]
                            && box[row][column] != quads[3]
                            && box[row][column].isEmpty()) {
                        for (Integer option : quadOptions) {
                            box[row][column].addIneligible(option, "Box Naked Quad");
                        }
                    }
                }
            }
        }
        return eliminated;
    }

    // Recursive solution
    private void lookForQuadNaked(List<Cell> haveUpTo4options, int i, int j, int k, int l, List<Cell[]> quads) {
        if(haveUpTo4options.size() < 4 || i == haveUpTo4options.size() - 3){
            return;
        }
        if(j == haveUpTo4options.size() - 2) {
            lookForQuadNaked(haveUpTo4options, i + 1, i + 2, i + 3, i + 4,quads);
        }
        else if(k == haveUpTo4options.size() - 1) {
            lookForQuadNaked(haveUpTo4options, i, j + 1, j + 2, j + 3,quads);
        }
        else if(l == haveUpTo4options.size()) {
            lookForQuadNaked(haveUpTo4options, i, j, k + 1, k + 2,quads);
        }
        else{
            HashSet<Integer> options = new HashSet<>();
            options.addAll(haveUpTo4options.get(i).getOptions());
            options.addAll(haveUpTo4options.get(j).getOptions());
            options.addAll(haveUpTo4options.get(k).getOptions());
            options.addAll(haveUpTo4options.get(l).getOptions());
            if(options.size() == 4){
                Cell[] quad = {haveUpTo4options.get(i), haveUpTo4options.get(j), haveUpTo4options.get(k), haveUpTo4options.get(l)};
                quads.add(quad);
                lookForQuadNaked(haveUpTo4options, i + 1, i + 2, i + 3, i + 4,quads);
            }
            else{
                lookForQuadNaked(haveUpTo4options, i, j, k, l + 1, quads);
            }
        }
    }

    // Iterative solution
    private List<Cell[]> lookForQuadNaked(List<Cell> haveUpTo4options){
        List<Cell[]> quads = new ArrayList<>();
        for (int i = 0; i < haveUpTo4options.size() - 3; i++) {
            outerLoop:
            for (int j = i + 1; j < haveUpTo4options.size() - 2; j++) {
                for (int k = j + 1; k < haveUpTo4options.size() - 1; k++) {
                    for (int l = k + 1; l < haveUpTo4options.size(); l++) {
                        HashSet<Integer> options = new HashSet<>();
                        options.addAll(haveUpTo4options.get(i).getOptions());
                        options.addAll(haveUpTo4options.get(j).getOptions());
                        options.addAll(haveUpTo4options.get(k).getOptions());
                        options.addAll(haveUpTo4options.get(l).getOptions());
                        if(options.size() == 4){
                            Cell[] quad = {haveUpTo4options.get(i), haveUpTo4options.get(j), haveUpTo4options.get(k), haveUpTo4options.get(l)};
                            quads.add(quad);
                            break outerLoop;
                        }
                    }
                }
            }
        }
        return quads;
    }

    /**
     * When a certain candidate appears only in two (three) cells in a block, and the cells
     * are aligned in a column or a row, they are called a Pointing Pair(Triple).
     * All other appearances of the candidate outside the block in the same column or row
     * can be eliminated.
     * @return list of pointing pair records
     */
    public List<PointingPairRecord> lookForVerticalPointingPair(){
        return lookForPointingPair(buildSetOfOptionsForColumn(), "column");
    }

    public List<PointingPairRecord> lookForHorizontalPointingPair(){
        return lookForPointingPair(buildSetOfOptionsForRow(), "row");
    }

    private Set<Integer>[] buildSetOfOptionsForRow() {
        Set<Integer>[] setPerRow = new Set[size];
        for (int i = 0; i < size; i++) {
            setPerRow[i] = new HashSet<>();
        }
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                if(box[row][column].isEmpty()) {
                    setPerRow[row].addAll(box[row][column].getOptions());
                }
            }
        }
        return setPerRow;
    }

    private Set<Integer>[] buildSetOfOptionsForColumn() {
        Set<Integer>[] setPerColumn = new Set[size];
        for (int i = 0; i < size; i++) {
            setPerColumn[i] = new HashSet<>();
        }
        for (int column = 0; column < size; column++) {
            for (int row = 0; row < size; row++) {
                if(box[row][column].isEmpty()) {
                    setPerColumn[column].addAll(box[row][column].getOptions());
                }
            }
        }
        return setPerColumn;
    }

    private List<PointingPairRecord> lookForPointingPair(Set<Integer>[] setPerLine, String lineType){
        List<PointingPairRecord> pointingPairs = new ArrayList<>();
        for (int setIndex = 0; setIndex < size; setIndex++) {
            Set<Integer> tempSet = new HashSet<>(setPerLine[setIndex]);
            for (int otherSetIndex = 0; otherSetIndex < size; otherSetIndex++) {
                if(otherSetIndex != setIndex) {
                    tempSet.removeAll(setPerLine[otherSetIndex]);
                }
            }
            if(!tempSet.isEmpty()){
                for (Integer integer : tempSet) {
                    switch (lineType){
                        case "row" -> pointingPairs.add(new PointingPairRecord(globalRow + setIndex, integer));
                        case "column" -> pointingPairs.add(new PointingPairRecord(globalColumn + setIndex, integer));
                    }
                }
            }
        }
        return pointingPairs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                sb.append(box[i][j]).append("\t");
            }
            sb.append("\n");
        }
        sb.append("localBoxRow: ").append(localRow).append("\n");
        sb.append("localBoxColumn: ").append(localColumn);
        return sb.toString();
    }
}
