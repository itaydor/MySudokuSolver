package v2;

import java.util.List;

public class Row extends Line {

    public Row(int lineIndex, int size, Cell[] line, List<EliminatedRecord> eliminatedRecords) {
        super(lineIndex, size, line, eliminatedRecords);
    }

    @Override
    protected int getRelevantIndex(Cell cell) {
        return cell.getColumn();
    }
}
