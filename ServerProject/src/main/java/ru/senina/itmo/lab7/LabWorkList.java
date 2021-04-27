package ru.senina.itmo.lab7;

import lombok.Getter;
import lombok.Setter;
import ru.senina.itmo.lab7.labwork.LabWork;

import java.util.List;

@Getter @Setter
public class LabWorkList {
    private int numberOfCollectionElements;
    private List<LabWork> labWorkList;

    public LabWorkList(List<LabWork> labWorkList) {
        this.labWorkList = labWorkList;
    }

    public LabWorkList() {
    }

    public int getNumberOfCollectionElements() {
        return labWorkList.size();
    }
}
