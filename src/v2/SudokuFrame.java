package v2;

import java.util.List;

public interface SudokuFrame {

    /**
     * A cell with only one candidate is called a Naked Single.
     * The sole candidate is the solution to the cell.
     * All other appearances of the same candidate, if any, are eliminated if they can be
     * seen by the Single.
     * The method will update the Frame!
     * @return List<Cell> all naked single (after update)
     */
    List<CellRecord> getNakedSingles();

    /**
     * A cell with multiple candidates is called a Hidden Single if one of the candidate
     * is the only candidate in a line, or a box.
     * The single candidate is the solution to the cell.
     * All other appearances of the same candidate, if any, are eliminated if they can be
     * seen by the Single.
     * The method will update the Frame!
     * @return List<Cell> all the hidden singles (after update)
     */
    List<CellRecord> getHiddenSingles();

    /**
     * Two cells in a line, or a block having only the same pair of candidates are
     * called a Naked Pair.
     * All other appearances of the two candidates in the same line, or box can be
     * eliminated.
     * The method will update the Frame!
     * @return List<Cell[]> list of naked pairs
     */
    List<Cell[]> getNakedPairs();

    /**
     * When a pair of candidates appears in only two cells in a line, or a box,
     * but they aren't the only candidates in the cells, they are called a Hidden Pair.
     * All candidates other than the pair in the cells can be eliminated, yielding a
     * Naked Pair.
     * The method will update the Frame!
     * @return List<Cell[]> list of hidden pairs
     */
    List<Cell[]> getHiddenPairs();

    /**
     * Three cells in a line, or a box, having only the same three candidates,
     * or their subset, are called a Naked Triple.
     * All other appearances of the same candidates can be eliminated if they are in the same line
     * or box.
     * The method will update the Frame!
     * @return List<Cell[]> list of naked triple
     */
    List<Cell[]> getNakedTriple();

    /**
     * Four cells in a line, or a box, having only the same four candidates,
     * or their subset, are called a Naked Quad.
     * All other appearances of the same candidates can be eliminated if they are in the same line
     * or box.
     * The method will update the Line!
     * @return List<Cell[]> list of naked triple
     */
    List<Cell[]> getNakedQuad();
}
