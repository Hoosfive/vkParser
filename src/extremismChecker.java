import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

public class extremismChecker implements Callable {
    private String _materialName;
    private String _recordNum = "-1";

    extremismChecker(String materialName) {
        _materialName = materialName;
    }

    @Override
    public String call() {
        try {
            int resultsCount = 0;
            Workbook workbook = WorkbookFactory.create(new File("D:\\export2.xls"));
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getCell(1).toString().toLowerCase().contains(_materialName.toLowerCase())) {
                    _recordNum = row.getCell(0).getStringCellValue();
                    resultsCount++;
                }
                if (resultsCount > 1) {
                    _recordNum = "-1";
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return _recordNum;
    }
}