import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import v2.*;

import java.util.ArrayList;
import java.util.List;

public class LineTests {

    List<EliminatedRecord> eliminatedRecords;

    Row rowWithNakedSingle;
    Row rowWithHiddenSingle;
    Row rowWithNakedPair;
    Row rowWithHiddenPair;
    Row rowWithNakedTriple;
    Row rowWithNakedQuad;
    Row emptyLine;

    Column columnWithNakedSingle;
    Column columnWithHiddenSingle;
    Column columnWithNakedPair;
    Column columnWithHiddenPair;
    Column columnWithNakedTriple;
    Column columnWithNakedQuad;


    @Before
    public void init(){
        eliminatedRecords = new ArrayList<>();
        initEmptyLine();
        initLineWithNakedSingle();
        initLineWithHiddenSingle();
        initLineWithNakedPair();
        initLineWithHiddenPair();
        initLineWithNakedTriple();
        initLineWithNakedQuad();
    }

    private Cell[] createEmptyRow(){
        Cell[] row = new Cell[9];
        for (int index = 0; index < 9; index++) {
            row[index] = new Cell(0, 9, 0, index, eliminatedRecords);
        }
        return row;
    }

    private Cell[] createEmptyColumn(){
        Cell[] column = new Cell[9];
        for (int index = 0; index < 9; index++) {
            column[index] = new Cell(0, 9, index, 0, eliminatedRecords);
        }
        return column;
    }

    private void initEmptyLine() {
        emptyLine = new Row(0, 9, createEmptyRow(), eliminatedRecords);
    }

    private void initLineWithNakedQuad() {
        Cell[] row = createEmptyRow();
        Cell[] column = createEmptyColumn();
        // 0 3 6 8
        for (int num = 1; num <= 9; num++) {
            if(num == 2){
                row[3].addIneligible(num, "Initialization");
                row[8].addIneligible(num, "Initialization");
                column[3].addIneligible(num, "Initialization");
                column[8].addIneligible(num, "Initialization");
            }
            else if(num == 6){
                row[0].addIneligible(num, "Initialization");
                column[0].addIneligible(num, "Initialization");
            }
            else if(num == 9){
                row[0].addIneligible(num, "Initialization");
                row[3].addIneligible(num, "Initialization");
                column[0].addIneligible(num, "Initialization");
                column[3].addIneligible(num, "Initialization");
            }
            else if(num != 8){
                row[0].addIneligible(num, "Initialization");
                row[3].addIneligible(num, "Initialization");
                row[6].addIneligible(num, "Initialization");
                row[8].addIneligible(num, "Initialization");
                column[0].addIneligible(num, "Initialization");
                column[3].addIneligible(num, "Initialization");
                column[6].addIneligible(num, "Initialization");
                column[8].addIneligible(num, "Initialization");
            }
        }
        rowWithNakedQuad = new Row(0, 9, row, eliminatedRecords);
        columnWithNakedQuad = new Column(0, 9, column, eliminatedRecords);
    }

    private void initLineWithNakedTriple() {
        Cell[] row = createEmptyRow();
        Cell[] column = createEmptyColumn();
        for (int num = 1; num <= 9; num++) {
            if(num == 4) {
                row[8].addIneligible(4, "Initialization");
                column[8].addIneligible(4, "Initialization");
            }
            else if(num == 6){
                row[0].addIneligible(6, "Initialization");
                column[0].addIneligible(6, "Initialization");
            }
            else if(num == 8){
                row[4].addIneligible(8, "Initialization");
                column[4].addIneligible(8, "Initialization");
            }
            else{
                row[0].addIneligible(num, "Initialization");
                row[4].addIneligible(num, "Initialization");
                row[8].addIneligible(num, "Initialization");
                column[0].addIneligible(num, "Initialization");
                column[4].addIneligible(num, "Initialization");
                column[8].addIneligible(num, "Initialization");
            }
        }
        rowWithNakedTriple = new Row(0, 9, row, eliminatedRecords);
        columnWithNakedTriple = new Column(0, 9, column, eliminatedRecords);
    }

    private void initLineWithHiddenPair() {
        Cell[] row = createEmptyRow();
        Cell[] column = createEmptyColumn();
        for (int index = 1; index < 8; index++) {
            row[index].addIneligible(1, "Initialization");
            row[index].addIneligible(9, "Initialization");
            column[index].addIneligible(1, "Initialization");
            column[index].addIneligible(9, "Initialization");
        }
        rowWithHiddenPair = new Row(0, 9, row, eliminatedRecords);
        columnWithHiddenPair = new Column(0, 9, column, eliminatedRecords);
    }

