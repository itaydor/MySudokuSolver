import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import v2.*;

import java.util.ArrayList;
import java.util.List;

public class BoxTests {

    List<EliminatedRecord> eliminatedRecords;

    Box emptyMiddleBox;
    Box boxWithHiddenSingle;
    Box boxWithNakedPair;
    Box boxWithHiddenPair;
    Box boxWithNakedTriple;
    Box boxWithNakedQuad;
    Box boxWithVerticalPointingPair;
    Box boxWithHorizontalPointingPair;

    private Cell[][] initBox() {
        Cell[][] box = new Cell[3][3];
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                box[row][column] = new Cell(0, 9, row, column, eliminatedRecords);
            }
        }
        return box;
    }

    @Before
    public void init(){
        eliminatedRecords = new ArrayList<>();
        initEmptyMiddleBox();
        initBoxWithHiddenSingle();
        initBoxWithNakedPair();
        initBoxWithHiddenPair();
        initBoxWithNakedTriple();
        initBoxWithNakedQuad();
        initBoxWithVerticalPointingPair();
        initBoxWithHorizontalPointingPair();
    }

    private void initBoxWithHorizontalPointingPair() {
        Cell[][] box = initBox();
        for (int row = 0; row < 3; row += 2) {
            for (int column = 0; column < 3; column++) {
                box[row][column].addIneligible(9, "Initialization");
            }
        }
        boxWithHorizontalPointingPair = new Box(box, 2, 1, eliminatedRecords);
    }

    private void initBoxWithVerticalPointingPair() {
        Cell[][] box = initBox();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column += 2) {
                box[row][column].addIneligible(9, "Initialization");
            }
        }
        boxWithVerticalPointingPair = new Box(box, 2, 1, eliminatedRecords);
    }

    private void initBoxWithNakedQuad() {
        Cell[][] box = initBox();
        // 0 3 6 8
        for (int num = 1; num <= 9; num++) {
            if(num == 2){
                box[1][0].addIneligible(num, "Initialization");
                box[2][2].addIneligible(num, "Initialization");
            }
            else if(num == 6){
                box[0][0].addIneligible(num, "Initialization");
            }
            else if(num == 9){
                box[0][0].addIneligible(num, "Initialization");
                box[1][0].addIneligible(num, "Initialization");
            }
            else if(num != 8){
                box[0][0].addIneligible(num, "Initialization");
                box[1][0].addIneligible(num, "Initialization");
                box[1][2].addIneligible(num, "Initialization");
                box[2][2].addIneligible(num, "Initialization");
            }
        }
        boxWithNakedQuad = new Box(box, 0, 0, eliminatedRecords);
    }

    private void initBoxWithNakedTriple() {
        Cell[][] box = initBox();
        for (int number = 1; number <= 9; number++) {
            if(number == 4) {
                box[2][2].addIneligible(4, "Initialization");
            }
            else if(number == 6){
                box[0][0].addIneligible(6, "Initialization");
            }
            else if(number == 8){
                box[1][1].addIneligible(8, "Initialization");
            }
            else{
                box[0][0].addIneligible(number, "Initialization");
                box[1][1].addIneligible(number, "Initialization");
                box[2][2].addIneligible(number, "Initialization");
            }
        }
        boxWithNakedTriple = new Box(box, 0, 0, eliminatedRecords);
    }

    private void initBoxWithHiddenPair() {
        Cell[][] box = initBox();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if(box[row][column] != box[0][0] && box[row][column] != box[2][2]){
                    box[row][column].addIneligible(1, "Initialization");
                    box[row][column].addIneligible(9, "Initialization");
                }
            }
        }
        boxWithHiddenPair = new Box(box, 0, 0, eliminatedRecords);
    }

    private void initBoxWithNakedPair() {
        Cell[][] box = initBox();
        for (int number = 2; number < 9; number++) {
            box[0][0].addIneligible(number, "Initialization");
            box[2][2].addIneligible(number, "Initialization");
        }
        boxWithNakedPair = new Box(box, 0, 0, eliminatedRecords);
    }

    private void initBoxWithHiddenSingle() {
        Cell[][] box = initBox();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if(row != 0 || column != 0){
                    box[row][column].addIneligible(1, "Initialization");
                }
            }
        }
        boxWithHiddenSingle = new Box(box, 0, 0, eliminatedRecords);
    }

    private void initEmptyMiddleBox() {
        Cell[][] box = initBox();
        emptyMiddleBox = new Box(box, 2, 2, eliminatedRecords);
    }

    @Test
    public void putNumberTest(){
        emptyMiddleBox.putNumber(4, 4, 1);
        Cell[][] box = emptyMiddleBox.getBox();
        Assert.assertEquals("Value of the middle cell:", 1, box[1][1].getValue());
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if(row != 1 || column != 1)
                    Assert.assertTrue("other cells ineligibles contains 1", box[row][column].getIneligibles().contains(1));
            }
        }
    }

    @Test
    public void getHiddenSinglesTest(){
        List<CellRecord> hiddenSingles = boxWithHiddenSingle.getHiddenSingles();
        Assert.assertEquals("# of hidden single numbers", 1, hiddenSingles.size());
        Assert.assertEquals("The value of the hidden single", 1, hiddenSingles.get(0).value());
    }

    @Test
    public void getNakedPairsTest(){
        List<Cell[]> nakedPairs = boxWithNakedPair.getNakedPairs();
        Assert.assertEquals("# of naked pairs: ", 1, nakedPairs.size());
        Cell cell1 = nakedPairs.get(0)[0];
        Cell cell2 = nakedPairs.get(0)[1];
        Assert.assertEquals("# of options for the First cell", 2, cell1.getOptions().size());
        Assert.assertEquals("# of options for the Second cell", 2, cell2.getOptions().size());
        Assert.assertTrue("First cell contains 1", cell1.getOptions().contains(1));
        Assert.assertTrue("First cell contains 9", cell1.getOptions().contains(9));
        Assert.assertTrue("Second cell contains 1", cell2.getOptions().contains(1));
        Assert.assertTrue("Second cell contains 9", cell2.getOptions().contains(9));
        Cell[][] box = boxWithNakedPair.getBox();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if(box[row][column] != cell1 && box[row][column] != cell2){
                    Assert.assertTrue("Other cells not contains 1", box[row][column].getIneligibles().contains(1));
                    Assert.assertTrue("Other cells not contains 9", box[row][column].getIneligibles().contains(9));
                }
            }
        }
    }

    @Test
    public void getHiddenPairsTest(){
        Cell[][] box = boxWithHiddenPair.getBox();
        Assert.assertEquals("# of options for the First cell before looking for hidden pair", 9, box[0][0].getOptions().size());
        Assert.assertEquals("# of options for the Second cell before looking for hidden pair", 9, box[2][2].getOptions().size());
        List<Cell[]> hiddenPairs = boxWithHiddenPair.getHiddenPairs();
        Assert.assertEquals("# of hidden pairs: ", 1, hiddenPairs.size());
        Cell cell1 = hiddenPairs.get(0)[0];
        Cell cell2 = hiddenPairs.get(0)[1];
        Assert.assertEquals("First cell has only 2 options", 2, cell1.getOptions().size());
        Assert.assertEquals("Second cell has only 2 options", 2, cell2.getOptions().size());
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if(box[row][column] != cell1 && box[row][column] != cell2){
                    Assert.assertTrue("Other cells not contains 4", box[row][column].getIneligibles().contains(1));
                    Assert.assertTrue("Other cells not contains 4", box[row][column].getIneligibles().contains(9));
                }
            }
        }
    }

    @Test
    public void getNakedTripleTest(){
        List<Cell[]> nakedTriples = boxWithNakedTriple.getNakedTriple();
        Assert.assertEquals("# of naked triple", 1, nakedTriples.size());
        Cell cell1 = nakedTriples.get(0)[0];
        Cell cell2 = nakedTriples.get(0)[1];
        Cell cell3 = nakedTriples.get(0)[2];
        Assert.assertTrue("First cell has up to 3 options", cell1.getOptions().size() <= 3);
        Assert.assertTrue("Second cell has up to 3 options", cell2.getOptions().size() <= 3);
        Assert.assertTrue("Third cell has up to 3 options", cell3.getOptions().size() <= 3);
        Cell[][] box = boxWithNakedTriple.getBox();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if(box[row][column] != cell1 && box[row][column] != cell2 && box[row][column] != cell3) {
                    Assert.assertTrue("Other cells not contains 4", box[row][column].getIneligibles().contains(4));
                    Assert.assertTrue("Other cells not contains 6", box[row][column].getIneligibles().contains(6));
                    Assert.assertTrue("Other cells not contains 8", box[row][column].getIneligibles().contains(8));
                }
            }
        }
    }

    @Test
    public void getNakedQuadTest(){
        List<Cell[]> nakedQuads = boxWithNakedQuad.getNakedQuad();
        Assert.assertEquals("# of naked quad", 1, nakedQuads.size());
        Cell cell1 = nakedQuads.get(0)[0];
        Cell cell2 = nakedQuads.get(0)[1];
        Cell cell3 = nakedQuads.get(0)[2];
        Cell cell4 = nakedQuads.get(0)[3];
        Assert.assertTrue("First cell has up to 4 options", cell1.getOptions().size() <= 4);
        Assert.assertTrue("Second cell has up to 4 options", cell2.getOptions().size() <= 4);
        Assert.assertTrue("Third cell has up to 4 options", cell3.getOptions().size() <= 4);
        Assert.assertTrue("Forth cell has up to 4 options", cell4.getOptions().size() <= 4);
        Cell[][] box = boxWithNakedQuad.getBox();
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                if(box[row][column] != cell1 && box[row][column] != cell2 && box[row][column] != cell3 && box[row][column] != cell4) {
                    Assert.assertTrue("Other cells not contains 2", box[row][column].getIneligibles().contains(2));
                    Assert.assertTrue("Other cells not contains 6", box[row][column].getIneligibles().contains(6));
                    Assert.assertTrue("Other cells not contains 8", box[row][column].getIneligibles().contains(8));
                    Assert.assertTrue("Other cells not contains 9", box[row][column].getIneligibles().contains(9));
                }
            }
        }
    }

    @Test
    public void lookForVerticalPointingPairTest(){
        List<PointingPairRecord> pointingPairs = boxWithVerticalPointingPair.lookForVerticalPointingPair();
        Assert.assertEquals("# of pointing pairs", 1, pointingPairs.size());
        Assert.assertEquals("The pointing number", 9, pointingPairs.get(0).value());
        Assert.assertEquals("The pointing number column", 4, pointingPairs.get(0).line());
    }

    @Test
    public void lookForHorizontalPointingPairTest(){
        List<PointingPairRecord> pointingPairs = boxWithHorizontalPointingPair.lookForHorizontalPointingPair();
        Assert.assertEquals("# of pointing pairs", 1, pointingPairs.size());
        Assert.assertEquals("The pointing number", 9, pointingPairs.get(0).value());
        Assert.assertEquals("The pointing number column", 7, pointingPairs.get(0).line());
    }
}
