package net.sf.openrocket.flightcomputer;

/**
 * Created by chris on 8/17/15.
 */
public interface IControllable {
    void setControl(double u);
    double getControl();
}
