package entity;

public class LocationDetails {
    private int shelfNumber;
    private int rowNumber;
    private int columnNumber;

    public int getShelfNumber() {
        return shelfNumber;
    }

    public void setShelfNumber(int shelfNumber) {
        this.shelfNumber = shelfNumber;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }

    public int getColumnNumber() {
        return columnNumber;
    }

    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    @Override
    public String toString() {
        return "LocationDetails{" +
                "shelfNumber=" + shelfNumber +
                ", rowNumber=" + rowNumber +
                ", columnNumber=" + columnNumber +
                '}';
    }
}