    private void initLineWithNakedPair() {
        Cell[] row = createEmptyRow();
        Cell[] column = createEmptyColumn();
        for (int num = 2; num < 9; num++) {
            row[0].addIneligible(num, "Initialization");
            row[8].addIneligible(num, "Initialization");
            column[0].addIneligible(num, "Initialization");
            column[8].addIneligible(num, "Initialization");
        }
        rowWithNakedPair = new Row(0, 9, row, eliminatedRecords);
        columnWithNakedPair = new Column(0, 9, column, eliminatedRecords);
    }

    private void initLineWithHiddenSingle() {
        Cell[] row = createEmptyRow();
        Cell[] column = createEmptyColumn();
        for (int index = 0; index < 9; index++) {
            if(index != 0){
                row[index].addIneligible(1, "Initialization");
                column[index].addIneligible(1, "Initialization");
            }
        }
        rowWithHiddenSingle = new Row(0, 9, row, eliminatedRecords);
        columnWithHiddenSingle = new Column(0, 9, column, eliminatedRecords);
    }

    private void initLineWithNakedSingle() {
        Cell[] row = createEmptyRow();
        Cell[] column = createEmptyColumn();
        for (int j = 0; j < 9; j++) {
            if(j != 0){
                row[0].addIneligible(j + 1, "Initialization");
                column[0].addIneligible(j + 1, "Initialization");
            }
        }
        rowWithNakedSingle = new Row(0, 9, row, eliminatedRecords);
        columnWithNakedSingle = new Column(0, 9, column, eliminatedRecords);
    }

    @Test
    public void putNumberTest(){
        emptyLine.putNumber(4, 1);
        Cell[] line = emptyLine.getLine();
        Assert.assertEquals("Value of the 5th cell:", 1, line[4].getValue());
        for (Cell cell : line) {
            if(cell != line[4])
                Assert.assertTrue("other cells ineligibles contains 1", cell.getIneligibles().contains(1));
        }
    }

    // Row Tests

    /*@Test
    public void getNakedSingleTest(){
        try {
            List<Cell> nakedSingles = rowWithNakedSingle.getNakedSingles();
            Assert.assertEquals("# of naked single in the line:", 1, nakedSingles.size());
            Assert.assertEquals("# of ineligibles for the naked single cell:", 8, nakedSingles.get(0).getIneligibles().size());
            Assert.assertEquals("# of options for the naked single cell:", 1, nakedSingles.get(0).getOptions().size());
            Assert.assertEquals("The value of the naked single", 1, nakedSingles.get(0).getValue());
            Cell[] line = rowWithNakedSingle.getLine();
            for (int j = 1; j < line.length; j++) {
                Assert.assertEquals("other cells # of ineligibles", 1, line[j].getIneligibles().size());
                Assert.assertTrue("other cells ineligibles contains 1", line[j].getIneligibles().contains(1));
            }
        } catch (ValueHaveFewOptionsException e) {
            e.printStackTrace();
        }
    }*/

    @Test
    public void getHiddenSingleTest(){
        List<CellRecord> hiddenSingles = rowWithHiddenSingle.getHiddenSingles();
        Assert.assertEquals("# of hidden single in the line:", 1, hiddenSingles.size());
        Assert.assertEquals("The value of the hidden single", 1, hiddenSingles.get(0).value());
    }

    @Test
    public void getNakedPairsTest(){
        List<Cell[]> nakedPairs = rowWithNakedPair.getNakedPairs();
        Assert.assertEquals("# of naked pairs: ", 1, nakedPairs.size());
        Cell cell1 = nakedPairs.get(0)[0];
        Cell cell2 = nakedPairs.get(0)[1];
        Assert.assertEquals("# of options for the First cell", 2, cell1.getOptions().size());
        Assert.assertEquals("# of options for the Second cell", 2, cell2.getOptions().size());
        Assert.assertTrue("First cell contains 1", cell1.getOptions().contains(1));
        Assert.assertTrue("First cell contains 9", cell1.getOptions().contains(9));
        Assert.assertTrue("Second cell contains 1", cell2.getOptions().contains(1));
        Assert.assertTrue("Second cell contains 9", cell2.getOptions().contains(9));
        Cell[] line = rowWithNakedPair.getLine();
        for (Cell cell : line) {
            if(cell != cell1 && cell != cell2) {
                Assert.assertTrue("Other cells not contains 1", cell.getIneligibles().contains(1));
                Assert.assertTrue("Other cells not contains 9", cell.getIneligibles().contains(9));
            }
        }
    }

