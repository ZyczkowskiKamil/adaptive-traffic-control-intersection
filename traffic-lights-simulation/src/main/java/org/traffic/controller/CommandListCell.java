package org.traffic.controller;

import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import org.traffic.model.dto.Command;

public class CommandListCell extends ListCell<Command> {
    private final HBox hbox = new HBox();
    private final Label commandTypeLabel = new Label();
    private final Label commandDetailsLabel = new Label();
    private final Button removeBtn = new Button("Remove");

    public CommandListCell(ObservableList<Command> commandList) {
        super();

        removeBtn.setOnAction(e -> {
            Command command = getItem();
            commandList.remove(command);
        });

        hbox.getChildren().addAll(commandTypeLabel, commandDetailsLabel, removeBtn);
    }

    @Override
    protected void updateItem(Command command, boolean empty) {
        super.updateItem(command, empty);

        if (empty || command == null) {
            setText(null);
            setGraphic(null);
        } else {
            commandTypeLabel.setText(command.type().toString());

            switch (command.type()) {
                case STEP -> commandDetailsLabel.setText("");
                case ADD_VEHICLE -> commandDetailsLabel.setText(command.vehicleId() + " " + command.startRoad() + " " + command.endRoad());
            }

            hbox.getChildren().setAll(commandTypeLabel, commandDetailsLabel, removeBtn);

            setGraphic(hbox);
        }
    }
}
