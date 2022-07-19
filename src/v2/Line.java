package v2;

import java.util.*;
import java.util.stream.Collectors;

public abstract class Line implements SudokuFrame{

    protected int lineIndex;
    protected int size;
    protected Cell[] line;
    private List<EliminatedRecord> eliminated;

    public Line(int lineIndex, int size, Cell[] line, List<EliminatedRecord> eliminatedRecords) {
        this.lineIndex = lineIndex;
        this.size = size;
        this.line = line;
        this.eliminated = eliminatedRecords;
    }

    protected abstract int getRelevantIndex(Cell cell);

    public Cell[] getLine() {
        return line;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public int size() {
        return size;
    }

    public List<EliminatedRecord> putNumber(int indexToPut, int number) {
        line[indexToPut].setValue(number);
        for (int index = 0; index < size; index++) {
            if(index != indexToPut) {
                line[index].addIneligible(number,"Line Put Number");
            }
        }
        return eliminated;
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
        for (int index = 0; index < size; index++) {
            if(line[index].isNakedSingle()) {
                try {
                    res.add(new CellRecord(line[index], line[index].getOnlyOption()));
                } catch (ValueHaveFewOptionsException ignore) {
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
        List<CellRecord> res = new ArrayList<>();
        for (int index = 0; index < size; index++) {
            Integer hiddenSingleForIndex = getHiddenSingleForIndex(index);
            if(hiddenSingleForIndex != null){
                res.add(new CellRecord(line[index], hiddenSingleForIndex));
            }
        }
        return res;
    }

    private Integer getHiddenSingleForIndex(int cellIndex) {
        if(!line[cellIndex].isEmpty())
            return null;
        Set<Integer> cellOptions = new HashSet<>(line[cellIndex].getOptions());
        Set<Integer> othersOptions = new HashSet<>();
        for (int index = 0; index < size; index++) {
            if(line[index].isEmpty() && index != cellIndex){
                othersOptions.addAll(line[index].getOptions());
            }
        }
        cellOptions.removeAll(othersOptions);
        if(cellOptions.size() == 1){
            return cellOptions.iterator().next();
        }
        else{
            return null;
        }
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
        for (int index1 = 0; index1 < size - 1; index1++) {
            for (int index2 = index1 + 1; index2 < size; index2++) {
                if(line[index1].getOptions().size() == 2 && line[index1].getOptions().equals(line[index2].getOptions())){
                    Cell[] pair = {line[index1], line[index2]};
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
            for (int index = 0; index < size; index++) {
                if (line[index].isEmpty() && line[index] != pair[0] && line[index] != pair[1]) {
                    for (Integer option : pairOptions) {
                        line[index].addIneligible(option, "Line Naked Pair");
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
        List<Set<Integer>> hiddenNumbersForCell = Arrays.stream(line).map(cell -> hiddenNumbersInCell(hiddenNumberCandidate, getRelevantIndex(cell))).collect(Collectors.toList());
        List<Cell[]> pairs = new ArrayList<>();
        for (int index1 = 0; index1 < line.length - 1; index1++) {
            for (int index2 = index1 + 1; index2 < line.length; index2++) {
                if(hiddenNumbersForCell.get(index1).size() == 2 && hiddenNumbersForCell.get(index1).equals(hiddenNumbersForCell.get(index2))) {
                    Cell[] pair = {line[index1], line[index2]};
                    pairs.add(pair);
                }
            }
        }
        updateAfterHiddenPair(pairs, hiddenNumbersForCell);
        return pairs;
    }

    public int[] getAppearanceCountersForNumberArray() {
        int[] numberOfAppearance = new int[line.length + 1];
        for (Cell cell : line) {
            if(cell.isEmpty()){
                for (Integer option : cell.getOptions()) {
                    numberOfAppearance[option]++;
                }
            }
        }
        return numberOfAppearance;
    }

    public Set<Integer> getCellsIndexesWithOption(int option){
        Set<Integer> holdingOption = new HashSet<>();
        for (int index = 0; index < size; index++) {
            if(line[index].isEmpty() && line[index].hasOption(option)){
                holdingOption.add(index);
            }
        }
        return holdingOption;
    }

    protected Set<Integer> hiddenNumbersInCell(ArrayList<Integer> hiddenNumberCandidate, int cellIndex) {
        Set<Integer> res = new HashSet<>();
        if(!line[cellIndex].isEmpty())
            return res;
        for (Integer number : hiddenNumberCandidate) {
            if(line[cellIndex].hasOption(number))
                res.add(number);
        }
        return res;
    }

    private List<EliminatedRecord> updateAfterHiddenPair(List<Cell[]> pairs, List<Set<Integer>> hiddenNumbersForCell) {
        for (Cell[] pair : pairs) {
            for (int number = 1; number <= line.length; number++) {
                if(!hiddenNumbersForCell.get(getRelevantIndex(pair[0])).contains(number)){
                    pair[0].addIneligible(number, "Line Hidden Pair");
                    pair[1].addIneligible(number, "Line Hidden Pair");
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
        List<Cell> haveUpTo3options = Arrays.stream(line).filter(cell -> cell.isEmpty() && cell.getOptions().size() > 1 && cell.getOptions().size() < 4).collect(Collectors.toList());
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
            for (int index = 0; index < size; index++) {
                if (line[index] != triple[0] && line[index] != triple[1] && line[index] != triple[2] && line[index].isEmpty()) {
                    for (Integer option : tripleOptions) {
                        line[index].addIneligible(option, "Line Naked Triple");
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
//        List<Cell[]> res = new ArrayList<>();
        List<Cell> haveUpTo4options = Arrays.stream(line).filter(cell -> cell.isEmpty() && cell.getOptions().size() > 1 && cell.getOptions().size() < 5).collect(Collectors.toList());
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
            for (int index = 0; index < size; index++) {
                if (line[index] != quads[0] && line[index] != quads[1] && line[index] != quads[2] && line[index] != quads[3] && line[index].isEmpty()) {
                    for (Integer option : quadOptions) {
                        line[index].addIneligible(option, "Line Naked Quad");
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
     * When a certain candidate appears only in two (three) cells in a row or a column, and the cells
     * are also in a box, they are called a Claiming Pair(Triple).
     * All other appearances of the candidate in the same block can be eliminated.
     * @return list of pointing pair records
     */
    public List<ClaimingPairRecord> lookForVerticalClaimingPair(){
        return lookForClaimingPair(buildSetOfOptionsForClaimingPair(), "vertical");
    }

    public List<ClaimingPairRecord> lookForHorizontalClaimingPair(){
        return lookForClaimingPair(buildSetOfOptionsForClaimingPair(), "horizontal");
    }

    private Set<Integer>[] buildSetOfOptionsForClaimingPair(){
        int sqrtSize = (int) Math.sqrt(size);
        Set<Integer>[] optionsForBox = new Set[sqrtSize];
        for (int i = 0; i < sqrtSize; i++) {
            optionsForBox[i] = new HashSet<>();
        }
        for (int lineIndex = 0; lineIndex < size; lineIndex++) {
            if(line[lineIndex].isEmpty()){
                optionsForBox[lineIndex / sqrtSize].addAll(line[lineIndex].getOptions());
            }
        }
        return optionsForBox;
    }

    private List<ClaimingPairRecord> lookForClaimingPair(Set<Integer>[] setPerBox, String type) {
        int sqrtSize = setPerBox.length;
        List<ClaimingPairRecord> claimingPairs = new ArrayList<>();
        for (int setIndex = 0; setIndex < setPerBox.length; setIndex++) {
            Set<Integer> tempSet = new HashSet<>(setPerBox[setIndex]);
            for (int otherSetIndex = 0; otherSetIndex < setPerBox.length; otherSetIndex++) {
                if(otherSetIndex != setIndex){
                    tempSet.removeAll(setPerBox[otherSetIndex]);
                }
            }
            if(!tempSet.isEmpty()){
                for (Integer integer : tempSet) {
                    int counter = 0;
                    for (int i = setIndex * sqrtSize; i < setIndex * sqrtSize + sqrtSize; i++) {
                        if(line[i].hasOption(integer)){
                            counter++;
                        }
                    }
                    if(counter > 1){
                        switch (type){
                            case "vertical" -> claimingPairs.add(new ClaimingPairRecord(lineIndex / sqrtSize, setIndex, lineIndex % sqrtSize, integer));
                            case "horizontal" -> claimingPairs.add(new ClaimingPairRecord(setIndex, lineIndex / sqrtSize, lineIndex % sqrtSize, integer));
                        }
                    }
                }
            }
        }
        return claimingPairs;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Length: ").append(size).append("\n").append("Index: ").append(lineIndex).append("\n");
        sb.append("Line:\t");
        for (Cell cell : line) {
            sb.append(cell).append("\t");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        HashSet<Integer> s1 = new HashSet<>();
        s1.add(1);
        s1.add(2);
        HashSet<Integer> s2 = new HashSet<>();
        s2.add(2);
        s2.add(1);
        System.out.println(s1.equals(s2));
    }
}
