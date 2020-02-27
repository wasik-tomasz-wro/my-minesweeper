package com.company;

import com.trolltech.qt.core.QPoint;
import com.trolltech.qt.core.QSettings;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.*;

import java.util.ArrayList;
import java.util.List;

public class BestResultsDialog extends QDialog
{
    private static final int ELAPSED_TIME_COLUMN = 1;

    QVBoxLayout contentLayout = new QVBoxLayout(this);

    QHBoxLayout horizontalLayout1 = new QHBoxLayout(this);
    QHBoxLayout horizontalLayout2 = new QHBoxLayout(this);

    QLabel bestResultsLabel = new QLabel(tr("Best results for individual game modes"), this);
    String informationText = tr("Up to seven best results for each game mode are saved here. Click \"Clear table\" button to remove them from the table.");
    QLabel informationLabel = new QLabel(informationText, this);

    QPushButton clearTablePushButton = new QPushButton(tr("Clear table"), this);
    QPushButton closePushButton = new QPushButton(tr("Close"), this);

    QTreeView bestResultsTreeView = new QTreeView(this);

    // data model elements:
    QStandardItemModel bestResultsItemModel = new QStandardItemModel(this);

    QStandardItem basicModeItem = new QStandardItem();
    QStandardItem mediumModeItem = new QStandardItem();
    QStandardItem advancedModeItem = new QStandardItem();
    QStandardItem expertModeItem = new QStandardItem();

    ArrayList<QStandardItem> person = new ArrayList<QStandardItem>();


    BestResultsDialog()
    {
        setWindowTitle(tr("Best results - MyMinesweeper"));

        packOnLayouts();
        connectToSlots();
        loadBestResults();
    }

    /*
        This method places all widgets on layouts  and sets a layout for a window
     */
    private void packOnLayouts()
    {
        // "Best results" label
        QSizePolicy sizePolicy = new QSizePolicy(QSizePolicy.Policy.Expanding, QSizePolicy.Policy.Preferred);
        byte b = 0;
        sizePolicy.setHorizontalStretch(b);
        sizePolicy.setVerticalStretch(b);
        sizePolicy.setHeightForWidth(bestResultsLabel.sizePolicy().hasHeightForWidth());
        bestResultsLabel.setSizePolicy(sizePolicy);
        bestResultsLabel.setStyleSheet("background-color: rgb(175, 191, 250); padding: 3px; color: black;");
        contentLayout.addWidget(bestResultsLabel);

        // Layout with information label and "Clear table" button
        informationLabel.setSizePolicy(sizePolicy);
        informationLabel.setWordWrap(true);
        horizontalLayout1.addWidget(informationLabel);
        QSizePolicy sizePolicy1 = new QSizePolicy(QSizePolicy.Policy.Minimum, QSizePolicy.Policy.Minimum);
        sizePolicy1.setHorizontalStretch(b);
        sizePolicy1.setVerticalStretch(b);
        sizePolicy1.setHeightForWidth(clearTablePushButton.sizePolicy().hasHeightForWidth());
        clearTablePushButton.setSizePolicy(sizePolicy1);

        horizontalLayout1.addWidget(clearTablePushButton);
        contentLayout.addLayout(horizontalLayout1);

        // TreeView with best results list
        contentLayout.addWidget(bestResultsTreeView);

        // Horizontal line
        QFrame horizontalLine = new QFrame(this);
        horizontalLine.setFrameShape(QFrame.Shape.HLine);
        horizontalLine.setFrameShadow(QFrame.Shadow.Plain);
        contentLayout.addWidget(horizontalLine);

        // "Close" button with stretch on the left side
        horizontalLayout2.addStretch();
        horizontalLayout2.addWidget(closePushButton);
        contentLayout.addLayout(horizontalLayout2);

        this.setLayout(contentLayout);
    }

    private void loadBestResults()
    {
        bestResultsItemModel.clear();

        // Set number of view columns and names for columns
        bestResultsItemModel.setColumnCount(3);
        bestResultsItemModel.setHeaderData(0, Qt.Orientation.Horizontal, tr("Name"), Qt.ItemDataRole.EditRole);
        bestResultsItemModel.setHeaderData(1, Qt.Orientation.Horizontal, tr("Elapsed time"), Qt.ItemDataRole.EditRole);
        bestResultsItemModel.setHeaderData(2, Qt.Orientation.Horizontal, tr("Date \\ time"), Qt.ItemDataRole.EditRole);

        QSettings settings = new QSettings("tw", "MyMinesweeper");

        settings.beginGroup("best_results");
        List<String> gameModeGroups = settings.childGroups();
        settings.endGroup();

        for ( int i = 0; i < gameModeGroups.size(); i++ )
        {
            QStandardItem gameModeItem = new QStandardItem(gameModeGroups.get(i));
            bestResultsItemModel.setItem(i, gameModeItem);

            settings.beginGroup("best_results/" + gameModeGroups.get(i));
            List<String> keys = settings.childKeys();
            ArrayList<QStandardItem> resultDetailsItems = new ArrayList<QStandardItem>();
            for ( String key : keys )
            {
                resultDetailsItems.clear();
                ArrayList<String> resultDetails = (ArrayList<String>) settings.value(key);
                for ( String detail : resultDetails )
                {
                    resultDetailsItems.add(new QStandardItem(detail));
                }
                resultDetailsItems.add(new QStandardItem(key));
                gameModeItem.appendRow(resultDetailsItems);
            }
            settings.endGroup();
        }
        bestResultsTreeView.setModel(bestResultsItemModel);
        bestResultsTreeView.expandAll();
        bestResultsTreeView.setEditTriggers(QAbstractItemView.EditTrigger.NoEditTriggers);
        bestResultsTreeView.setAlternatingRowColors(true);
        bestResultsTreeView.sortByColumn(ELAPSED_TIME_COLUMN, Qt.SortOrder.AscendingOrder);

        for (int i = 0; i < bestResultsItemModel.columnCount(); i++)
        {
            bestResultsTreeView.resizeColumnToContents(i);
        }
    }

    private void connectToSlots()
    {
        clearTablePushButton.clicked.connect(this, "clearTablePushButtonClicked()");
        closePushButton.clicked.connect(this, "closePushButtonClicked()");
    }

    // slots
    private void clearTablePushButtonClicked()
    {
        QSettings settings = new QSettings("tw", "MyMinesweeper");
        settings.remove("best_results");
        loadBestResults();
    }

    private void closePushButtonClicked()
    {
        this.close();
    }
}