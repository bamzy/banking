package ir.samatco.eft.tms.entity;

/**
 * Created by Bamdad Aghili on 4/24/2014.
 */
public class NextVisit {
    private Long nextVisit;
    private Long terminalNumber;
    private Long weight;

    public Long getWeight() {
        return weight;
    }

    public void setWeight(Long weight) {
        this.weight = weight;
    }


    public Long getNextVisit() {
        return nextVisit;
    }

    public void setNextVisit(Long nextVisit) {
        this.nextVisit = nextVisit;
    }

    public Long getTerminalNumber() {
        return terminalNumber;
    }

    public void setTerminalNumber(Long terminalNumber) {
        this.terminalNumber = terminalNumber;
    }
}