    @Test
    public void getHiddenPairsTest(){
        Cell[] line = rowWithHiddenPair.getLine();
        Assert.assertEquals("# of options for the First cell", 9, line[0].getOptions().size());
        Assert.assertEquals("# of options for the Second cell", 9, line[8].getOptions().size());
        List<Cell[]> hiddenPairs = rowWithHiddenPair.getHiddenPairs();
        Assert.assertEquals("# of hidden pairs: ", 1, hiddenPairs.size());
        Cell cell1 = hiddenPairs.get(0)[0];
        Cell cell2 = hiddenPairs.get(0)[1];
        Assert.assertEquals("First cell has only 2 options", 2, cell1.getOptions().size());
        Assert.assertEquals("Second cell has only 2 options", 2, cell2.getOptions().size());
        for (Cell cell : line) {
            if(cell != cell1 && cell != cell2){
                Assert.assertTrue("Other cells not contains 4", cell.getIneligibles().contains(1));
                Assert.assertTrue("Other cells not contains 4", cell.getIneligibles().contains(9));
            }
        }
    }

    @Test
    public void getNakedTripleTest(){
        List<Cell[]> nakedTriples = rowWithNakedTriple.getNakedTriple();
        Assert.assertEquals("# of naked triple", 1, nakedTriples.size());
        Cell cell1 = nakedTriples.get(0)[0];
        Cell cell2 = nakedTriples.get(0)[1];
        Cell cell3 = nakedTriples.get(0)[2];
        Assert.assertTrue("First cell has up to 3 options", cell1.getOptions().size() <= 3);
        Assert.assertTrue("Second cell has up to 3 options", cell2.getOptions().size() <= 3);
        Assert.assertTrue("Third cell has up to 3 options", cell3.getOptions().size() <= 3);
        Cell[] line = rowWithNakedTriple.getLine();
        for (Cell cell : line) {
            if(cell != cell1 && cell != cell2 && cell != cell3) {
                Assert.assertTrue("Other cells not contains 4", cell.getIneligibles().contains(4));
                Assert.assertTrue("Other cells not contains 6", cell.getIneligibles().contains(6));
                Assert.assertTrue("Other cells not contains 8", cell.getIneligibles().contains(8));
            }
        }
    }

    @Test
    public void getNakedQuadInARowTest(){
        List<Cell[]> nakedQuads = rowWithNakedQuad.getNakedQuad();
        Assert.assertEquals("# of naked quad", 1, nakedQuads.size());
        Cell cell1 = nakedQuads.get(0)[0];
        Cell cell2 = nakedQuads.get(0)[1];
        Cell cell3 = nakedQuads.get(0)[2];
        Cell cell4 = nakedQuads.get(0)[3];
        Assert.assertTrue("First cell has up to 4 options", cell1.getOptions().size() <= 4);
        Assert.assertTrue("Second cell has up to 4 options", cell2.getOptions().size() <= 4);
        Assert.assertTrue("Third cell has up to 4 options", cell3.getOptions().size() <= 4);
        Assert.assertTrue("Forth cell has up to 4 options", cell4.getOptions().size() <= 4);
        Cell[] line = rowWithNakedQuad.getLine();
        for (Cell cell : line) {
            if(cell != cell1 && cell != cell2 && cell != cell3 && cell != cell4) {
                Assert.assertTrue("Other cells not contains 2", cell.getIneligibles().contains(2));
                Assert.assertTrue("Other cells not contains 6", cell.getIneligibles().contains(6));
                Assert.assertTrue("Other cells not contains 8", cell.getIneligibles().contains(8));
                Assert.assertTrue("Other cells not contains 9", cell.getIneligibles().contains(9));
            }
        }
    }

    // Column Tests

    /*@Test
    public void getNakedSingleInAColumnTest(){
        try {
            List<Cell> nakedSingles = columnWithNakedSingle.getNakedSingles();
            Assert.assertEquals("# of naked single in the line:", 1, nakedSingles.size());
            Assert.assertEquals("# of ineligibles for the naked single cell:", 8, nakedSingles.get(0).getIneligibles().size());
            Assert.assertEquals("# of options for the naked single cell:", 1, nakedSingles.get(0).getOptions().size());
            Assert.assertEquals("The value of the naked single", 1, nakedSingles.get(0).getValue());
            Cell[] line = columnWithNakedSingle.getLine();
            for (int j = 1; j < line.length; j++) {
                Assert.assertEquals("other cells # of ineligibles", 1, line[j].getIneligibles().size());
                Assert.assertTrue("other cells ineligibles contains 1", line[j].getIneligibles().contains(1));
            }
        } catch (ValueHaveFewOptionsException e) {
            e.printStackTrace();
        }
    }*/

    @Test
    public void getHiddenSingleInAColumnTest(){
        List<CellRecord> hiddenSingles = columnWithHiddenSingle.getHiddenSingles();
        Assert.assertEquals("# of hidden single in the line:", 1, hiddenSingles.size());
        Assert.assertEquals("The value of the hidden single", 1, hiddenSingles.get(0).value());
    }

