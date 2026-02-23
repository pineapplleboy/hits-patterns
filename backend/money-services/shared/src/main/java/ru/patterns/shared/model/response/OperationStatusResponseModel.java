package ru.patterns.shared.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.patterns.shared.model.enums.OperationStatus;

@Data
@AllArgsConstructor
public class OperationStatusResponseModel {

    private OperationStatus status;
}
