package v2;

import java.util.List;

public class Column extends Line {
    public Column(int lineIndex, int size, Cell[] line, List<EliminatedRecord> eliminatedRecords) {
        super(lineIndex, size, line, eliminatedRecords);
    }

    @Override
    protected int getRelevantIndex(Cell cell) {
        return cell.getRow();
    }
}
