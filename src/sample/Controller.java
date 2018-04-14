package sample;

import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class Controller implements Initializable {
    private DEModel deModel;

    @FXML
    private LineChart<Number, Number> lineChartMain, lineCharTrunc;
    @FXML
    private TextField textFieldMainX0, textFieldMainY0, textFieldMainX, textFieldMainN, textFieldTruncN0, textFieldTruncNI;
    @FXML
    private CheckBox checkBoxMainEuler, checkBoxMainExact, checkBoxMainIe, checkBoxMainRk, checkBoxTruncEuler, checkBoxTruncIe, checkBoxTruncRk;
    @FXML
    private AnchorPane paneMain, paneTrunc;
    @FXML
    private Button buttonMainSolve, buttonTruncSolve;
    @FXML
    private VBox vBoxTrunc;


    // Initialize views with default values
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lineChartMain.setCreateSymbols(false);
        lineCharTrunc.setCreateSymbols(false);
        textFieldMainX0.setText("0.0");
        textFieldMainY0.setText("0.0");
        textFieldMainX.setText("5.0");
        textFieldMainN.setText("100");
        textFieldTruncN0.setText("10");
        textFieldTruncNI.setText("100");
    }

    // onClick for buttonMainView, it hide elements from Truncation error scene and make visible main scene
    @FXML
    private void mainView() throws IOException {
        paneTrunc.setVisible(false);
        lineCharTrunc.setVisible(false);
        vBoxTrunc.setVisible(false);

        paneMain.setVisible(true);
        buttonMainSolve.setVisible(true);
        buttonTruncSolve.setVisible(false);
    }

    // OnClick for buttonMainSolve, this function update main chart with values from different fields
    @FXML
    private void updateMainChartFromFields() {
        double x0 = Double.parseDouble(textFieldMainX0.getText());
        double y0 = Double.parseDouble(textFieldMainY0.getText());
        double X = Double.parseDouble(textFieldMainX.getText());
        int N = Integer.parseInt(textFieldMainN.getText());
        deModel = new DEModel(x0, y0, X, N);
        List<XYChart.Series> plots = deModel.getSolutions(checkBoxMainExact.isSelected()
                , checkBoxMainEuler.isSelected()
                , checkBoxMainIe.isSelected()
                , checkBoxMainRk.isSelected());
        updateMainChart(plots);
    }

    private void updateMainChart(List<XYChart.Series> plots) {
        lineChartMain.getData().clear();
        for (int i = 0; i < 4; i++) {
            if (plots.get(i) != null)
                lineChartMain.getData().add(plots.get(i));
        }
    }

    // onClick for buttonTruncView, it hide elements from paneMain (Main scene) and make visible elements of Truncation error scene
    @FXML
    private void truncView() throws IOException {
        paneMain.setVisible(false);
        buttonMainSolve.setVisible(false);

        paneTrunc.setVisible(true);
        lineCharTrunc.setVisible(true);
        vBoxTrunc.setVisible(true);
        buttonTruncSolve.setVisible(true);
    }

    // OnClick for buttonTruncSolve, this function update truncation error chart with values from different fields
    @FXML
    private void updateTruncChartFromFields() {
        double x0 = Double.parseDouble(textFieldMainX0.getText());
        double y0 = Double.parseDouble(textFieldMainY0.getText());
        double X = Double.parseDouble(textFieldMainX.getText());
        int N = Integer.parseInt(textFieldMainN.getText());
        deModel = new DEModel(x0, y0, X, N);
        int n0 = Integer.parseInt(textFieldTruncN0.getText());
        int ni = Integer.parseInt(textFieldTruncNI.getText());
        List<XYChart.Series> plots = deModel.getTrunc(checkBoxTruncEuler.isSelected()
                , checkBoxTruncIe.isSelected()
                , checkBoxTruncRk.isSelected()
                , n0
                , ni);
        updateTruncChart(plots);
    }

    private void updateTruncChart(List<XYChart.Series> plots) {
        lineCharTrunc.getData().clear();
        lineCharTrunc.getXAxis().setLabel("N");
        for (int i = 0; i < 3; i++) {
            if (plots.get(i) != null) {
                lineCharTrunc.getData().add(plots.get(i));
            }
        }
    }
}