    @Test
    public void getNakedPairsInAColumnTest(){
        List<Cell[]> hiddenPairs = columnWithNakedPair.getNakedPairs();
        Assert.assertEquals("# of hidden pairs: ", 1, hiddenPairs.size());
        Cell cell1 = hiddenPairs.get(0)[0];
        Cell cell2 = hiddenPairs.get(0)[1];
        Assert.assertEquals("# of options in First cell", 2, cell1.getOptions().size());
        Assert.assertEquals("# of options in Second cell", 2, cell2.getOptions().size());
        Assert.assertTrue("First cell contains 1", cell1.getOptions().contains(1));
        Assert.assertTrue("First cell contains 9", cell1.getOptions().contains(9));
        Assert.assertTrue("Second cell contains 1", cell2.getOptions().contains(1));
        Assert.assertTrue("Second cell contains 9", cell2.getOptions().contains(9));
        Cell[] line = columnWithNakedPair.getLine();
        for (Cell cell : line) {
            if(cell != cell1 && cell != cell2) {
                Assert.assertTrue("Other cells not contains 1", cell.getIneligibles().contains(1));
                Assert.assertTrue("Other cells not contains 9", cell.getIneligibles().contains(9));
            }
        }
    }

    @Test
    public void getHiddenPairsInAColumnTest(){
        Assert.assertEquals("First cell has 9 options", 9, columnWithHiddenPair.getLine()[0].getOptions().size());
        Assert.assertEquals("Second cell has 9 options", 9, columnWithHiddenPair.getLine()[8].getOptions().size());
        List<Cell[]> hiddenPairs = columnWithHiddenPair.getHiddenPairs();
        Assert.assertEquals("# of hidden pairs: ", 1, hiddenPairs.size());
        Cell cell1 = hiddenPairs.get(0)[0];
        Cell cell2 = hiddenPairs.get(0)[1];
        Assert.assertEquals("First cell has only 2 options", 2, cell1.getOptions().size());
        Assert.assertEquals("Second cell has only 2 options", 2, cell2.getOptions().size());
    }

    @Test
    public void getNakedTripleInAColumnTest(){
        List<Cell[]> nakedTriples = columnWithNakedTriple.getNakedTriple();
        Assert.assertEquals("# of naked triple", 1, nakedTriples.size());
        Cell cell1 = nakedTriples.get(0)[0];
        Cell cell2 = nakedTriples.get(0)[1];
        Cell cell3 = nakedTriples.get(0)[2];
        Assert.assertTrue("First cell has up to 3 options", cell1.getOptions().size() <= 3);
        Assert.assertTrue("Second cell has up to 3 options", cell2.getOptions().size() <= 3);
        Assert.assertTrue("Third cell has up to 3 options", cell3.getOptions().size() <= 3);
        Cell[] line = columnWithNakedTriple.getLine();
        for (Cell cell : line) {
            if(cell != cell1 && cell != cell2 && cell != cell3) {
                Assert.assertTrue("Other cells not contains 4", cell.getIneligibles().contains(4));
                Assert.assertTrue("Other cells not contains 6", cell.getIneligibles().contains(6));
                Assert.assertTrue("Other cells not contains 8", cell.getIneligibles().contains(8));
            }
        }
    }

    @Test
    public void getNakedQuadInAColumnTest(){
        List<Cell[]> nakedQuads = columnWithNakedQuad.getNakedQuad();
        Assert.assertEquals("# of naked quad", 1, nakedQuads.size());
        Cell cell1 = nakedQuads.get(0)[0];
        Cell cell2 = nakedQuads.get(0)[1];
        Cell cell3 = nakedQuads.get(0)[2];
        Cell cell4 = nakedQuads.get(0)[3];
        Assert.assertTrue("First cell has up to 4 options", cell1.getOptions().size() <= 4);
        Assert.assertTrue("Second cell has up to 4 options", cell2.getOptions().size() <= 4);
        Assert.assertTrue("Third cell has up to 4 options", cell3.getOptions().size() <= 4);
        Assert.assertTrue("Forth cell has up to 4 options", cell4.getOptions().size() <= 4);
        Cell[] line = columnWithNakedQuad.getLine();
        for (Cell cell : line) {
            if(cell != cell1 && cell != cell2 && cell != cell3 && cell != cell4) {
                Assert.assertTrue("Other cells not contains 2", cell.getIneligibles().contains(2));
                Assert.assertTrue("Other cells not contains 6", cell.getIneligibles().contains(6));
                Assert.assertTrue("Other cells not contains 8", cell.getIneligibles().contains(8));
                Assert.assertTrue("Other cells not contains 9", cell.getIneligibles().contains(9));
            }
        }
    }

}
