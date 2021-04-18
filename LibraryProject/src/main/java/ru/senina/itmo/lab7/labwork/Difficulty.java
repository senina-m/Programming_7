package ru.senina.itmo.lab7.labwork;

/**
 * Description how difficult is something
 * @see LabWork
 */
public enum Difficulty {
    VERY_EASY(0),
    NORMAL(1),
    VERY_HARD(2),
    INSANE(3),
    HOPELESS(4);

    private final int value;

    Difficulty(int value){
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}